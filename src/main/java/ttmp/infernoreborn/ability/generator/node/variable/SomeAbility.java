package ttmp.infernoreborn.ability.generator.node.variable;

import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.ability.Ability;
import ttmp.infernoreborn.ability.generator.node.Node;
import ttmp.infernoreborn.capability.AbilityHolder;

import javax.annotation.Nullable;

public interface SomeAbility extends Node{
	@Nullable Ability getAbility(LivingEntity entity, AbilityHolder holder);
}
