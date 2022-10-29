package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;
import ttmp.infernoreborn.infernaltype.dsl.SwitchDsl;

import javax.annotation.Nullable;

public final class IntSwitchOp extends SwitchDsl<DynamicInt> implements DynamicInt{
	public IntSwitchOp(Dynamic value, Cases<DynamicInt> cases, DynamicInt defaultCase){
		super(value, cases, defaultCase);
	}

	@Override public int evaluateInt(@Nullable InfernalGenContext context){
		return matchExhaustive(context).evaluateInt(context);
	}
	@Override public boolean isConstant(){
		return value.isConstant()&&matchExhaustive(null).isConstant();
	}
}
