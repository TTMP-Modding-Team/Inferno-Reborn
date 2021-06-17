package ttmp.infernoreborn.ability;

import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.ability.holder.AbilityHolder;

import java.util.Objects;


public class AbilitySkill{
	private final String id;
	private final long castTime;
	private final long cooldown;
	private final SkillAction skillAction;
	private final Ability ability;

	public AbilitySkill(SkillData skillData, Ability ability){
		this.id = Objects.requireNonNull(skillData.getId());
		this.skillAction = Objects.requireNonNull(skillData.getSkillAction());
		this.castTime = skillData.getCastTime();
		this.cooldown = skillData.getCooldown();
		if(this.castTime<0||this.cooldown<0)
			throw new IllegalArgumentException();
		this.ability = ability;
	}

	public String getId(){
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

	@Override public boolean equals(Object o){
		if(this==o) return true;
		if(o==null||getClass()!=o.getClass()) return false;
		AbilitySkill that = (AbilitySkill)o;
		return id.equals(that.id)&&ability.equals(that.ability);
	}
	@Override public int hashCode(){
		return Objects.hash(id, ability);
	}
	@FunctionalInterface
	public interface SkillAction{
		void useSkill(LivingEntity entity, AbilityHolder holder);
	}

	public static class SkillData{
		private final String id;
		private final long castTime;
		private final long cooldown;
		private final SkillAction skillAction;

		public String getId(){
			return id;
		}
		public SkillAction getSkillAction(){
			return skillAction;
		}
		public long getCastTime(){
			return castTime;
		}
		public long getCooldown(){
			return cooldown;
		}

		public SkillData(String id, long castTime, long cooldown, SkillAction skillAction){
			this.id = Objects.requireNonNull(id);
			this.skillAction = Objects.requireNonNull(skillAction);
			if(castTime<0||cooldown<0)
				throw new IllegalArgumentException();
			this.castTime = castTime;
			this.cooldown = cooldown;
		}
	}
}
