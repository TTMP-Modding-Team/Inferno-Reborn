package ttmp.cafscript.definitions.initializer;

import ttmp.cafscript.exceptions.CafException;
import ttmp.cafscript.exceptions.CafNoPropertyException;

import javax.annotation.Nullable;

@FunctionalInterface
public interface Initializer<T>{
	Initializer<Object> EMPTY = () -> Initializer.EMPTY;

	/**
	 * Get property value associated with given property name. Error the shit out if there is no such property. TODO exception pls
	 */
	default Object getPropertyValue(String property){
		throw new CafNoPropertyException();
	}

	/**
	 * Set property value associated with given property name. Error the shit out if there is no such property. TODO exception pls
	 */
	default void setPropertyValue(String property, Object o){
		throw new CafNoPropertyException();
	}

	/**
	 * Set property values associated with given property name - but lazily. Returning non-null initializer will make the property value evaluated instantly.
	 * TODO should be able to store it, while controlling lazy initialization
	 */
	default @Nullable Initializer<?> setPropertyValueLazy(String property, int codepoint){
		throw new CafNoPropertyException();
	}

	/**
	 * Apply. Exception if it doesn't accept. TODO exception pls
	 */
	default void apply(Object o){
		throw new CafException();
	}

	T finish();
}
