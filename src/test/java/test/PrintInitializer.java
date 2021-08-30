package test;

import ttmp.wtf.definitions.initializer.Initializer;
import ttmp.wtf.internal.WtfExecutor;

import javax.annotation.Nullable;

/**
 * No property getters. Properties set and applied objects are logged.
 */
public class PrintInitializer implements Initializer<Object>{
	public static final PrintInitializer INSTANCE = new PrintInitializer();

	@Override public void setPropertyValue(WtfExecutor executor, String property, Object o){
		System.out.println("Property "+property+": "+o);
	}
	@Nullable @Override public Initializer<?> setPropertyValueLazy(WtfExecutor executor, String property, int codepoint){
		System.out.println("Lazy Property "+property+": "+codepoint);
		return INSTANCE;
	}

	@Override public void apply(WtfExecutor executor, Object o){
		System.out.println(": "+o);
	}

	@Override public Object finish(WtfExecutor executor){
		return INSTANCE;
	}
}
