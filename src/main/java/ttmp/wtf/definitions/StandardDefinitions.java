package ttmp.wtf.definitions;

import ttmp.wtf.definitions.initializer.BoolInitializer;
import ttmp.wtf.definitions.initializer.NumberInitializer;
import ttmp.wtf.definitions.initializer.RandomInitializer;
import ttmp.wtf.definitions.initializer.RandomNumberInitializer;
import ttmp.wtf.definitions.initializer.SetInitializer;

import java.util.Set;

public final class StandardDefinitions{
	private StandardDefinitions(){}

	public static final InitDefinition<Double> NUMBER = NumberInitializer::new;
	public static final InitDefinition<Boolean> BOOL = BoolInitializer::new;

	public static final InitDefinition<Boolean> RANDOM = RandomInitializer::new;
	public static final InitDefinition<Double> RANDOM_NUMBER = RandomNumberInitializer::new;

	public static final InitDefinition<Set<Object>> SET = SetInitializer::new;
}
