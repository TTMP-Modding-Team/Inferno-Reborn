package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;

public final class BoolNot implements DynamicBool{
	private final DynamicBool bool;

	public BoolNot(DynamicBool bool){
		this.bool = bool;
	}

	@Override public boolean evaluateBool(@Nullable InfernalGenContext context){
		return !bool.evaluateBool(context);
	}

	@Override public boolean isConstant(){
		return bool.isConstant();
	}

	@Override public String toString(){
		return "!"+bool;
	}
}
