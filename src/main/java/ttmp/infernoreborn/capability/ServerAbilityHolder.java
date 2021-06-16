package ttmp.infernoreborn.capability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import ttmp.infernoreborn.ability.Ability;
import ttmp.infernoreborn.ability.AbilitySkill;
import ttmp.infernoreborn.ability.OnEvent;
import ttmp.infernoreborn.ability.generator.AbilityGenerator;
import ttmp.infernoreborn.ability.generator.AbilityGenerators;
import ttmp.infernoreborn.ability.generator.scheme.AbilityGeneratorScheme;
import ttmp.infernoreborn.contents.Abilities;
import ttmp.infernoreborn.network.ModNet;
import ttmp.infernoreborn.network.SyncAbilityHolderMsg;

import javax.annotation.Nullable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class ServerAbilityHolder extends AbilityHolder implements INBTSerializable<CompoundNBT>{
	@Nullable
	public static ServerAbilityHolder of(ICapabilityProvider provider){
		AbilityHolder of = AbilityHolder.of(provider);
		return of instanceof ServerAbilityHolder ? (ServerAbilityHolder)of : null;
	}

	private final Set<Ability> abilities = new HashSet<>();
	private final Set<Ability> addedAbilities = new HashSet<>();
	private final Set<Ability> removedAbilities = new HashSet<>();
	private final Set<Ability> abilitiesView = Collections.unmodifiableSet(abilities);

	private final Map<Ability, OnEvent<LivingHurtEvent>> onHurtListeners = new HashMap<>();
	private final Map<Ability, OnEvent<LivingHurtEvent>> onAttackListeners = new HashMap<>();
	private final Map<Ability, OnEvent<LivingDeathEvent>> onDeathListeners = new HashMap<>();
	private final Map<Ability, OnEvent<LivingUpdateEvent>> onUpdateListeners = new HashMap<>();
	private final Map<Ability, OnEvent<LivingHurtEvent>> onHurtListenersView = Collections.unmodifiableMap(onHurtListeners);
	private final Map<Ability, OnEvent<LivingHurtEvent>> onAttackListenersView = Collections.unmodifiableMap(onAttackListeners);
	private final Map<Ability, OnEvent<LivingDeathEvent>> onDeathListenersView = Collections.unmodifiableMap(onDeathListeners);
	private final Map<Ability, OnEvent<LivingUpdateEvent>> onUpdateListenersView = Collections.unmodifiableMap(onUpdateListeners);

	@Nullable private AbilityGeneratorScheme appliedGeneratorScheme;

	private boolean generateAbility = true;

	private final Map<AbilitySkill, Long> cooldownTime = new HashMap<>();
	private final Set<AbilitySkill> abilitySkills = new HashSet<>();
	private AbilitySkill queuedSkill = null;
	private long castingTime = 0;

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

	public Map<Ability, OnEvent<LivingHurtEvent>> getOnHurtListeners(){
		return onHurtListenersView;
	}
	public Map<Ability, OnEvent<LivingHurtEvent>> getOnAttackListeners(){
		return onAttackListenersView;
	}
	public Map<Ability, OnEvent<LivingDeathEvent>> getOnDeathListeners(){
		return onDeathListenersView;
	}
	public Map<Ability, OnEvent<LivingUpdateEvent>> getOnUpdateListeners(){
		return onUpdateListenersView;
	}

	public boolean isCasting(LivingEntity entity){
		return castingTime>entity.level.getGameTime();
	}
	public boolean tryUseSkill(LivingEntity entity){
		if(!isCasting(entity)){
			if(queuedSkill==null){
				AbilitySkill[] skills = abilitySkills.toArray(new AbilitySkill[0]);
				AbilitySkill skill = skills[entity.getRandom().nextInt(skills.length)];
				if(!cooldownTime.containsKey(skill)||cooldownTime.get(skill)<entity.level.getGameTime()){
					castingTime = skill.getCastTime()+entity.level.getGameTime();
					queuedSkill = skill;
					return false;
				}
			}else{
				queuedSkill.getSkillAction().useSkill(entity, this);
				cooldownTime.put(queuedSkill, queuedSkill.getCooldown()+entity.level.getGameTime());
				queuedSkill = null;
				return true;
			}
		}
		return false;
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
		if(!abilitySkills.isEmpty()){
			tryUseSkill(entity);
		}
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
		if(ability.onHurt()!=null) onHurtListeners.put(ability, ability.onHurt());
		if(ability.onAttack()!=null) onAttackListeners.put(ability, ability.onAttack());
		if(ability.onDeath()!=null) onDeathListeners.put(ability, ability.onDeath());
		if(ability.onUpdate()!=null) onUpdateListeners.put(ability, ability.onUpdate());
		if(!ability.getSkills().isEmpty()) abilitySkills.addAll(ability.getSkills());
	}

	protected void onAbilityRemoved(Ability ability, LivingEntity entity){
		for(Entry<Attribute, Set<AttributeModifier>> entry : ability.getAttributes().entrySet()){
			ModifiableAttributeInstance instance = entity.getAttributes().getInstance(entry.getKey());
			if(instance==null) continue;
			for(AttributeModifier m : entry.getValue())
				instance.removeModifier(m.getId());
		}
		onHurtListeners.remove(ability);
		onAttackListeners.remove(ability);
		onDeathListeners.remove(ability);
		onUpdateListeners.remove(ability);
		abilitySkills.removeAll(ability.getSkills());
	}

	@Override
	public CompoundNBT serializeNBT(){
		CompoundNBT nbt = new CompoundNBT();
		if(!abilities.isEmpty()) nbt.put("abilities", serializeAbilities(abilities));
		if(this.generateAbility) nbt.putBoolean("generateAbility", true);
		if(appliedGeneratorScheme!=null) nbt.putString("appliedGeneratorScheme", appliedGeneratorScheme.getId().toString());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt){
		this.clear();

		ListNBT abilities = nbt.getList("abilities", Constants.NBT.TAG_STRING);
		deserializeAbilities(abilities, this::add);
		this.generateAbility = nbt.getBoolean("generateAbility");
		this.appliedGeneratorScheme = nbt.contains("appliedGeneratorScheme", Constants.NBT.TAG_STRING) ?
				AbilityGenerators.findSchemeWithId(new ResourceLocation(nbt.getString("appliedGeneratorScheme"))) :
				null;
	}

	private static ListNBT serializeAbilities(Collection<Ability> abilities){
		return abilities.stream()
				.map(Abilities.getRegistry()::getKey)
				.filter(Objects::nonNull)
				.map(ResourceLocation::toString)
				.map(StringNBT::valueOf)
				.collect(ListNBT::new, AbstractList::add, (l1, l2) -> {});
	}

	private static void deserializeAbilities(ListNBT nbt, Consumer<Ability> forEach){
		if(!nbt.isEmpty()) nbt.stream()
				.map(INBT::getAsString)
				.map(ResourceLocation::new)
				.map(Abilities.getRegistry()::getValue)
				.filter(Objects::nonNull)
				.forEach(forEach);
	}
}
