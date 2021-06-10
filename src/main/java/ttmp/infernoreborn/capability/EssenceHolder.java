package ttmp.infernoreborn.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import ttmp.infernoreborn.util.EssenceType;

import javax.annotation.Nullable;
import java.util.Locale;

public class EssenceHolder implements ICapabilitySerializable<CompoundNBT>{
	@CapabilityInject(EssenceHolder.class)
	public static Capability<EssenceHolder> capability;

	private final int[] essences = new int[EssenceType.values().length];

	public int getEssence(EssenceType type){
		return essences[type.ordinal()];
	}
	public void setEssence(EssenceType type, int essence){
		this.essences[type.ordinal()] = Math.max(0, essence);
	}

	public int[] getEssences(){
		return this.essences.clone();
	}

	private final LazyOptional<EssenceHolder> self = LazyOptional.of(() -> this);

	@Override public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side){
		return capability==cap ? self.cast() : LazyOptional.empty();
	}

	@Override public CompoundNBT serializeNBT(){
		CompoundNBT nbt = new CompoundNBT();
		for(EssenceType type : EssenceType.values()){
			int essence = getEssence(type);
			if(essence>0) nbt.putInt(type.name().toLowerCase(Locale.ROOT), essence);
		}
		return nbt;
	}

	@Override public void deserializeNBT(CompoundNBT nbt){
		for(EssenceType type : EssenceType.values()){
			essences[type.ordinal()] = Math.max(0, nbt.getInt(type.name().toLowerCase(Locale.ROOT)));
		}
	}
}
