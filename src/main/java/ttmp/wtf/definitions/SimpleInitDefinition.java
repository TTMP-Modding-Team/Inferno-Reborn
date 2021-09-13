package ttmp.wtf.definitions;

import ttmp.wtf.EvalContext;
import ttmp.wtf.definitions.initializer.Initializer;

import javax.annotation.Nullable;

/**
 * Simple utility class for {@link InitDefinition}s that doesn't depend on eval context.
 *
 * @param <T> Final product of {@link Initializer}
 */
@FunctionalInterface
public interface SimpleInitDefinition<T> extends InitDefinition<T>{
	Initializer<T> createInitializer();

	@Override default Initializer<T> createInitializer(@Nullable EvalContext evalContext){
		return createInitializer();
	}
}
