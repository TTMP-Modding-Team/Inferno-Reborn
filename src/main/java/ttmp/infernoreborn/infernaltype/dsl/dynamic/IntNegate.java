package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;

public final class IntNegate implements DynamicInt{
	private final DynamicInt i;

	public IntNegate(DynamicInt i){
		this.i = i;
	}

	@Override public int evaluateInt(@Nullable InfernalGenContext context){
		return -i.evaluateInt(context);
	}

	@Override public boolean isConstant(){
		return i.isConstant();
	}

	@Override public String toString(){
		return "-"+i;
	}
}
