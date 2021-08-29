package ttmp.infernoreborn.infernaltype;

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

	@Override public Object getPropertyValue(WtfExecutor interpreter, String property){
		if("Weight".equals(property)) return (double)weight;
		return interpreter.noPropertyError(property);
	}
	@Override public void setPropertyValue(WtfExecutor interpreter, String property, Object o){
		if("Weight".equals(property)) weight = interpreter.expectType(Number.class, o).intValue();
		interpreter.noPropertyError(property);
	}
	@Nullable @Override public Initializer<?> setPropertyValueLazy(WtfExecutor interpreter, String property, int codepoint){
		if("Weight".equals(property)) return new IntInitializer(weight);
		if("Abilities".equals(property)){
			hasAbilities = true;
			abilitiesCodepoint = codepoint;
			return null;
		}
		return interpreter.noPropertyError(property);
	}
	@Override public InfernalTypeInitializer finish(WtfExecutor interpreter){
		return this;
	}
}
