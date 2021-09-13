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

	@Override public void apply(WtfExecutor executor, Object o){
		value = executor.expectBoolean(o);
	}

	@Override public Boolean finish(WtfExecutor executor){
		return value;
	}
}
