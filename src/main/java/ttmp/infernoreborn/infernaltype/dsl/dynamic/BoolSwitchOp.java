package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;
import ttmp.infernoreborn.infernaltype.dsl.SwitchDsl;

import javax.annotation.Nullable;

public final class BoolSwitchOp extends SwitchDsl<DynamicBool> implements DynamicBool{
	public BoolSwitchOp(Dynamic value, Cases<DynamicBool> cases, DynamicBool defaultCase){
		super(value, cases, defaultCase);
	}

	@Override public boolean evaluateBool(@Nullable InfernalGenContext context){
		return matchExhaustive(context).evaluateBool(context);
	}
	@Override public boolean isConstant(){
		return value.isConstant()&&matchExhaustive(null).isConstant();
	}
}
