package ttmp.infernoreborn.util;

import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import ttmp.infernoreborn.api.ability.Ability;
import ttmp.infernoreborn.api.ability.AbilitySkill;
import ttmp.infernoreborn.api.ability.Cooldown;
import ttmp.infernoreborn.contents.Abilities;

import javax.annotation.Nullable;
import java.util.Set;

public class ServerAbilityCooldown implements Cooldown{
	private final Object2LongMap<Ability.CooldownTicket> cooldowns = new Object2LongLinkedOpenHashMap<>();

	@Nullable private AbilitySkill castingSkill;
	private long castTime;

	private long globalDelay;

	@Override public long get(Ability.CooldownTicket cooldown){
		return cooldowns.getLong(cooldown);
	}
	@Override public void set(Ability.CooldownTicket cooldown, long ticks){
		cooldowns.put(cooldown, ticks);
	}
	@Override public Set<Ability.CooldownTicket> getAllActiveCooldowns(){
		return cooldowns.keySet();
	}
	@Override public void increaseAll(long ticks){
		for(Object2LongMap.Entry<Ability.CooldownTicket> e : cooldowns.object2LongEntrySet()) e.setValue(e.getLongValue()+ticks);
	}
	@Override public void decreaseAll(long ticks){
		ObjectIterator<Object2LongMap.Entry<Ability.CooldownTicket>> it = cooldowns.object2LongEntrySet().iterator();
		while(it.hasNext()){
			Object2LongMap.Entry<Ability.CooldownTicket> e = it.next();
			long left = e.getLongValue()-ticks;
			if(left<=0) it.remove();
			else e.setValue(left);
		}
	}
	@Override public long getGlobalDelay(){
		return globalDelay;
	}
	@Override public void setGlobalDelay(long ticks){
		globalDelay = ticks;
	}
	@Override public long getCastTime(){
		return castTime;
	}
	@Override public void setCastTime(long ticks){
		castTime = ticks;
	}

	@Nullable public AbilitySkill getCastingSkill(){
		return castingSkill;
	}
	public void setCastingSkill(@Nullable AbilitySkill castingSkill){
		this.castingSkill = castingSkill;
	}

	public void save(CompoundNBT nbt){
		CompoundNBT cooldowns = null;
		for(Object2LongMap.Entry<Ability.CooldownTicket> e : this.cooldowns.object2LongEntrySet()){
			if(e.getLongValue()<=0) continue;
			if(cooldowns==null) cooldowns = new CompoundNBT();
			cooldowns.putLong(write(e.getKey()), e.getLongValue());
		}
		if(cooldowns!=null) nbt.put("cooldowns", cooldowns);
		if(castingSkill!=null){
			nbt.putString("castingSkill", write(castingSkill));
			nbt.putLong("castTime", castTime);
		}
		if(globalDelay>0) nbt.putLong("globalDelay", globalDelay);
	}

	public void load(CompoundNBT nbt){
		this.cooldowns.clear();
		if(nbt.contains("cooldowns", Constants.NBT.TAG_COMPOUND)){
			CompoundNBT cooldowns = nbt.getCompound("cooldowns");
			for(String key : cooldowns.getAllKeys()){
				Ability.CooldownTicket ticket = readTicket(key);
				if(ticket!=null) this.cooldowns.put(ticket, cooldowns.getLong(key));
			}
		}
		if(nbt.contains("castingSkill", Constants.NBT.TAG_STRING)){
			castingSkill = readSkill(nbt.getString("castingSkill"));
			if(castingSkill!=null) castTime = nbt.getLong("castTime");
		}
		globalDelay = nbt.getLong("globalDelay");
	}

	private static String write(AbilitySkill skill){
		return skill.getCooldownTicket().getAbility().getRegistryName()+":"+Byte.toUnsignedInt(skill.getId());
	}

	@Nullable private static AbilitySkill readSkill(String string){
		int idx = string.lastIndexOf(':');
		ResourceLocation id = new ResourceLocation(string.substring(0, idx));
		Ability ability = Abilities.getRegistry().getValue(id);
		if(ability==null) return null;
		int i = Integer.parseInt(string.substring(idx+1));
		for(AbilitySkill skill : ability.getSkills())
			if(skill.getId()==i) return skill;
		return null;
	}

	private static String write(Ability.CooldownTicket cooldown){
		return cooldown.getAbility().getRegistryName()+":"+Byte.toUnsignedInt(cooldown.getId());
	}

	@Nullable private static Ability.CooldownTicket readTicket(String string){
		int idx = string.lastIndexOf(':');
		ResourceLocation id = new ResourceLocation(string.substring(0, idx));
		Ability ability = Abilities.getRegistry().getValue(id);
		if(ability==null) return null;
		int i = Integer.parseInt(string.substring(idx+1));
		return i<256 ? ability.getCooldownTicket((byte)i) : null;
	}
}
