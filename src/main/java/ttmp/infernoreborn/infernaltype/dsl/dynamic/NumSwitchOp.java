package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;
import ttmp.infernoreborn.infernaltype.dsl.SwitchDsl;

import javax.annotation.Nullable;

public final class NumSwitchOp extends SwitchDsl<DynamicNumber> implements DynamicNumber{
	public NumSwitchOp(Dynamic value, Cases<DynamicNumber> cases, DynamicNumber defaultCase){
		super(value, cases, defaultCase);
	}

	@Override public double evaluateNumber(@Nullable InfernalGenContext context){
		return matchExhaustive(context).evaluateNumber(context);
	}
	@Override public boolean isConstant(){
		return value.isConstant()&&matchExhaustive(null).isConstant();
	}
}
