package ttmp.wtf.definitions.initializer;

import ttmp.wtf.internal.WtfExecutor;

import javax.annotation.Nullable;

public class RandomInitializer implements Initializer<Boolean>{
	private double roll = .5;

	@Override public Object getPropertyValue(WtfExecutor executor, String property){
		if("Roll".equals(property)) return roll;
		else return executor.noPropertyError(property);
	}
	@Override public void setPropertyValue(WtfExecutor executor, String property, Object o){
		if("Roll".equals(property)) roll = executor.expectNumber(o);
		else executor.noPropertyError(property);
	}
	@Nullable @Override public Initializer<?> setPropertyValueLazy(WtfExecutor executor, String property, int codepoint){
		if("Roll".equals(property)) return new NumberInitializer(roll);
		else return executor.noPropertyError(property);
	}

	@Override public Boolean finish(WtfExecutor executor){
		return executor.getEngine().getRandom().nextDouble()<roll;
	}
}
