package ttmp.infernoreborn.contents.ability;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nullable;

public interface SkillCastingState{
	/**
	 * *Mutable* list of cooldowns
	 */
	Object2LongMap<AbilitySkill> getCooldowns();

	default long getCooldown(AbilitySkill skill){
		return getCooldowns().getLong(skill);
	}
	default boolean hasCooldown(AbilitySkill skill){
		return getCooldown(skill)>0;
	}
	/**
	 * Set cooldown for specific skill. {@code time} with value <= 0 removes cooldown.
	 */
	default void setCooldown(AbilitySkill skill, long time){
		if(time<=0) getCooldowns().removeLong(skill);
		else getCooldowns().put(skill, time);
	}
	default void setCooldown(AbilitySkill skill){
		setCooldown(skill, skill.getCooldown());
	}
	default void removeCooldown(AbilitySkill skill){
		setCooldown(skill, 0);
	}

	@Nullable AbilitySkill getCastingSkill();
	long getCastingTimeLeft();
	void setCastingTimeLeft(long time);

	default boolean isCasting(){
		return getCastingSkill()!=null;
	}

	boolean triggerSkillEffect(AbilitySkill skill, LivingEntity entity, boolean applyCooldownOnSuccess);
}
