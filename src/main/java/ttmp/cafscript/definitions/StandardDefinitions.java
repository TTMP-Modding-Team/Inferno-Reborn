package ttmp.cafscript.definitions;

import ttmp.cafscript.definitions.initializer.BoolInitializer;
import ttmp.cafscript.definitions.initializer.NumberInitializer;
import ttmp.cafscript.definitions.initializer.RandomInitializer;
import ttmp.cafscript.definitions.initializer.RandomNumberInitializer;

public final class StandardDefinitions{
	private StandardDefinitions(){}

	public static final InitDefinition<Double> NUMBER = NumberInitializer::new;
	public static final InitDefinition<Boolean> BOOL = BoolInitializer::new;

	public static final InitDefinition<Boolean> RANDOM = RandomInitializer::new;
	public static final InitDefinition<Double> RANDOM_NUMBER = RandomNumberInitializer::new;
}
