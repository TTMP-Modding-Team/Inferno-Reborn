package ttmp.wtf.definitions.initializer;

import ttmp.wtf.internal.WtfExecutor;

import javax.annotation.Nullable;

@FunctionalInterface
public interface Initializer<T>{
	Initializer<Object> EMPTY = new Initializer<Object>(){
		@Override public Object finish(WtfExecutor executor){
			return this;
		}
		@Override public String toString(){
			return "Initializer.EMPTY";
		}
	};

	/**
	 * Get property value associated with given property name. Error the shit out if there is no such property.
	 */
	default Object getPropertyValue(WtfExecutor executor, String property){
		return executor.noPropertyError(property);
	}

	/**
	 * Set property value associated with given property name. Error the shit out if there is no such property.
	 */
	default void setPropertyValue(WtfExecutor executor, String property, Object o){
		executor.noPropertyError(property);
	}

	/**
	 * Set property values associated with given property name - but lazily. Returning non-null initializer will make the property value evaluated instantly.
	 * TODO should be able to store it, while controlling lazy initialization
	 */
	default @Nullable Initializer<?> setPropertyValueLazy(WtfExecutor executor, String property, int codepoint){
		return executor.noPropertyError(property);
	}

	/**
	 * Apply. Exception if it doesn't accept.
	 */
	default void apply(WtfExecutor executor, Object o){
		executor.noApplyFunctionError();
	}

	T finish(WtfExecutor executor);
}
