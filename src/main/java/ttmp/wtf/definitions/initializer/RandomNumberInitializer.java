package ttmp.wtf.definitions.initializer;

import ttmp.wtf.internal.WtfExecutor;

import javax.annotation.Nullable;

public class RandomNumberInitializer implements Initializer<Double>{
	private double min = 0;
	private double max = 1;

	@Override public Object getPropertyValue(WtfExecutor executor, String property){
		if("Min".equals(property)) return min;
		if("Max".equals(property)) return max;
		return executor.noPropertyError(property);
	}

	@Override public void setPropertyValue(WtfExecutor executor, String property, Object o){
		if("Min".equals(property)) min = executor.expectNumber(o);
		else if("Max".equals(property)) max = executor.expectNumber(o);
		else executor.noPropertyError(property);
	}

	@Nullable @Override public Initializer<?> setPropertyValueLazy(WtfExecutor executor, String property, int codepoint){
		if("Min".equals(property)) return new NumberInitializer(min);
		if("Max".equals(property)) return new NumberInitializer(max);
		return executor.noPropertyError(property);
	}

	@Override public Double finish(WtfExecutor executor){
		return min==max ? min :
				min+executor.getEngine().getRandom().nextDouble()*(max-min);
	}
}
