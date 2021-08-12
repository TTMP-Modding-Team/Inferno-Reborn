package ttmp.cafscript.definitions.initializer;

import ttmp.cafscript.internal.CafInterpreter;

import javax.annotation.Nullable;

public class RandomNumberInitializer implements Initializer<Double>{
	private double min = 0;
	private double max = 1;

	@Override public Object getPropertyValue(CafInterpreter interpreter, String property){
		if("Min".equals(property)) return min;
		if("Max".equals(property)) return max;
		return interpreter.noPropertyError(property);
	}

	@Override public void setPropertyValue(CafInterpreter interpreter, String property, Object o){
		if("Min".equals(property)) min = interpreter.expectNumber(o);
		else if("Max".equals(property)) max = interpreter.expectNumber(o);
		else interpreter.noPropertyError(property);
	}

	@Nullable @Override public Initializer<?> setPropertyValueLazy(CafInterpreter interpreter, String property, int codepoint){
		if("Min".equals(property)) return new NumberInitializer(min);
		if("Max".equals(property)) return new NumberInitializer(max);
		return interpreter.noPropertyError(property);
	}

	@Override public Double finish(CafInterpreter interpreter){
		return min==max ? min :
				min+interpreter.getEngine().getRandom().nextDouble()*(max-min);
	}
}
