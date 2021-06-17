package ttmp.infernoreborn.ability;

import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.ability.holder.AbilityHolder;

@FunctionalInterface
public interface OnAbilityEvent<EVENT>{
	void onEvent(LivingEntity entity, AbilityHolder holder, EVENT event);
}
