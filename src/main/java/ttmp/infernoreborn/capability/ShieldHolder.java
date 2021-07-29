package ttmp.infernoreborn.capability;

import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public interface ShieldHolder{
	@SuppressWarnings("ConstantConditions") @Nullable static ShieldHolder of(ICapabilityProvider provider){
		return provider.getCapability(Caps.shieldHolder).orElse(null);
	}

	float getShield();
	void setShield(float shield);
}
