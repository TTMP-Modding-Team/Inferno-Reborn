package ttmp.infernoreborn.contents.ability.cooldown;

import ttmp.infernoreborn.contents.ability.Ability;

import java.util.Set;

public interface Cooldown{
	default boolean has(Ability.CooldownTicket cooldown){
		return get(cooldown)>0;
	}
	long get(Ability.CooldownTicket cooldown);
	void set(Ability.CooldownTicket cooldown, long ticks);

	Set<Ability.CooldownTicket> getAllActiveCooldowns();

	void increaseAll(long ticks);
	void decreaseAll(long ticks);

	default boolean hasGlobalDelay(){
		return getGlobalDelay()>0;
	}
	long getGlobalDelay();
	void setGlobalDelay(long ticks);

	default boolean hasCastTime(){
		return getCastTime()>0;
	}
	long getCastTime();
	void setCastTime(long ticks);
}
