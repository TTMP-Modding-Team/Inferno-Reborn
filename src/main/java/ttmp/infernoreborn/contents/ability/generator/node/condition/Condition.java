package ttmp.infernoreborn.contents.ability.generator.node.condition;

import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.contents.ability.generator.node.Node;
import ttmp.infernoreborn.contents.ability.holder.AbilityHolder;

public interface Condition extends Node{
	boolean matches(LivingEntity entity, AbilityHolder holder);
}
