package ttmp.wtf.definitions.initializer;

import ttmp.wtf.internal.WtfExecutor;

public class NumberInitializer implements Initializer<Double>{
	private double value;

	public NumberInitializer(){}
	public NumberInitializer(double defaultValue){
		this.value = defaultValue;
	}

	@Override public void apply(WtfExecutor interpreter, Object o){
		this.value = interpreter.expectNumber(o);
	}

	@Override public Double finish(WtfExecutor interpreter){
		return value;
	}
}
