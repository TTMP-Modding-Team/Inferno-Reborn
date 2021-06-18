package ttmp.infernoreborn.contents.ability.generator.node.variable;

import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.contents.ability.generator.node.Node;
import ttmp.infernoreborn.contents.ability.holder.AbilityHolder;

public interface SomeInteger extends Node{
	int getInt(LivingEntity entity, AbilityHolder holder);
}
