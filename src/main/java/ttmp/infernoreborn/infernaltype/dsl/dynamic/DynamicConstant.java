package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;

public final class DynamicConstant implements Dynamic{
	private final Object constant;

	public DynamicConstant(Object constant){
		this.constant = constant;
	}

	@Override public Object evaluate(@Nullable InfernalGenContext context){
		return constant;
	}
	@Override public boolean matches(Class<?> type){
		return type.isInstance(constant);
	}
	@Override public boolean isConstant(){
		return true;
	}
	@Override public Dynamic collapseConstant(){
		return this;
	}

	@Override public String toString(){
		return "\""+constant+'"';
	}
}
