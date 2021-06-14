package ttmp.infernoreborn.ability;

import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.capability.AbilityHolder;


public class AbilitySkill{
	private final String id;
	private final long castTime;
	private final long cooldown;
	private final OnSkill onskill;

	public AbilitySkill(String id, long castTime, long cooldown, OnSkill onskill){
		this.id = id;
		this.castTime = castTime;
		this.cooldown = cooldown;
		this.onskill = onskill;
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

	public OnSkill getOnskill(){
		return onskill;
	}
	@FunctionalInterface
	public interface OnSkill{
		void cast(LivingEntity entity, AbilityHolder holder);
	}
}
