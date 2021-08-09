package test;

import ttmp.cafscript.definitions.initializer.Initializer;

import javax.annotation.Nullable;

/**
 * No property getters. Properties set and applied objects are logged.
 */
public class PrintInitializer implements Initializer<Object>{
	public static final PrintInitializer INSTANCE = new PrintInitializer();

	@Override public void setPropertyValue(String property, Object o){
		System.out.println("Property "+property+": "+o);
	}
	@Nullable @Override public Initializer<?> setPropertyValueLazy(String property, int codepoint){
		System.out.println("Lazy Property "+property+": "+codepoint);
		return INSTANCE;
	}

	@Override public void apply(Object o){
		System.out.println(": "+o);
	}

	@Override public Object finish(){
		return INSTANCE;
	}
}
