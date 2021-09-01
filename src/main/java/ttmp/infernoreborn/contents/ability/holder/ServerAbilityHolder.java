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
import ttmp.infernoreborn.capability.Caps;
import ttmp.infernoreborn.contents.Abilities;
import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.contents.ability.AbilitySkill;
import ttmp.infernoreborn.contents.ability.OnAbilityEvent;
import ttmp.infernoreborn.contents.ability.OnAbilityUpdate;
import ttmp.infernoreborn.contents.ability.cooldown.Cooldown;
import ttmp.infernoreborn.contents.ability.cooldown.ServerCooldown;
import ttmp.infernoreborn.infernaltype.InfernalType;
import ttmp.infernoreborn.infernaltype.InfernalTypes;
import ttmp.infernoreborn.network.ModNet;
import ttmp.infernoreborn.network.SyncAbilityHolderMsg;
import ttmp.infernoreborn.util.LazyPopulatedList;
import ttmp.infernoreborn.util.StupidUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

public class ServerAbilityHolder implements AbilityHolder, ICapabilitySerializable<CompoundNBT>{
	@Nullable
	public static ServerAbilityHolder of(ICapabilityProvider provider){
		AbilityHolder of = AbilityHolder.of(provider);
		return of instanceof ServerAbilityHolder ? (ServerAbilityHolder)of : null;
	}

	private final Set<Ability> abilities = new LinkedHashSet<>();
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

	@Nullable private InfernalType appliedInfernalType;

	private boolean generateAbility = true;

	private final ServerCooldown cooldown = new ServerCooldown();

	@Override public Set<Ability> getAbilities(){
		return abilitiesView;
	}
	@Override public boolean has(Ability ability){
		return this.abilities.contains(ability);
	}
	@Override public boolean add(Ability ability){
		if(!this.abilities.add(ability)) return false;
		if(!removedAbilities.remove(ability)) addedAbilities.add(ability);
		return true;
	}
	@Override public boolean remove(Ability ability){
		if(!this.abilities.remove(ability)) return false;
		if(!addedAbilities.remove(ability)) removedAbilities.add(ability);
		return true;
	}
	@Override public void clear(){
		if(this.abilities.isEmpty()) return;
		this.addedAbilities.clear();
		this.removedAbilities.addAll(this.abilities);
		this.abilities.clear();
		this.appliedInfernalType = null;
	}

	@Nullable public InfernalType getAppliedInfernalType(){
		return appliedInfernalType;
	}
	public void setAppliedInfernalType(@Nullable InfernalType appliedInfernalType){
		this.appliedInfernalType = appliedInfernalType;
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
			if(entity instanceof IMob){
				clear();
				InfernalTypes.generate(entity, this);
			}
			generateAbility = false;
		}

		if(!addedAbilities.isEmpty()||!removedAbilities.isEmpty()){
			float maxHealth = entity.getMaxHealth();

			for(Ability ability : addedAbilities) onAbilityAdded(ability, entity);
			for(Ability ability : removedAbilities) onAbilityRemoved(ability, entity);
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
		cooldown.decreaseAll(1);
		if(cooldown.getCastingSkill()!=null){
			cooldown.setCastTime(cooldown.getCastTime()-1);
			if(!cooldown.hasCastTime()){
				triggerSkillEffect(cooldown.getCastingSkill(), entity, true);
				cooldown.setCastingSkill(null);
			}
		}
		if(!cooldown.hasGlobalDelay()&&!abilitySkills.isEmpty()&&cooldown.getCastingSkill()==null){
			AbilitySkill[] validSkills = this.abilitySkills.stream()
					.filter(abilitySkill -> !cooldown.has(abilitySkill.getCooldownTicket())&&
							(abilitySkill.getSkillCondition()==null||abilitySkill.getSkillCondition().useSkill(entity, this)))
					.toArray(AbilitySkill[]::new);
			if(validSkills.length>0){
				startCastSkill(validSkills[entity.getRandom().nextInt(validSkills.length)], entity);
			}
		}
		if(entity.isAlive()){
			for(OnAbilityUpdate e : onUpdateListeners)
				e.onUpdate(entity, this);
		}
	}

	public boolean triggerSkillEffect(AbilitySkill skill, LivingEntity entity, boolean applyCooldownOnSuccess){
		if(!entity.isAlive()||!skill.getSkillAction().useSkill(entity, this)) return false;
		if(applyCooldownOnSuccess){
			cooldown.set(skill.getCooldownTicket(), skill.getCooldown());
			cooldown.setGlobalDelay(Math.max(cooldown.getGlobalDelay(), 10));
		}
		return true;
	}

	public void startCastSkill(AbilitySkill skill, LivingEntity entity){
		if(cooldown.getCastingSkill()!=null) return;
		if(skill.getCastTime()<=0){
			triggerSkillEffect(skill, entity, true);
		}else{
			cooldown.setCastingSkill(skill);
			cooldown.setCastTime(skill.getCastTime());
		}
	}

	public void syncAbilityToClient(LivingEntity entity){
		ModNet.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity),
				new SyncAbilityHolderMsg(entity.getId(),
						abilities,
						appliedInfernalType!=null&&appliedInfernalType.getSpecialEffect()!=null ? appliedInfernalType : null));
	}

	@Override public Cooldown cooldown(){
		return cooldown;
	}

	protected void onAbilityAdded(Ability ability, LivingEntity entity){
		for(Entry<Attribute, Collection<AttributeModifier>> entry : ability.getAttributes().asMap().entrySet()){
			ModifiableAttributeInstance instance = entity.getAttributes().getInstance(entry.getKey());
			if(instance==null) continue;
			for(AttributeModifier m : entry.getValue())
				instance.addTransientModifier(m);
		}
	}

	protected void onAbilityRemoved(Ability ability, LivingEntity entity){
		for(Entry<Attribute, Collection<AttributeModifier>> entry : ability.getAttributes().asMap().entrySet()){
			ModifiableAttributeInstance instance = entity.getAttributes().getInstance(entry.getKey());
			if(instance==null) continue;
			for(AttributeModifier m : entry.getValue())
				instance.removeModifier(m.getId());
		}
	}

	private final LazyOptional<AbilityHolder> self = LazyOptional.of(() -> this);

	@Nonnull @Override public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side){
		return cap==Caps.abilityHolder ? self.cast() : LazyOptional.empty();
	}

	@Override
	public CompoundNBT serializeNBT(){
		CompoundNBT nbt = new CompoundNBT();
		if(!abilities.isEmpty()) nbt.put("abilities", StupidUtils.writeToNbt(abilities, Abilities.getRegistry()));
		if(this.generateAbility) nbt.putBoolean("generateAbility", true);
		if(appliedInfernalType!=null) nbt.putString("appliedGeneratorScheme", appliedInfernalType.getId().toString());
		this.cooldown.save(nbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt){
		this.clear();

		ListNBT abilities = nbt.getList("abilities", Constants.NBT.TAG_STRING);
		StupidUtils.read(abilities, Abilities.getRegistry(), this::add);
		this.generateAbility = nbt.getBoolean("generateAbility");
		this.appliedInfernalType = nbt.contains("appliedGeneratorScheme", Constants.NBT.TAG_STRING) ?
				InfernalTypes.get(new ResourceLocation(nbt.getString("appliedGeneratorScheme"))) :
				null;
		this.cooldown.load(nbt);
	}
}
