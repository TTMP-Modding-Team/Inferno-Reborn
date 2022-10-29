package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;

public final class IntEqOp implements DynamicBool{
	private final DynamicInt a, b;
	private final boolean expectedEqual;

	public IntEqOp(DynamicInt a, DynamicInt b, boolean expectedEqual){
		this.a = a;
		this.b = b;
		this.expectedEqual = expectedEqual;
	}

	@Override public boolean evaluateBool(@Nullable InfernalGenContext context){
		return (a.evaluateInt(context)==b.evaluateInt(context))==expectedEqual;
	}

	@Override public boolean isConstant(){
		return a.isConstant()&&b.isConstant();
	}

	@Override public String toString(){
		return "("+a+(expectedEqual ? "==" : "!=")+b+")";
	}
}
