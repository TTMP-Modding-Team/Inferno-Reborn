package ttmp.infernoreborn.util;

import ttmp.infernoreborn.contents.ability.Ability;

import javax.annotation.Nullable;

public interface SomeAbility{
	SomeAbility NONE = new SomeAbility(){
		@Nullable @Override public Ability getAbility(){
			return null;
		}
		@Override public String toString(){
			return "<No Ability>";
		}
	};

	@Nullable Ability getAbility();
}
