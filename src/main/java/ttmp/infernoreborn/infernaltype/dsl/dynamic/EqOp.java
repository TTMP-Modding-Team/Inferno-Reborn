package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;
import java.util.Objects;

public final class EqOp implements DynamicBool{
	private final Dynamic a, b;
	private final boolean expectedEqual;

	public EqOp(Dynamic a, Dynamic b, boolean expectedEqual){
		this.a = a;
		this.b = b;
		this.expectedEqual = expectedEqual;
	}

	@Override public boolean evaluateBool(@Nullable InfernalGenContext context){
		return Objects.equals(a.evaluate(context), b.evaluate(context))==expectedEqual;
	}

	@Override public boolean isConstant(){
		return a.isConstant()&&b.isConstant();
	}

	@Override public String toString(){
		return "("+a+(expectedEqual ? "==" : "!=")+b+")";
	}
}
