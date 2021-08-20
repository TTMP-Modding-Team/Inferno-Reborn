package ttmp.wtf.definitions;

import ttmp.wtf.definitions.initializer.Initializer;

@FunctionalInterface
public interface InitDefinition<T>{
	Initializer<T> createInitializer();
}
