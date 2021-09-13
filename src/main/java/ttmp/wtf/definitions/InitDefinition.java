package ttmp.wtf.definitions;

import ttmp.wtf.EvalContext;
import ttmp.wtf.definitions.initializer.Initializer;

import javax.annotation.Nullable;

@FunctionalInterface
public interface InitDefinition<T>{
	/**
	 * @param evalContext Context, {@code null} if it's not called on execution
	 */
	Initializer<T> createInitializer(@Nullable EvalContext evalContext);
}
