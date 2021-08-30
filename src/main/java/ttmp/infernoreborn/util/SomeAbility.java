package ttmp.infernoreborn.util;

import ttmp.infernoreborn.contents.ability.Ability;

import javax.annotation.Nullable;

public interface SomeAbility{
	SomeAbility NONE = () -> null;

	@Nullable Ability getAbility();
}
