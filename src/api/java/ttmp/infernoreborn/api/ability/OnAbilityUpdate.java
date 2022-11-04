package ttmp.infernoreborn.api.ability;

import net.minecraft.entity.LivingEntity;

@FunctionalInterface
public interface OnAbilityUpdate{
	void onUpdate(LivingEntity entity, AbilityHolder holder);
}
