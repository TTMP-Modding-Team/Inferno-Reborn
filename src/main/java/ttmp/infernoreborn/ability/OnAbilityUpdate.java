package ttmp.infernoreborn.ability;

import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.ability.holder.AbilityHolder;

@FunctionalInterface
public interface OnAbilityUpdate{
	void onUpdate(LivingEntity entity, AbilityHolder holder);
}
