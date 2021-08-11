package ttmp.cafscript.definitions.initializer;

import ttmp.cafscript.internal.CafInterpreter;

public class NumberInitializer implements Initializer<Double>{
	private double value;

	public NumberInitializer(){}
	public NumberInitializer(double defaultValue){
		this.value = defaultValue;
	}

	@Override public void apply(CafInterpreter interpreter, Object o){
		this.value = (Double)o;
	}

	@Override public Double finish(){
		return value;
	}
}
