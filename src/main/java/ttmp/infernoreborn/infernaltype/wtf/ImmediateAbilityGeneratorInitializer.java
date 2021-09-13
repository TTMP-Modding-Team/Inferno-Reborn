package ttmp.infernoreborn.infernaltype.wtf;

import ttmp.wtf.definitions.initializer.Initializer;
import ttmp.wtf.definitions.initializer.IntInitializer;
import ttmp.wtf.internal.WtfExecutor;

import javax.annotation.Nullable;

public class ImmediateAbilityGeneratorInitializer implements Initializer<Void>{
	private int weight;

	public int getWeight(){
		return weight;
	}

	@Override public Object getPropertyValue(WtfExecutor executor, String property){
		if("Weight".equals(property)) return (double)weight;
		return executor.noPropertyError(property);
	}
	@Override public void setPropertyValue(WtfExecutor executor, String property, Object o){
		if("Weight".equals(property)) weight = executor.expectType(Number.class, o).intValue();
		else if(!"Abilities".equals(property)) executor.noPropertyError(property);
	}
	@Nullable @Override public Initializer<?> setPropertyValueLazy(WtfExecutor executor, String property, int codepoint){
		if("Weight".equals(property)) return new IntInitializer(weight);
		if("Abilities".equals(property)) return AbilitiesInitializer::new;
		return executor.noPropertyError(property);
	}
	@Override public Void finish(WtfExecutor executor){
		return null;
	}
}
