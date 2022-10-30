package ttmp.infernoreborn.infernaltype;

import among.TypeFlags;
import among.construct.ConditionedConstructor;
import among.construct.Constructor;
import among.obj.AmongObject;
import ttmp.infernoreborn.infernaltype.dsl.abilitygen.AbilityGen;
import ttmp.infernoreborn.infernaltype.dsl.abilitygen.NoAbilityGen;
import ttmp.infernoreborn.infernaltype.dsl.dynamic.Dynamic;
import ttmp.infernoreborn.infernaltype.dsl.dynamic.DynamicInt;
import ttmp.infernoreborn.infernaltype.dsl.effect.InfernalEffect;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class InfernalType{
	@Nullable private final String name;
	private final DynamicInt weight;
	private final List<InfernalEffect> effects;
	private final AbilityGen abilityGen;

	public InfernalType(@Nullable String name, DynamicInt weight, List<InfernalEffect> effects, @Nullable AbilityGen abilityGen){
		this.name = name;
		this.weight = Objects.requireNonNull(weight);
		this.effects = effects;
		this.abilityGen = abilityGen==null ? NoAbilityGen.INSTANCE : abilityGen;
	}

	@Nullable public String getName(){
		return name;
	}
	public DynamicInt getWeight(){
		return weight;
	}
	public List<InfernalEffect> getEffects(){
		return Collections.unmodifiableList(effects);
	}
	public AbilityGen getAbilityGen(){
		return abilityGen;
	}

	@Override public String toString(){
		StringBuilder stb = new StringBuilder().append("InfernalType{");
		boolean first = true;
		if(name!=null){
			stb.append("name='").append(name).append('\'');
			first = false;
		}
		if(!first) stb.append(", ");
		stb.append("weight=").append(weight);
		if(!effects.isEmpty()) stb.append(", effects=").append(effects);
		if(abilityGen!=NoAbilityGen.INSTANCE) stb.append(", abilityGen=").append(abilityGen);
		return stb.append('}').toString();
	}

	public static final Constructor<AmongObject, InfernalType> INFERNAL_TYPE = ConditionedConstructor.objectCondition(c -> c
					.property("Weight")
					.optionalProperty("Effects", TypeFlags.LIST)
					.optionalProperty("Abilities"),
			(o, reportHandler) -> {
				DynamicInt weight = Dynamic.DYNAMIC_INT.construct(o.expectProperty("Weight"), reportHandler);
				List<InfernalEffect> effects = o.hasProperty("Effects") ? Constructor.listOf(InfernalEffect.INFERNAL_EFFECT)
						.construct(o.expectProperty("Effects").asList(), reportHandler) :
						Collections.emptyList();
				AbilityGen abilityGen;
				if(o.hasProperty("Abilities")){
					abilityGen = AbilityGen.ABILITY_GEN.construct(o.expectProperty("Abilities"), reportHandler);
					if(abilityGen==null) return null;
				}else abilityGen = null;
				if(weight==null||effects==null) return null;
				if(reportHandler!=null&&o.getName().matches("\\s"))
					reportHandler.reportWarning("Avoid using whitespaces in infernal type name, as it is difficult to write in commands", o.sourcePosition());
				return new InfernalType(o.hasName() ? o.getName() : null, weight, effects, abilityGen);
			});
}
