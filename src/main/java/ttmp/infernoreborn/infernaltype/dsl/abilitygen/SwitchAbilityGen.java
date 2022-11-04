package ttmp.infernoreborn.infernaltype.dsl.abilitygen;

import ttmp.infernoreborn.api.ability.Ability;
import ttmp.infernoreborn.infernaltype.InfernalGenContext;
import ttmp.infernoreborn.infernaltype.dsl.SwitchDsl;
import ttmp.infernoreborn.infernaltype.dsl.dynamic.Dynamic;

import java.util.Collections;
import java.util.List;

public final class SwitchAbilityGen extends SwitchDsl<AbilityGen> implements AbilityGen{
	public SwitchAbilityGen(Dynamic value, Cases<AbilityGen> cases, AbilityGen defaultCase){
		super(value, cases, defaultCase);
	}

	@Override public List<Ability> generate(InfernalGenContext context){
		AbilityGen m = match(context);
		return m!=null ? m.generate(context) : Collections.emptyList();
	}

	@Override public void validate(){
		for(AbilityGen c : cases.cases()) c.validate();
	}
}
