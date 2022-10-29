package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;

public final class NumEqOp implements DynamicBool{
	private final DynamicNumber a, b;
	private final boolean expectedEqual;

	public NumEqOp(DynamicNumber a, DynamicNumber b, boolean expectedEqual){
		this.a = a;
		this.b = b;
		this.expectedEqual = expectedEqual;
	}

	@Override public boolean evaluateBool(@Nullable InfernalGenContext context){
		return (Double.compare(a.evaluateNumber(context), b.evaluateNumber(context))==0)==expectedEqual;
	}

	@Override public boolean isConstant(){
		return a.isConstant()&&b.isConstant();
	}

	@Override public String toString(){
		return "("+a+(expectedEqual ? "==" : "!=")+b+")";
	}
}
