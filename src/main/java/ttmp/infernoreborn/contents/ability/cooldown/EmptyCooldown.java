package ttmp.infernoreborn.contents.ability.cooldown;

import ttmp.infernoreborn.contents.ability.Ability;

import java.util.Collections;
import java.util.Set;

public enum EmptyCooldown implements Cooldown{
	INSTANCE;

	@Override public long get(Ability.CooldownTicket cooldown){
		return 0;
	}
	@Override public void set(Ability.CooldownTicket cooldown, long ticks){}
	@Override public Set<Ability.CooldownTicket> getAllActiveCooldowns(){
		return Collections.emptySet();
	}
	@Override public void increaseAll(long ticks){}
	@Override public void decreaseAll(long ticks){}
	@Override public long getGlobalDelay(){
		return 0;
	}
	@Override public void setGlobalDelay(long ticks){}
	@Override public long getCastTime(){
		return 0;
	}
	@Override public void setCastTime(long ticks){}
}
