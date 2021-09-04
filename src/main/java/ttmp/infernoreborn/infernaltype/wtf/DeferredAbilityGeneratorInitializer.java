package ttmp.infernoreborn.infernaltype.wtf;

import ttmp.wtf.definitions.initializer.Initializer;
import ttmp.wtf.definitions.initializer.IntInitializer;
import ttmp.wtf.internal.WtfExecutor;

import javax.annotation.Nullable;

public class DeferredAbilityGeneratorInitializer implements Initializer<DeferredAbilityGeneratorInitializer>{
	private int weight;
	private boolean hasAbilities;
	private int abilitiesCodepoint;

	public int getWeight(){
		return weight;
	}
	public boolean hasAbilities(){
		return hasAbilities;
	}
	public int getAbilitiesCodepoint(){
		return abilitiesCodepoint;
	}

	@Override public Object getPropertyValue(WtfExecutor executor, String property){
		if("Weight".equals(property)) return weight;
		return executor.noPropertyError(property);
	}
	@Override public void setPropertyValue(WtfExecutor executor, String property, Object o){
		if("Weight".equals(property)) weight = executor.expectInt(o);
		else executor.noPropertyError(property);
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
	@Override public DeferredAbilityGeneratorInitializer finish(WtfExecutor executor){
		return this;
	}
}
