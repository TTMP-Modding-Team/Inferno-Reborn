package ttmp.infernoreborn.contents.ability;

import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import ttmp.infernoreborn.contents.ability.holder.AbilityHolder;
import ttmp.infernoreborn.contents.Abilities;

import javax.annotation.Nullable;

public class ServerSkillCastingState implements SkillCastingState{
	private final Object2LongMap<AbilitySkill> cooldowns = new Object2LongLinkedOpenHashMap<>();
	private final AbilityHolder holder;

	public ServerSkillCastingState(AbilityHolder holder){
		this.holder = holder;
	}

	@Nullable private AbilitySkill castingSkill;
	private long castingTimeLeft;

	@Override public Object2LongMap<AbilitySkill> getCooldowns(){
		return cooldowns;
	}
	@Nullable @Override public AbilitySkill getCastingSkill(){
		return castingSkill;
	}
	@Override public long getCastingTimeLeft(){
		return castingTimeLeft;
	}
	@Override public void setCastingTimeLeft(long time){
		castingTimeLeft = time;
	}

	@Override public boolean triggerSkillEffect(AbilitySkill skill, LivingEntity entity, boolean applyCooldownOnSuccess){
		if(!entity.isAlive()||!skill.getSkillAction().useSkill(entity, holder)) return false;
		if(applyCooldownOnSuccess) setCooldown(skill);
		return true;
	}

	public void update(LivingEntity entity){
		for(ObjectIterator<Entry<AbilitySkill>> it = cooldowns.object2LongEntrySet().iterator(); it.hasNext(); ){
			Entry<AbilitySkill> e = it.next();
			e.setValue(e.getLongValue()-1);
			if(e.getLongValue()<=0) it.remove();
		}
		if(castingSkill!=null){
			if(--castingTimeLeft<=0){
				triggerSkillEffect(castingSkill, entity, true);
				castingSkill = null;
			}
		}
	}

	public void startCastSkill(AbilitySkill skill, LivingEntity entity){
		if(isCasting()) return;
		if(skill.getCastTime()<=0){
			triggerSkillEffect(skill, entity, true);
		}else{
			castingSkill = skill;
			castingTimeLeft = skill.getCastTime();
		}
	}

	public void save(CompoundNBT nbt){
		CompoundNBT cooldowns = null;
		for(Entry<AbilitySkill> e : this.cooldowns.object2LongEntrySet()){
			if(e.getLongValue()<=0) continue;
			if(cooldowns==null) cooldowns = new CompoundNBT();
			cooldowns.putLong(serialize(e.getKey()), e.getLongValue());
		}
		if(cooldowns!=null) nbt.put("cooldowns", cooldowns);
		if(castingSkill!=null){
			nbt.putString("castingSkill", serialize(castingSkill));
			nbt.putLong("castingTimeLeft", castingTimeLeft);
		}
	}
	public void load(CompoundNBT nbt){
		this.cooldowns.clear();
		if(nbt.contains("cooldowns", Constants.NBT.TAG_COMPOUND)){
			CompoundNBT cooldowns = nbt.getCompound("cooldowns");
			for(String key : cooldowns.getAllKeys()){
				AbilitySkill skill = deserialize(key);
				if(skill!=null) this.cooldowns.put(skill, cooldowns.getLong(key));
			}
		}
		if(nbt.contains("castingSkill", Constants.NBT.TAG_STRING)){
			castingSkill = deserialize(nbt.getString("castingSkill"));
			if(castingSkill!=null)
				castingTimeLeft = nbt.getLong("castingTimeLeft");
		}
	}

	private static String serialize(AbilitySkill skill){
		return skill.getAbility().getRegistryName()+":"+skill.getId();
	}
	@Nullable private static AbilitySkill deserialize(String string){
		int idx = string.lastIndexOf(':');
		ResourceLocation id = new ResourceLocation(string.substring(0, idx));
		Ability ability = Abilities.getRegistry().getValue(id);
		if(ability==null) return null;
		int i = Integer.parseInt(string.substring(idx+1));
		for(AbilitySkill skill : ability.getSkills())
			if(skill.getId()==i) return skill;
		return null;
	}
}
