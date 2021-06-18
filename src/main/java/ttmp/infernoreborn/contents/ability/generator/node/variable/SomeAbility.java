package ttmp.infernoreborn.contents.ability.generator.node.variable;

import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.contents.ability.generator.node.Node;
import ttmp.infernoreborn.contents.ability.holder.AbilityHolder;

import javax.annotation.Nullable;

public interface SomeAbility extends Node{
	@Nullable Ability getAbility(LivingEntity entity, AbilityHolder holder);
}
