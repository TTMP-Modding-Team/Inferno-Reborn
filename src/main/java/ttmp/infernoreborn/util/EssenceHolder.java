package ttmp.infernoreborn.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Arrays;

public final class EssenceHolder implements Essences, INBTSerializable<CompoundNBT>{
	private final int[] essences = new int[EssenceType.values().length];
	private final int max;

	public EssenceHolder(){
		this(Integer.MAX_VALUE);
	}
	public EssenceHolder(int max){
		this.max = max;
	}

	@Override public int getEssence(EssenceType type){
		return essences[type.ordinal()];
	}
	public void setEssence(EssenceType type, int essence){
		this.essences[type.ordinal()] = Math.min(Math.max(0, essence), max);
	}

	public int insertEssence(EssenceType type, int essence, boolean simulate){
		if(essence<=0) return essence;
		int toInsert = Math.min(max-this.essences[type.ordinal()], essence);
		if(!simulate) this.essences[type.ordinal()] += toInsert;
		return toInsert;
	}
	public int extractEssence(EssenceType type, int essence, boolean simulate){
		if(essence<=0) return essence;
		int toExtract = Math.min(this.essences[type.ordinal()], essence);
		if(!simulate) this.essences[type.ordinal()] -= toExtract;
		return toExtract;
	}

	public boolean insertEssences(Essences holder, boolean simulate){
		for(EssenceType type : EssenceType.values()){
			int essence = holder.getEssence(type);
			if(essence>0&&insertEssence(type, essence, true)!=essence) return false;
		}
		if(!simulate){
			for(EssenceType type : EssenceType.values()){
				int essence = holder.getEssence(type);
				if(essence>0) insertEssence(type, essence, false);
			}
		}
		return true;
	}

	public boolean extractEssences(Essences holder, boolean simulate){
		for(EssenceType type : EssenceType.values()){
			int essence = holder.getEssence(type);
			if(essence>0&&extractEssence(type, essence, true)!=essence) return false;
		}
		if(!simulate){
			for(EssenceType type : EssenceType.values()){
				int essence = holder.getEssence(type);
				if(essence>0) extractEssence(type, essence, false);
			}
		}
		return true;
	}

	@Override public boolean isEmpty(){
		for(int essence : essences)
			if(essence>0) return false;
		return true;
	}

	public void clear(){
		Arrays.fill(essences, 0);
	}

	@Override public CompoundNBT serializeNBT(){
		CompoundNBT nbt = new CompoundNBT();
		for(EssenceType type : EssenceType.values()){
			int essence = getEssence(type);
			if(essence>0) nbt.putInt(type.id, essence);
		}
		return nbt;
	}

	@Override public void deserializeNBT(CompoundNBT nbt){
		for(EssenceType type : EssenceType.values()){
			essences[type.ordinal()] = Math.max(0, nbt.getInt(type.id));
		}
	}
}
