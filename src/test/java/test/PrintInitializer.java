package test;

import ttmp.cafscript.definitions.initializer.Initializer;
import ttmp.cafscript.internal.CafInterpreter;

import javax.annotation.Nullable;

/**
 * No property getters. Properties set and applied objects are logged.
 */
public class PrintInitializer implements Initializer<Object>{
	public static final PrintInitializer INSTANCE = new PrintInitializer();

	@Override public void setPropertyValue(CafInterpreter interpreter, String property, Object o){
		System.out.println("Property "+property+": "+o);
	}
	@Nullable @Override public Initializer<?> setPropertyValueLazy(CafInterpreter interpreter, String property, int codepoint){
		System.out.println("Lazy Property "+property+": "+codepoint);
		return INSTANCE;
	}

	@Override public void apply(CafInterpreter interpreter, Object o){
		System.out.println(": "+o);
	}

	@Override public Object finish(CafInterpreter interpreter){
		return INSTANCE;
	}
}
