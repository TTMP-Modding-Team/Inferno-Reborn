package ttmp.cafscript.definitions.initializer;

import ttmp.cafscript.definitions.InitDefinition;
import ttmp.cafscript.exceptions.CafException;
import ttmp.cafscript.exceptions.CafNoPropertyException;

import javax.annotation.Nullable;

@FunctionalInterface
public interface Initializer<T>{
	Initializer<Object> EMPTY = () -> Initializer.EMPTY;

	/**
	 * Get property definition assosiated with given property name. {@code null} if there is no such property.
	 */
	default @Nullable InitDefinition<?> getPropertyDefinition(String property){
		return null;
	}

	/**
	 * Get property value assosiated with given property name. Error the shit out if there is no such property. TODO exception pls
	 */
	default Object getPropertyValue(String property){
		throw new CafNoPropertyException();
	}

	/**
	 * Set property value assosiated with given property name. Error the shit out if there is no such property. TODO exception pls
	 */
	default void setPropertyValue(String property, Object o){
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
