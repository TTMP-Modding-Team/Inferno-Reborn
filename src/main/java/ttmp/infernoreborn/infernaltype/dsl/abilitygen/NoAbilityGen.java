package ttmp.infernoreborn.infernaltype.dsl.abilitygen;

import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import java.util.Collections;
import java.util.List;

public final class NoAbilityGen implements AbilityGen{
	public static final NoAbilityGen INSTANCE = new NoAbilityGen();

	private NoAbilityGen(){}

	@Override public List<Ability> generate(InfernalGenContext context){
		return Collections.emptyList();
	}
	@Override public void validate(){}

	@Override public String toString(){
		return "<No Ability>";
	}
}
