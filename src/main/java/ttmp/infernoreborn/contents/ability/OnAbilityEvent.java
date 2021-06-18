package ttmp.infernoreborn.contents.ability;

import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.contents.ability.holder.AbilityHolder;

@FunctionalInterface
public interface OnAbilityEvent<EVENT>{
	void onEvent(LivingEntity entity, AbilityHolder holder, EVENT event);
}
