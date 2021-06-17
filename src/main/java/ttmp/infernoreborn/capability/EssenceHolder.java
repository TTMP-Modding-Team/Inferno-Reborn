package ttmp.infernoreborn.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import ttmp.infernoreborn.util.EssenceType;

import javax.annotation.Nullable;
import java.util.Arrays;

public class EssenceHolder implements ICapabilitySerializable<CompoundNBT>{
	public static final int MAX = Integer.MAX_VALUE;

	private final int[] essences = new int[EssenceType.values().length];

	public int getEssence(EssenceType type){
		return essences[type.ordinal()];
	}
	public void setEssence(EssenceType type, int essence){
		this.essences[type.ordinal()] = Math.max(0, essence);
	}

	public int insertEssence(EssenceType type, int essence, boolean simulate){
		if(essence<=0) return essence;
		int toInsert = Math.min(MAX-this.essences[type.ordinal()], essence);
		if(!simulate) this.essences[type.ordinal()] += toInsert;
		return toInsert;
	}
	public int extractEssence(EssenceType type, int essence, boolean simulate){
		if(essence<=0) return essence;
		int toExtract = Math.min(this.essences[type.ordinal()], essence);
		if(!simulate) this.essences[type.ordinal()] -= toExtract;
		return toExtract;
	}

	public boolean isEmpty(){
		for(int essence : essences)
			if(essence>0) return false;
		return true;
	}

	public void clear(){
		Arrays.fill(essences, 0);
	}

	private final LazyOptional<EssenceHolder> self = LazyOptional.of(() -> this);

	@Override public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side){
		return Caps.essenceHolder==cap ? self.cast() : LazyOptional.empty();
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
