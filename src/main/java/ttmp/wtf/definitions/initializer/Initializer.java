package ttmp.wtf.definitions.initializer;

import ttmp.wtf.internal.WtfExecutor;

import javax.annotation.Nullable;

@FunctionalInterface
public interface Initializer<T>{
	Initializer<Object> EMPTY = (WtfExecutor interpreter) -> Initializer.EMPTY;

	/**
	 * Get property value associated with given property name. Error the shit out if there is no such property.
	 */
	default Object getPropertyValue(WtfExecutor interpreter, String property){
		return interpreter.noPropertyError(property);
	}

	/**
	 * Set property value associated with given property name. Error the shit out if there is no such property.
	 */
	default void setPropertyValue(WtfExecutor interpreter, String property, Object o){
		interpreter.noPropertyError(property);
	}

	/**
	 * Set property values associated with given property name - but lazily. Returning non-null initializer will make the property value evaluated instantly.
	 * TODO should be able to store it, while controlling lazy initialization
	 */
	default @Nullable Initializer<?> setPropertyValueLazy(WtfExecutor interpreter, String property, int codepoint){
		return interpreter.noPropertyError(property);
	}

	/**
	 * Apply. Exception if it doesn't accept.
	 */
	default void apply(WtfExecutor interpreter, Object o){
		interpreter.noApplyFunctionError();
	}

	T finish(WtfExecutor interpreter);
}
