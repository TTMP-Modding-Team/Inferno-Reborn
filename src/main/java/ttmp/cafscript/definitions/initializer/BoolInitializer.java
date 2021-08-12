package ttmp.cafscript.definitions.initializer;

import ttmp.cafscript.internal.CafInterpreter;

public class BoolInitializer implements Initializer<Boolean>{
	private boolean value;

	public BoolInitializer(){
		this(false);
	}
	public BoolInitializer(boolean defaultValue){
		this.value = defaultValue;
	}

	@Override public void apply(CafInterpreter interpreter, Object o){
		value = interpreter.expectBoolean(o);
	}

	@Override public Boolean finish(CafInterpreter interpreter){
		return value;
	}
}
