package ttmp.cafscript.definitions;

import ttmp.cafscript.definitions.initializer.Initializer;

@FunctionalInterface
public interface InitDefinition<T>{
	Initializer<T> createInitializer();
}
