package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;

public interface DynamicBool extends Dynamic{
	boolean evaluateBool(@Nullable InfernalGenContext context);

	@Override default DynamicBool collapseConstant(){
		return isConstant() ? Dynamic.constantBool(evaluateBool(null)) : this;
	}
	@Override default Boolean evaluate(@Nullable InfernalGenContext context){
		return evaluateBool(context);
	}
	@Override default boolean matches(Class<?> type){
		return type.isAssignableFrom(Boolean.class);
	}

	enum Const implements DynamicBool{
		TRUE, FALSE;

		@Override public boolean evaluateBool(@Nullable InfernalGenContext context){
			return this==TRUE;
		}

		@Override public boolean isConstant(){
			return true;
		}

		@Override public DynamicBool collapseConstant(){
			return this;
		}

		@Override public String toString(){
			return this==TRUE ? "true" : "false";
		}
	}
}
