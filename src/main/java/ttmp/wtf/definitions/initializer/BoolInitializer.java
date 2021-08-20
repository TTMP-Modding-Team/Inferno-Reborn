package ttmp.wtf.definitions.initializer;

import ttmp.wtf.internal.WtfExecutor;

public class BoolInitializer implements Initializer<Boolean>{
	private boolean value;

	public BoolInitializer(){
		this(false);
	}
	public BoolInitializer(boolean defaultValue){
		this.value = defaultValue;
	}

	@Override public void apply(WtfExecutor interpreter, Object o){
		value = interpreter.expectBoolean(o);
	}

	@Override public Boolean finish(WtfExecutor interpreter){
		return value;
	}
}
