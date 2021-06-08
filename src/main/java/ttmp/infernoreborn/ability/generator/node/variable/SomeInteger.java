package ttmp.infernoreborn.ability.generator.node.variable;

import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.ability.generator.node.Node;
import ttmp.infernoreborn.capability.AbilityHolder;

public interface SomeInteger extends Node{
	int getInt(LivingEntity entity, AbilityHolder holder);
}
