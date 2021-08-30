package ttmp.infernoreborn.infernaltype.wtf;

import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.contents.Abilities;
import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.util.SomeAbility;
import ttmp.wtf.EvalContext;
import ttmp.wtf.definitions.initializer.Initializer;
import ttmp.wtf.internal.WtfExecutor;
import ttmp.wtf.obj.Bundle;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ChooseAbilityInitializer implements Initializer<SomeAbility>{
	private final AbilityGenerationContext context;
	private final List<AbilityByWeight> abilities = new ArrayList<>();
	private int weightSum;

	public ChooseAbilityInitializer(@Nullable EvalContext evalContext){
		if(evalContext==null) throw new IllegalArgumentException("Expected context");
		else if(evalContext instanceof AbilityGenerationContext) this.context = (AbilityGenerationContext)evalContext;
		else throw new IllegalArgumentException("Expected AbilityGenerationContext");
	}

	@Override public void apply(WtfExecutor executor, Object o){
		if(o instanceof ResourceLocation) put((ResourceLocation)o, 1);
		else if(o instanceof Bundle){
			Bundle b = (Bundle)o;
			if(b.size()!=2) executor.error("Expected bundle with size of 2 instead of "+b.size());
			Object o2 = b.get(1);
			int weight = (int)executor.expectNumber(b.get(0));
			if(o2 instanceof ResourceLocation) put((ResourceLocation)o2, weight);
			else if(o2 instanceof SomeAbility) put(((SomeAbility)o2).getAbility(), weight);
			else executor.error("Cannot handle object of type "+o.getClass().getSimpleName());
		}else if(o instanceof SomeAbility) put(((SomeAbility)o).getAbility(), 1);
		else executor.error("Cannot handle object of type "+o.getClass().getSimpleName());
	}

	private void put(ResourceLocation id, int weight){
		Ability ability = Abilities.getRegistry().getValue(id);
		if(ability==null) InfernoReborn.LOGGER.warn("No ability with ID {}", id);
		else put(ability, weight);
	}

	private void put(@Nullable Ability ability, int weight){
		if(ability!=null&&weight>=1&&!context.getAbilityHolder().has(ability)){
			abilities.add(new AbilityByWeight(ability, weight));
			weightSum += weight;
		}
	}

	@Override public SomeAbility finish(WtfExecutor executor){
		switch(abilities.size()){
			case 0:
				return SomeAbility.NONE;
			case 1:
				return abilities.get(0).ability;
			default:
				int random = executor.getEngine().getRandom().nextInt(weightSum);
				for(AbilityByWeight ability : abilities)
					if((random -= ability.weight)<0)
						return ability.ability;
				return SomeAbility.NONE;
		}
	}

	private static final class AbilityByWeight{
		public final Ability ability;
		public final int weight;

		private AbilityByWeight(Ability ability, int weight){
			this.ability = ability;
			this.weight = weight;
		}
	}
}
