package ttmp.infernoreborn.ability.generator.node.condition;

import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.ability.generator.node.Node;
import ttmp.infernoreborn.ability.holder.AbilityHolder;

public interface Condition extends Node{
	boolean matches(LivingEntity entity, AbilityHolder holder);
}
