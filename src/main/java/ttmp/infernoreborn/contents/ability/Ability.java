package ttmp.infernoreborn.contents.ability;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
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
import ttmp.infernoreborn.util.SomeAbility;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

public class Ability extends ForgeRegistryEntry<Ability> implements SomeAbility{
	private final int primaryColor, secondaryColor, highlightColor;
	private final Multimap<Attribute, AttributeModifier> attributes;
	private final CooldownTicket[] cooldownTickets;
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
		this.attributes = ImmutableMultimap.copyOf(properties.attributes);
		this.cooldownTickets = properties.cooldownTickets.toArray(new CooldownTicket[0]);
		for(CooldownTicket t : this.cooldownTickets) t.ability = this;
		this.drops = properties.drops;

		this.onAttacked = properties.onAttacked;
		this.onHurt = properties.onHurt;
		this.onHit = properties.onHit;
		this.onDeath = properties.onDeath;
		this.onUpdate = properties.onUpdate;

		this.skills = new HashSet<>(properties.skills);
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

	public Multimap<Attribute, AttributeModifier> getAttributes(){
		return attributes;
	}

	@Nullable public CooldownTicket getCooldownTicket(byte index){
		int ii = Byte.toUnsignedInt(index);
		return ii<this.cooldownTickets.length ? this.cooldownTickets[ii] : null;
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

	@Override public Ability getAbility(){
		return this;
	}

	public static final class Properties{
		private final int primaryColor, secondaryColor, highlightColor;
		private final Multimap<Attribute, AttributeModifier> attributes = LinkedHashMultimap.create();
		private final List<CooldownTicket> cooldownTickets = new ArrayList<>();
		private final List<AbilitySkill> skills = new ArrayList<>();
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

		public Properties addAttribute(Attribute attribute, String uuid, double amount, AttributeModifier.Operation operation){
			if(!attributes.put(attribute, new AttributeModifier(UUID.fromString(uuid), "Ability Attributes", amount, operation)))
				throw new IllegalStateException("Registration of attribute with overlapping ID "+uuid);
			return this;
		}

		private CooldownTicket newTicket(){
			if(cooldownTickets.size()>255) throw new IllegalStateException("Too many tickets");
			CooldownTicket t = new CooldownTicket((byte)cooldownTickets.size());
			cooldownTickets.add(t);
			return t;
		}

		public Properties addSkill(long castTime, long cooldown, AbilitySkill.SkillAction skillAction){
			return addSkill(castTime, cooldown, skillAction, null);
		}

		public Properties addSkill(long castTime, long cooldown, AbilitySkill.SkillAction skillAction, @Nullable AbilitySkill.SkillAction skillCondition){
			this.skills.add(new AbilitySkill(newTicket(), (byte)this.skills.size(), castTime, cooldown, skillAction, skillCondition));
			return this;
		}

		public Properties addTargetedSkill(long castTime, long cooldown, AbilitySkill.TargetedSkillAction skillAction){
			return addTargetedSkill(castTime, cooldown, skillAction, null);
		}

		public Properties addTargetedSkill(long castTime, long cooldown, AbilitySkill.TargetedSkillAction skillAction, @Nullable AbilitySkill.TargetedSkillAction skillCondition){
			this.skills.add(new AbilitySkill(newTicket(), (byte)this.skills.size(), castTime, cooldown, (entity, holder) -> {
				LivingEntity target = LivingUtils.getTarget(entity);
				return target!=null&&target.isAlive()&&skillAction.useTargetedSkill(entity, holder, target);
			}, (entity, holder) -> {
				LivingEntity target = LivingUtils.getTarget(entity);
				return target!=null&&target.isAlive()&&(skillCondition==null||skillCondition.useTargetedSkill(entity, holder, target));
			}));
			return this;
		}

		public Properties withCooldownTicket(BiConsumer<CooldownTicket, Properties> consumer){
			consumer.accept(newTicket(), this);
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

	public static final class CooldownTicket{
		private final byte id;
		@Nullable private Ability ability;

		public CooldownTicket(byte id){
			this.id = id;
		}

		public Ability getAbility(){
			if(ability==null) throw new IllegalStateException("Trying to use incomplete cooldown ticket");
			return ability;
		}
		public byte getId(){
			return id;
		}

		@Override public boolean equals(Object o){
			if(this==o) return true;
			if(o==null||getClass()!=o.getClass()) return false;
			CooldownTicket that = (CooldownTicket)o;
			return getId()==that.getId()&&Objects.equals(getAbility(), that.getAbility());
		}
		@Override public int hashCode(){
			return Objects.hash(getId(), getAbility());
		}

		@Override public String toString(){
			return ability==null ? "???@"+id : ability.getRegistryName()+"@"+id;
		}
	}
}