package ttmp.wtf.definitions;

import ttmp.wtf.definitions.initializer.BoolInitializer;
import ttmp.wtf.definitions.initializer.IntInitializer;
import ttmp.wtf.definitions.initializer.NumberInitializer;
import ttmp.wtf.definitions.initializer.SetInitializer;

import java.util.Set;

public final class StandardDefinitions{
	private StandardDefinitions(){}

	public static final SimpleInitDefinition<Double> NUMBER = NumberInitializer::new;
	public static final SimpleInitDefinition<Integer> INT = IntInitializer::new;
	public static final SimpleInitDefinition<Boolean> BOOL = BoolInitializer::new;

	public static final SimpleInitDefinition<Set<Object>> SET = SetInitializer::new;
}
