package ttmp.infernoreborn.contents.ability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.registries.ForgeRegistryEntry;
import ttmp.infernoreborn.util.EssenceType;
import ttmp.infernoreborn.util.LivingUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Ability extends ForgeRegistryEntry<Ability>{
	private final int primaryColor, secondaryColor, highlightColor;
	private final Map<Attribute, Set<AttributeModifier>> attributes;
	private final int[] drops;

	@Nullable private final OnAbilityEvent<LivingAttackEvent> onAttacked;
	@Nullable private final OnAbilityEvent<LivingHurtEvent> onHurt;
	@Nullable private final OnAbilityEvent<LivingHurtEvent> onHit;
	@Nullable private final OnAbilityEvent<LivingDeathEvent> onDeath;
	@Nullable private final OnAbilityUpdate onUpdate;

	private final Set<AbilitySkill> skills;

	public Ability(Properties properties){
		this.primaryColor = properties.primaryColor;
		this.secondaryColor = properties.secondaryColor;
		this.highlightColor = properties.highlightColor;
		this.attributes = properties.attributes;
		this.drops = properties.drops;

		this.onAttacked = properties.onAttacked;
		this.onHurt = properties.onHurt;
		this.onHit = properties.onHit;
		this.onDeath = properties.onDeath;
		this.onUpdate = properties.onUpdate;

		this.skills = properties.skillData.stream()
				.map(x -> new AbilitySkill(this, x))
				.collect(Collectors.toSet());
	}

	public int getPrimaryColor(){
		return primaryColor;
	}
	public int getSecondaryColor(){
		return secondaryColor;
	}
	public int getHighlightColor(){
		return highlightColor;
	}

	public Map<Attribute, Set<AttributeModifier>> getAttributes(){
		return attributes;
	}

	public int getDrop(EssenceType type){
		return drops[type.ordinal()];
	}

	@Nullable public OnAbilityEvent<LivingAttackEvent> onAttacked(){
		return onAttacked;
	}
	@Nullable public OnAbilityEvent<LivingHurtEvent> onHurt(){
		return onHurt;
	}
	@Nullable public OnAbilityEvent<LivingHurtEvent> onHit(){
		return onHit;
	}
	@Nullable public OnAbilityEvent<LivingDeathEvent> onDeath(){
		return onDeath;
	}
	@Nullable public OnAbilityUpdate onUpdate(){
		return onUpdate;
	}

	public Set<AbilitySkill> getSkills(){
		return skills;
	}

	public TranslationTextComponent getName(){
		return new TranslationTextComponent(getUnlocalizedName());
	}

	public String getUnlocalizedName(){
		ResourceLocation n = Objects.requireNonNull(getRegistryName());
		return "ability."+n.getNamespace()+"."+n.getPath();
	}

	@Override public int hashCode(){
		ResourceLocation name = getRegistryName();
		return name!=null ? name.hashCode() : 0;
	}
	@Override public boolean equals(Object obj){
		if(obj==this) return true;
		if(!(obj instanceof Ability)) return false;
		return Objects.equals(((Ability)obj).getRegistryName(), this.getRegistryName());
	}

	@Override public String toString(){
		return getUnlocalizedName();
	}

	public static final class Properties{
		private final int primaryColor, secondaryColor, highlightColor;
		private final Map<Attribute, Set<AttributeModifier>> attributes = new HashMap<>();
		private final List<AbilitySkill.Data> skillData = new ArrayList<>();
		private final int[] drops = new int[EssenceType.values().length];

		@Nullable private OnAbilityEvent<LivingAttackEvent> onAttacked;
		@Nullable private OnAbilityEvent<LivingHurtEvent> onHurt;
		@Nullable private OnAbilityEvent<LivingHurtEvent> onHit;
		@Nullable private OnAbilityEvent<LivingDeathEvent> onDeath;
		@Nullable private OnAbilityUpdate onUpdate;

		public Properties(int primaryColor, int secondaryColor){
			this(primaryColor, secondaryColor, primaryColor);
		}
		public Properties(int primaryColor, int secondaryColor, int highlightColor){
			this.primaryColor = primaryColor;
			this.secondaryColor = secondaryColor;
			this.highlightColor = highlightColor;
		}

		public Properties addAttribute(Attribute attribute, UUID uuid, double amount, AttributeModifier.Operation operation){
			Set<AttributeModifier> m = attributes.computeIfAbsent(attribute, a -> new HashSet<>());
			if(!m.add(new AttributeModifier(uuid, "Ability Attributes", amount, operation)))
				throw new IllegalStateException("Registration of attribute with overlapping ID "+uuid);
			return this;
		}

		public Properties addSkill(long castTime, long cooldown, AbilitySkill.SkillAction skillAction){
			return addSkill(castTime, cooldown, skillAction, null);
		}

		public Properties addSkill(long castTime, long cooldown, AbilitySkill.SkillAction skillAction, @Nullable AbilitySkill.SkillAction skillCondition){
			this.skillData.add(new AbilitySkill.Data((byte)this.skillData.size(), castTime, cooldown, skillAction, skillCondition));
			return this;
		}

		public Properties addTargetedSkill(long castTime, long cooldown, AbilitySkill.TargetedSkillAction skillAction){
			return addTargetedSkill(castTime, cooldown, skillAction, null);
		}

		public Properties addTargetedSkill(long castTime, long cooldown, AbilitySkill.TargetedSkillAction skillAction, @Nullable AbilitySkill.TargetedSkillAction skillCondition){
			this.skillData.add(new AbilitySkill.Data((byte)this.skillData.size(), castTime, cooldown, (entity, holder) -> {
				LivingEntity target = LivingUtils.getTarget(entity);
				return target!=null&&target.isAlive()&&skillAction.useTargetedSkill(entity, holder, target);
			}, (entity, holder) -> {
				LivingEntity target = LivingUtils.getTarget(entity);
				return target!=null&&target.isAlive()&&(skillCondition==null||skillCondition.useTargetedSkill(entity, holder, target));
			}));
			return this;
		}

		public Properties onAttacked(@Nullable OnAbilityEvent<LivingAttackEvent> onAttacked){
			this.onAttacked = onAttacked;
			return this;
		}

		public Properties onHurt(@Nullable OnAbilityEvent<LivingHurtEvent> onHurt){
			this.onHurt = onHurt;
			return this;
		}

		public Properties onHit(@Nullable OnAbilityEvent<LivingHurtEvent> onHit){
			this.onHit = onHit;
			return this;
		}

		public Properties onDeath(@Nullable OnAbilityEvent<LivingDeathEvent> onDeath){
			this.onDeath = onDeath;
			return this;
		}

		public Properties onUpdate(@Nullable OnAbilityUpdate onUpdate){
			this.onUpdate = onUpdate;
			return this;
		}

		public Properties drops(EssenceType type, int amount){
			this.drops[type.ordinal()] = Math.max(0, amount);
			return this;
		}
	}
}