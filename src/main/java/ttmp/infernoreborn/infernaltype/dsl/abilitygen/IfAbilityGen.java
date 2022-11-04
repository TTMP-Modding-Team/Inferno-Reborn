package ttmp.infernoreborn.infernaltype.dsl.abilitygen;

import ttmp.infernoreborn.api.ability.Ability;
import ttmp.infernoreborn.infernaltype.InfernalGenContext;
import ttmp.infernoreborn.infernaltype.dsl.dynamic.DynamicBool;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public final class IfAbilityGen implements AbilityGen{
	private final DynamicBool condition;
	private final AbilityGen ifThen;
	@Nullable private final AbilityGen elseThen;

	public IfAbilityGen(DynamicBool condition, AbilityGen ifThen, @Nullable AbilityGen elseThen){
		this.condition = condition;
		this.ifThen = ifThen;
		this.elseThen = elseThen;
	}

	@Override public List<Ability> generate(InfernalGenContext context){
		return condition.evaluateBool(context) ?
				ifThen.generate(context) :
				elseThen!=null ? elseThen.generate(context) :
						Collections.emptyList();
	}

	@Override public void validate(){
		ifThen.validate();
		if(elseThen!=null) elseThen.validate();
	}

	@Override public String toString(){
		return "if("+condition+", "+ifThen+(elseThen!=null ? ", "+elseThen+')' : ')');
	}
}
