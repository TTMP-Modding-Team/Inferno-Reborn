package ttmp.wtf.definitions.initializer;

import ttmp.wtf.internal.WtfExecutor;

import java.util.HashSet;
import java.util.Set;

public class SetInitializer implements Initializer<Set<Object>>{
	private final Set<Object> set = new HashSet<>();

	@Override public void apply(WtfExecutor interpreter, Object o){
		set.add(o);
	}

	@Override public Set<Object> finish(WtfExecutor interpreter){
		return set;
	}
}
