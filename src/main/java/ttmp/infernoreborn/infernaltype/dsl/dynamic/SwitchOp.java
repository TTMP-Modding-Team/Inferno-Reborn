package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;
import ttmp.infernoreborn.infernaltype.dsl.SwitchDsl;

import javax.annotation.Nullable;

public final class SwitchOp extends SwitchDsl<Dynamic> implements Dynamic{
	public SwitchOp(Dynamic value, Cases<Dynamic> cases, Dynamic defaultCase){
		super(value, cases, defaultCase);
	}

	@Override public Object evaluate(@Nullable InfernalGenContext context){
		return matchExhaustive(context).evaluate(context);
	}
	@Override public boolean matches(Class<?> type){
		return this.cases.cases().stream().allMatch(d -> d.matches(type));
	}
	@Override public boolean isConstant(){
		return value.isConstant()&&matchExhaustive(null).isConstant();
	}
}
