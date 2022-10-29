package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;

public final class NumNegate implements DynamicNumber{
	private final DynamicNumber num;

	public NumNegate(DynamicNumber num){
		this.num = num;
	}

	@Override public double evaluateNumber(@Nullable InfernalGenContext context){
		return num.evaluateNumber(context);
	}

	@Override public boolean isConstant(){
		return num.isConstant();
	}

	@Override public String toString(){
		return "-"+num;
	}
}
