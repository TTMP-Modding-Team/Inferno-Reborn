package ttmp.infernoreborn.infernaltype.wtf;

import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.contents.Abilities;
import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.util.SomeAbility;
import ttmp.wtf.definitions.initializer.Initializer;
import ttmp.wtf.internal.WtfExecutor;

import javax.annotation.Nullable;

public class AbilitiesInitializer implements Initializer<Void>{
	private final AbilityGenerationContext context;

	public AbilitiesInitializer(@Nullable Object evalContext){
		if(evalContext==null) throw new IllegalArgumentException("Expected context");
		else if(evalContext instanceof AbilityGenerationContext) this.context = (AbilityGenerationContext)evalContext;
		else throw new IllegalArgumentException("Expected AbilityGenerationContext");
	}

	@Override public void apply(WtfExecutor executor, Object o){
		Initializer.super.apply(executor, o);
		if(o instanceof SomeAbility){
			addAbility(((SomeAbility)o).getAbility());
		}else if(o instanceof ResourceLocation){
			addAbility((ResourceLocation)o);
		}
	}

	private void addAbility(ResourceLocation id){
		Ability ability = Abilities.getRegistry().getValue(id);
		if(ability==null) InfernoReborn.LOGGER.warn("No ability with ID {}", id);
		else addAbility(ability);
	}

	private void addAbility(@Nullable Ability ability){
		if(ability!=null) context.getAbilityHolder().add(ability);
	}

	@Override public Void finish(WtfExecutor executor){
		return null;
	}
}
