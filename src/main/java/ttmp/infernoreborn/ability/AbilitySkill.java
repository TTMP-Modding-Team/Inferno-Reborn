package ttmp.infernoreborn.ability;

import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.ability.holder.AbilityHolder;

import javax.annotation.Nullable;
import java.util.Objects;


public class AbilitySkill{
	private final Ability ability;
	private final byte id;
	private final long castTime;
	private final long cooldown;
	private final SkillAction skillAction;
	@Nullable private final SkillAction skillCondition;

	public AbilitySkill(Ability ability, Data skillData){
		this.ability = Objects.requireNonNull(ability);
		this.id = skillData.id;
		this.castTime = skillData.getCastTime();
		this.cooldown = skillData.getCooldown();
		this.skillAction = skillData.getSkillAction();
		this.skillCondition = skillData.getSkillCondition();
	}

	public Ability getAbility(){
		return ability;
	}
	public byte getId(){
		return id;
	}
	public long getCastTime(){
		return castTime;
	}
	public long getCooldown(){
		return cooldown;
	}
	public SkillAction getSkillAction(){
		return skillAction;
	}
	@Nullable public SkillAction getSkillCondition(){
		return skillCondition;
	}

	@Override public boolean equals(Object o){
		if(this==o) return true;
		if(o==null||getClass()!=o.getClass()) return false;
		AbilitySkill that = (AbilitySkill)o;
		return id==that.id&&ability.equals(that.ability);
	}
	@Override public int hashCode(){
		return Objects.hash(id, ability);
	}

	@Override public String toString(){
		return ability.getRegistryName()+":"+id;
	}

	@FunctionalInterface
	public interface SkillAction{
		boolean useSkill(LivingEntity entity, AbilityHolder holder);
	}

	@FunctionalInterface
	public interface TargetedSkillAction{
		boolean useTargetedSkill(LivingEntity entity, AbilityHolder holder, LivingEntity target);
	}

	public static class Data{
		private final byte id;
		private final long castTime;
		private final long cooldown;
		private final SkillAction skillAction;
		@Nullable private final SkillAction skillCondition;

		public Data(byte id, long castTime, long cooldown, SkillAction skillAction, @Nullable SkillAction skillCondition){
			if(castTime<0||cooldown<0) throw new IllegalArgumentException();
			this.id = id;
			this.castTime = castTime;
			this.cooldown = cooldown;
			this.skillAction = Objects.requireNonNull(skillAction);
			this.skillCondition = skillCondition;
		}

		public byte getId(){
			return id;
		}
		public long getCastTime(){
			return castTime;
		}
		public long getCooldown(){
			return cooldown;
		}
		public SkillAction getSkillAction(){
			return skillAction;
		}
		@Nullable public SkillAction getSkillCondition(){
			return skillCondition;
		}
	}
}
