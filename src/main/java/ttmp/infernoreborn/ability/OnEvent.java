package ttmp.infernoreborn.ability;

import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.capability.AbilityHolder;

@FunctionalInterface
public interface OnEvent<EVENT>{
	void onEvent(LivingEntity entity, AbilityHolder holder, EVENT event);
}
