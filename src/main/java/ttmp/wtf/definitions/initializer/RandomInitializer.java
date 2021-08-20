package ttmp.wtf.definitions.initializer;

import ttmp.wtf.internal.WtfExecutor;

import javax.annotation.Nullable;

public class RandomInitializer implements Initializer<Boolean>{
	private double roll = .5;

	@Override public Object getPropertyValue(WtfExecutor interpreter, String property){
		if("Roll".equals(property)) return roll;
		else return interpreter.noPropertyError(property);
	}
	@Override public void setPropertyValue(WtfExecutor interpreter, String property, Object o){
		if("Roll".equals(property)) roll = interpreter.expectNumber(o);
		else interpreter.noPropertyError(property);
	}
	@Nullable @Override public Initializer<?> setPropertyValueLazy(WtfExecutor interpreter, String property, int codepoint){
		if("Roll".equals(property)) return new NumberInitializer(roll);
		else return interpreter.noPropertyError(property);
	}

	@Override public Boolean finish(WtfExecutor interpreter){
		return interpreter.getEngine().getRandom().nextDouble()<roll;
	}
}
