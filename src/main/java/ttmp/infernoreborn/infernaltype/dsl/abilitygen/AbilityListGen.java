package ttmp.infernoreborn.infernaltype.dsl.abilitygen;

import ttmp.infernoreborn.api.ability.Ability;
import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import java.util.List;
import java.util.stream.Collectors;

public final class AbilityListGen implements AbilityGen{
	private final List<AbilityGen> list;

	public AbilityListGen(List<AbilityGen> list){
		this.list = list;
	}

	@Override public List<Ability> generate(InfernalGenContext context){
		return list.stream()
				.flatMap(abilityGen -> abilityGen.generate(context).stream())
				.collect(Collectors.toList());
	}

	@Override public void validate(){
		for(AbilityGen g : list) g.validate();
	}

	@Override public String toString(){
		return "["+list.stream().map(Object::toString).collect(Collectors.joining(", "))+"]";
	}
}
