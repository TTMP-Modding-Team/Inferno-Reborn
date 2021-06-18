package ttmp.infernoreborn.contents.ability.holder;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.contents.ability.AbilitySkill;
import ttmp.infernoreborn.contents.ability.OnAbilityEvent;
import ttmp.infernoreborn.contents.ability.OnAbilityUpdate;
import ttmp.infernoreborn.contents.ability.ServerSkillCastingState;
import ttmp.infernoreborn.contents.ability.SkillCastingState;
import ttmp.infernoreborn.contents.ability.SkillCastingStateProvider;
import ttmp.infernoreborn.contents.ability.generator.AbilityGenerator;
import ttmp.infernoreborn.contents.ability.generator.AbilityGenerators;
import ttmp.infernoreborn.contents.ability.generator.scheme.AbilityGeneratorScheme;
import ttmp.infernoreborn.capability.Caps;
import ttmp.infernoreborn.contents.Abilities;
import ttmp.infernoreborn.network.ModNet;
import ttmp.infernoreborn.network.SyncAbilityHolderMsg;
import ttmp.infernoreborn.util.LazyPopulatedList;
import ttmp.infernoreborn.util.StupidUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

public class ServerAbilityHolder implements AbilityHolder, ICapabilitySerializable<CompoundNBT>, SkillCastingStateProvider{
	@Nullable
	public static ServerAbilityHolder of(ICapabilityProvider provider){
		AbilityHolder of = AbilityHolder.of(provider);
		return of instanceof ServerAbilityHolder ? (ServerAbilityHolder)of : null;
	}

	private final Set<Ability> abilities = new HashSet<>();
	private final Set<Ability> addedAbilities = new HashSet<>();
	private final Set<Ability> removedAbilities = new HashSet<>();
	private final Set<Ability> abilitiesView = Collections.unmodifiableSet(abilities);

	public final LazyPopulatedList<Ability, OnAbilityEvent<LivingAttackEvent>> onAttackedListeners = new LazyPopulatedList<Ability, OnAbilityEvent<LivingAttackEvent>>(abilities){
		@Override protected void populate(Ability o, ImmutableList.Builder<OnAbilityEvent<LivingAttackEvent>> b){
			if(o.onAttacked()!=null) b.add(Objects.requireNonNull(o.onAttacked()));
		}
	};
	public final LazyPopulatedList<Ability, OnAbilityEvent<LivingHurtEvent>> onHurtListeners = new LazyPopulatedList<Ability, OnAbilityEvent<LivingHurtEvent>>(abilities){
		@Override protected void populate(Ability o, ImmutableList.Builder<OnAbilityEvent<LivingHurtEvent>> b){
			if(o.onHurt()!=null) b.add(Objects.requireNonNull(o.onHurt()));
		}
	};
	public final LazyPopulatedList<Ability, OnAbilityEvent<LivingHurtEvent>> onHitListeners = new LazyPopulatedList<Ability, OnAbilityEvent<LivingHurtEvent>>(abilities){
		@Override protected void populate(Ability o, ImmutableList.Builder<OnAbilityEvent<LivingHurtEvent>> b){
			if(o.onHit()!=null) b.add(Objects.requireNonNull(o.onHit()));
		}
	};
	public final LazyPopulatedList<Ability, OnAbilityEvent<LivingDeathEvent>> onDeathListeners = new LazyPopulatedList<Ability, OnAbilityEvent<LivingDeathEvent>>(abilities){
		@Override protected void populate(Ability o, ImmutableList.Builder<OnAbilityEvent<LivingDeathEvent>> b){
			if(o.onDeath()!=null) b.add(Objects.requireNonNull(o.onDeath()));
		}
	};
	public final LazyPopulatedList<Ability, OnAbilityUpdate> onUpdateListeners = new LazyPopulatedList<Ability, OnAbilityUpdate>(abilities){
		@Override protected void populate(Ability o, ImmutableList.Builder<OnAbilityUpdate> b){
			if(o.onUpdate()!=null) b.add(Objects.requireNonNull(o.onUpdate()));
		}
	};
	public final LazyPopulatedList<Ability, AbilitySkill> abilitySkills = new LazyPopulatedList<Ability, AbilitySkill>(abilities){
		@Override protected void populate(Ability o, ImmutableList.Builder<AbilitySkill> b){
			b.addAll(o.getSkills());
		}
	};

	@Nullable private AbilityGeneratorScheme appliedGeneratorScheme;

	private boolean generateAbility = true;

	private final ServerSkillCastingState skillCastingState = new ServerSkillCastingState(this);

	@Override public Set<Ability> getAbilities(){
		return abilitiesView;
	}
	@Override public boolean has(Ability ability){
		return this.abilities.contains(ability);
	}
	@Override public boolean add(Ability ability){
		return !this.abilities.contains(ability)&&addedAbilities.add(ability);
	}
	@Override public boolean remove(Ability ability){
		return this.abilities.contains(ability)&&removedAbilities.add(ability);
	}
	@Override public void clear(){
		if(this.abilities.isEmpty()) return;
		this.removedAbilities.addAll(this.abilities);
		this.appliedGeneratorScheme = null;
	}

	@Nullable public AbilityGeneratorScheme getAppliedGeneratorScheme(){
		return appliedGeneratorScheme;
	}
	public void setAppliedGeneratorScheme(@Nullable AbilityGeneratorScheme appliedGeneratorScheme){
		this.appliedGeneratorScheme = appliedGeneratorScheme;
	}

	public boolean generateAbility(){
		return generateAbility;
	}
	public void setGenerateAbility(boolean generateAbility){
		this.generateAbility = generateAbility;
	}

	@Override
	public void update(LivingEntity entity){
		if(generateAbility){
			if(entity instanceof IMob) generate(entity, null);
			generateAbility = false;
		}

		if(!addedAbilities.isEmpty()||!removedAbilities.isEmpty()){
			float maxHealth = entity.getMaxHealth();

			for(Ability ability : addedAbilities){
				if(abilities.add(ability))
					onAbilityAdded(ability, entity);
			}
			for(Ability ability : removedAbilities){
				if(abilities.remove(ability))
					onAbilityRemoved(ability, entity);
			}
			addedAbilities.clear();
			removedAbilities.clear();

			onAttackedListeners.sync();
			onHurtListeners.sync();
			onHitListeners.sync();
			onDeathListeners.sync();
			onUpdateListeners.sync();
			abilitySkills.sync();

			float newMaxHealth = entity.getMaxHealth();
			if(Float.compare(maxHealth, newMaxHealth)!=0){
				entity.setHealth(MathHelper.clamp(
						maxHealth<newMaxHealth ?
								entity.getHealth()+(newMaxHealth-maxHealth) :
								entity.getHealth(),
						1, newMaxHealth));
			}

			syncAbilityToClient(entity);
		}
		skillCastingState.update(entity);
		if(!abilitySkills.isEmpty()&&!skillCastingState.isCasting()){
			AbilitySkill[] validSkills = this.abilitySkills.stream()
					.filter(abilitySkill -> !skillCastingState.hasCooldown(abilitySkill)&&
							(abilitySkill.getSkillCondition()==null||abilitySkill.getSkillCondition().useSkill(entity, this)))
					.toArray(AbilitySkill[]::new);
			if(validSkills.length>0){
				skillCastingState.startCastSkill(validSkills[entity.getRandom().nextInt(validSkills.length)], entity);
			}
		}
		if(entity.isAlive()){
			for(OnAbilityUpdate e : onUpdateListeners)
				e.onUpdate(entity, this);
		}
	}

	@Override public SkillCastingState getSkillCastingState(){
		return skillCastingState;
	}

	public void syncAbilityToClient(LivingEntity entity){
		ModNet.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new SyncAbilityHolderMsg(entity.getId(), abilities, appliedGeneratorScheme));
	}

	public void generate(LivingEntity entity, @Nullable AbilityGenerator generator){
		clear();
		if(generator==null) generator = AbilityGenerators.getWeightedPool().nextItem(entity.getRandom());
		if(generator!=null) generator.generate(entity); // TODO target 체크 위로 밀어넣어야함
		this.appliedGeneratorScheme = generator!=null ? generator.getScheme() : null;
	}

	protected void onAbilityAdded(Ability ability, LivingEntity entity){
		for(Entry<Attribute, Set<AttributeModifier>> entry : ability.getAttributes().entrySet()){
			ModifiableAttributeInstance instance = entity.getAttributes().getInstance(entry.getKey());
			if(instance==null) continue;
			for(AttributeModifier m : entry.getValue())
				instance.addTransientModifier(m);
		}
	}

	protected void onAbilityRemoved(Ability ability, LivingEntity entity){
		for(Entry<Attribute, Set<AttributeModifier>> entry : ability.getAttributes().entrySet()){
			ModifiableAttributeInstance instance = entity.getAttributes().getInstance(entry.getKey());
			if(instance==null) continue;
			for(AttributeModifier m : entry.getValue())
				instance.removeModifier(m.getId());
		}
	}

	private final LazyOptional<AbilityHolder> self = LazyOptional.of(() -> this);

	@Override public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side){
		return cap==Caps.abilityHolder ? self.cast() : LazyOptional.empty();
	}

	@Override
	public CompoundNBT serializeNBT(){
		CompoundNBT nbt = new CompoundNBT();
		if(!abilities.isEmpty()) nbt.put("abilities", StupidUtils.writeToNbt(abilities, Abilities.getRegistry()));
		if(this.generateAbility) nbt.putBoolean("generateAbility", true);
		if(appliedGeneratorScheme!=null) nbt.putString("appliedGeneratorScheme", appliedGeneratorScheme.getId().toString());
		this.skillCastingState.save(nbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt){
		this.clear();

		ListNBT abilities = nbt.getList("abilities", Constants.NBT.TAG_STRING);
		StupidUtils.read(abilities, Abilities.getRegistry(), this::add);
		this.generateAbility = nbt.getBoolean("generateAbility");
		this.appliedGeneratorScheme = nbt.contains("appliedGeneratorScheme", Constants.NBT.TAG_STRING) ?
				AbilityGenerators.findSchemeWithId(new ResourceLocation(nbt.getString("appliedGeneratorScheme"))) :
				null;
		this.skillCastingState.load(nbt);
	}
}
