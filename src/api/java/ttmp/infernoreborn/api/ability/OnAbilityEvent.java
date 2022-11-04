package ttmp.infernoreborn.api.ability;

import net.minecraft.entity.LivingEntity;

@FunctionalInterface
public interface OnAbilityEvent<EVENT>{
	void onEvent(LivingEntity entity, AbilityHolder holder, EVENT event);
}
