package ttmp.infernoreborn.capability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import ttmp.infernoreborn.contents.ModAttributes;
import ttmp.infernoreborn.util.LivingUtils;

import javax.annotation.Nullable;

public final class ShieldHolder implements ICapabilitySerializable<FloatNBT>{
	@SuppressWarnings("ConstantConditions") @Nullable public static ShieldHolder of(ICapabilityProvider provider){
		return provider.getCapability(Caps.shieldHolder).orElse(null);
	}

	private final LivingEntity entity;
	private float shield;

	public ShieldHolder(LivingEntity entity){
		this.entity = entity;
	}

	public float getShield(){
		return shield;
	}
	public void setShield(float shield){
		this.shield = shield<=0 ? 0 : Math.min((float)LivingUtils.getAttrib(entity, ModAttributes.SHIELD.get()), shield);
	}

	private final LazyOptional<ShieldHolder> self = LazyOptional.of(() -> this);

	@Override public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side){
		return Caps.shieldHolder==cap ? self.cast() : LazyOptional.empty();
	}

	@Override public FloatNBT serializeNBT(){
		return FloatNBT.valueOf(shield);
	}

	@Override public void deserializeNBT(FloatNBT nbt){
		this.shield = nbt.getAsFloat();
	}
}
