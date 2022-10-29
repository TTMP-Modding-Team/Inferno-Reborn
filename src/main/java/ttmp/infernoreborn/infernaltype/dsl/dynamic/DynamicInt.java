package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;

public interface DynamicInt extends DynamicNumber{
	int evaluateInt(@Nullable InfernalGenContext context);

	@Override default double evaluateNumber(@Nullable InfernalGenContext context){
		return evaluateInt(context);
	}
	@Override default Object evaluate(@Nullable InfernalGenContext context){
		return evaluateInt(context);
	}
	@Override default boolean matches(Class<?> type){
		return type.isAssignableFrom(Integer.class);
	}
	@Override default DynamicInt collapseConstant(){
		return isConstant() ? Dynamic.constantInt(evaluateInt(null)) : this;
	}

	final class Const implements DynamicInt{
		private final int value;

		public Const(int value){
			this.value = value;
		}

		@Override public int evaluateInt(@Nullable InfernalGenContext context){
			return value;
		}

		@Override public boolean isConstant(){
			return true;
		}

		@Override public DynamicInt collapseConstant(){
			return this;
		}

		@Override public String toString(){
			return ""+value;
		}
	}
}
