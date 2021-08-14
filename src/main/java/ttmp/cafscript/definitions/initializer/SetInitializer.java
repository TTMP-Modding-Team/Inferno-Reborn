package ttmp.cafscript.definitions.initializer;

import ttmp.cafscript.internal.CafInterpreter;

import java.util.HashSet;
import java.util.Set;

public class SetInitializer implements Initializer<Set<Object>>{
	private final Set<Object> set = new HashSet<>();

	@Override public void apply(CafInterpreter interpreter, Object o){
		set.add(o);
	}

	@Override public Set<Object> finish(CafInterpreter interpreter){
		return set;
	}
}
