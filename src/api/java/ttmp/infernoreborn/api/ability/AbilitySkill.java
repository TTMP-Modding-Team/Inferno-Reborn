package ttmp.infernoreborn.api.ability;

import net.minecraft.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.Objects;

public class AbilitySkill{
	private final Ability.CooldownTicket cooldownTicket;
	private final byte id;
	private final long castTime;
	private final long cooldown;
	private final SkillAction skillAction;
	@Nullable private final SkillAction skillCondition;

	public AbilitySkill(Ability.CooldownTicket cooldownTicket, byte id, long castTime, long cooldown, SkillAction skillAction, @Nullable SkillAction skillCondition){
		if(castTime<0||cooldown<0) throw new IllegalArgumentException();
		this.cooldownTicket = Objects.requireNonNull(cooldownTicket);
		this.id = id;
		this.castTime = castTime;
		this.cooldown = cooldown;
		this.skillAction = Objects.requireNonNull(skillAction);
		this.skillCondition = skillCondition;
	}

	public Ability.CooldownTicket getCooldownTicket(){
		return cooldownTicket;
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
		return cooldownTicket.equals(that.cooldownTicket)&&id==that.id;
	}
	@Override public int hashCode(){
		return Objects.hash(cooldownTicket, id);
	}

	@Override public String toString(){
		return cooldownTicket+":"+id;
	}

	@FunctionalInterface
	public interface SkillAction{
		boolean useSkill(LivingEntity entity, AbilityHolder holder);
	}

	@FunctionalInterface
	public interface TargetedSkillAction{
		boolean useTargetedSkill(LivingEntity entity, AbilityHolder holder, LivingEntity target);
	}
}
