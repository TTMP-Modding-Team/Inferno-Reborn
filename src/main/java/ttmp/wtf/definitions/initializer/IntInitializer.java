package ttmp.wtf.definitions.initializer;

import ttmp.wtf.internal.WtfExecutor;

public class IntInitializer implements Initializer<Integer>{
	private int value;

	public IntInitializer(){}
	public IntInitializer(int defaultValue){
		this.value = defaultValue;
	}

	@Override public void apply(WtfExecutor interpreter, Object o){
		this.value = (int)interpreter.expectNumber(o);
	}

	@Override public Integer finish(WtfExecutor interpreter){
		return value;
	}
}
