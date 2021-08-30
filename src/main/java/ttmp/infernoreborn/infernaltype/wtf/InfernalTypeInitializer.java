package ttmp.infernoreborn.infernaltype.wtf;

import ttmp.wtf.definitions.initializer.Initializer;
import ttmp.wtf.definitions.initializer.IntInitializer;
import ttmp.wtf.internal.WtfExecutor;

import javax.annotation.Nullable;

public class InfernalTypeInitializer implements Initializer<InfernalTypeInitializer>{
	private int weight;
	private boolean hasAbilities;
	private int abilitiesCodepoint;

	public int getWeight(){
		return weight;
	}
	public boolean isHasAbilities(){
		return hasAbilities;
	}
	public int getAbilitiesCodepoint(){
		return abilitiesCodepoint;
	}

	@Override public Object getPropertyValue(WtfExecutor executor, String property){
		if("Weight".equals(property)) return (double)weight;
		return executor.noPropertyError(property);
	}
	@Override public void setPropertyValue(WtfExecutor executor, String property, Object o){
		if("Weight".equals(property)) weight = executor.expectType(Number.class, o).intValue();
		executor.noPropertyError(property);
	}
	@Nullable @Override public Initializer<?> setPropertyValueLazy(WtfExecutor executor, String property, int codepoint){
		if("Weight".equals(property)) return new IntInitializer(weight);
		if("Abilities".equals(property)){
			hasAbilities = true;
			abilitiesCodepoint = codepoint;
			return null;
		}
		return executor.noPropertyError(property);
	}
	@Override public InfernalTypeInitializer finish(WtfExecutor executor){
		return this;
	}
}
