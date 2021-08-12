package ttmp.cafscript.definitions.initializer;

import ttmp.cafscript.internal.CafInterpreter;

import javax.annotation.Nullable;

@FunctionalInterface
public interface Initializer<T>{
	Initializer<Object> EMPTY = (CafInterpreter interpreter) -> Initializer.EMPTY;

	/**
	 * Get property value associated with given property name. Error the shit out if there is no such property.
	 */
	default Object getPropertyValue(CafInterpreter interpreter, String property){
		return interpreter.noPropertyError(property);
	}

	/**
	 * Set property value associated with given property name. Error the shit out if there is no such property.
	 */
	default void setPropertyValue(CafInterpreter interpreter, String property, Object o){
		interpreter.noPropertyError(property);
	}

	/**
	 * Set property values associated with given property name - but lazily. Returning non-null initializer will make the property value evaluated instantly.
	 * TODO should be able to store it, while controlling lazy initialization
	 */
	default @Nullable Initializer<?> setPropertyValueLazy(CafInterpreter interpreter, String property, int codepoint){
		return interpreter.noPropertyError(property);
	}

	/**
	 * Apply. Exception if it doesn't accept.
	 */
	default void apply(CafInterpreter interpreter, Object o){
		interpreter.noApplyFunctionError();
	}

	T finish(CafInterpreter interpreter);
}
