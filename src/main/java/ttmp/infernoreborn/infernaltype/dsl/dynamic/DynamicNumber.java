package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;

public interface DynamicNumber extends Dynamic{
	double evaluateNumber(@Nullable InfernalGenContext context);

	@Override default Object evaluate(@Nullable InfernalGenContext context){
		return evaluateNumber(context);
	}
	@Override default boolean matches(Class<?> type){
		return type.isAssignableFrom(Double.class);
	}
	@Override default DynamicNumber collapseConstant(){
		return isConstant() ? Dynamic.constantNumber(evaluateNumber(null)) : this;
	}

	final class Const implements DynamicNumber{
		private final double value;

		public Const(double value){
			this.value = value;
		}

		@Override public double evaluateNumber(@Nullable InfernalGenContext context){
			return value;
		}

		@Override public boolean isConstant(){
			return true;
		}

		@Override public DynamicNumber collapseConstant(){
			return this;
		}

		@Override public String toString(){
			return ""+value;
		}
	}
}
