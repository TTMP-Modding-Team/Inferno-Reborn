package ttmp.infernoreborn.contents.ability;

import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.contents.ability.holder.AbilityHolder;

@FunctionalInterface
public interface OnAbilityUpdate{
	void onUpdate(LivingEntity entity, AbilityHolder holder);
}
