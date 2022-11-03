package ttmp.infernoreborn.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class EssenceHolder implements Essences, EssenceHandler, INBTSerializable<CompoundNBT>{
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
		essence = Math.min(Math.max(0, essence), max);
		if(this.essences[type.ordinal()]!=essence){
			this.essences[type.ordinal()] = essence;
			onChanged(type);
		}
	}

	@Override public long totalEssences(){
		long sum = 0;
		for(int essence : essences) sum += essence;
		return sum;
	}

	@Override public int insertEssence(EssenceType type, int essence, boolean simulate){
		if(essence<=0) return essence;
		int toInsert = Math.min(max-this.essences[type.ordinal()], essence);
		if(toInsert>0&&!simulate){
			this.essences[type.ordinal()] += toInsert;
			onChanged(type);
		}
		return toInsert;
	}
	@Override public int extractEssence(EssenceType type, int essence, boolean simulate){
		if(essence<=0) return essence;
		int toExtract = Math.min(this.essences[type.ordinal()], essence);
		if(toExtract>0&&!simulate){
			this.essences[type.ordinal()] -= toExtract;
			onChanged(type);
		}
		return toExtract;
	}

	@Override public boolean isEmpty(){
		for(int essence : essences)
			if(essence>0) return false;
		return true;
	}

	public void clear(){
		for(int i = 0; i<essences.length; i++){
			if(essences[i]!=0){
				essences[i] = 0;
				onChanged(EssenceType.values()[i]);
			}
		}
	}

	protected void onChanged(EssenceType type){}

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
