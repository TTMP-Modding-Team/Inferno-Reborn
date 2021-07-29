package ttmp.infernoreborn.capability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * haha funny class name haha shut the fuck up
 */
public class MobCapability implements ICapabilitySerializable<FloatNBT>{
	private final SimpleShieldHolder shieldHolder;

	public MobCapability(LivingEntity entity){
		this.shieldHolder = new SimpleShieldHolder(entity);
	}

	@Nullable private LazyOptional<ShieldHolder> shieldHolderLO;

	@Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		if(cap==Caps.shieldHolder){
			if(shieldHolderLO==null) shieldHolderLO = LazyOptional.of(() -> shieldHolder);
			return shieldHolderLO.cast();
		}
		return LazyOptional.empty();
	}

	@Override public FloatNBT serializeNBT(){
		return FloatNBT.valueOf(shieldHolder.getShield());
	}
	@Override public void deserializeNBT(FloatNBT nbt){
		shieldHolder.setShield(nbt.getAsFloat());
	}
}
