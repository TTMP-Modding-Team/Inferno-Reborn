package ttmp.infernoreborn.infernaltype;

import among.TypeFlags;
import among.construct.Constructor;
import among.obj.AmongObject;
import ttmp.infernoreborn.infernaltype.dsl.InfernalTypeDsl;
import ttmp.infernoreborn.infernaltype.dsl.abilitygen.AbilityGen;
import ttmp.infernoreborn.infernaltype.dsl.abilitygen.NoAbilityGen;
import ttmp.infernoreborn.infernaltype.dsl.dynamic.Dynamic;
import ttmp.infernoreborn.infernaltype.dsl.dynamic.DynamicInt;
import ttmp.infernoreborn.infernaltype.dsl.effect.InfernalEffect;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static among.construct.ConditionedConstructor.objectCondition;

public final class InfernalType{
	@Nullable private final String name;
	private final DynamicInt weight;
	@Nullable private final ItemColor itemColor;
	private final List<InfernalEffect> effects;
	private final AbilityGen abilityGen;

	public InfernalType(@Nullable String name, DynamicInt weight, @Nullable ItemColor itemColor, List<InfernalEffect> effects, @Nullable AbilityGen abilityGen){
		this.name = name;
		this.weight = Objects.requireNonNull(weight);
		this.itemColor = itemColor;
		this.effects = effects;
		this.abilityGen = abilityGen==null ? NoAbilityGen.INSTANCE : abilityGen;
	}

	@Nullable public String getName(){
		return name;
	}
	public DynamicInt getWeight(){
		return weight;
	}
	@Nullable public ItemColor getItemColor(){
		return itemColor;
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
		if(itemColor!=null) stb.append(", itemColor=").append(itemColor);
		if(!effects.isEmpty()) stb.append(", effects=").append(effects);
		if(abilityGen!=NoAbilityGen.INSTANCE) stb.append(", abilityGen=").append(abilityGen);
		return stb.append('}').toString();
	}

	private static final Constructor<AmongObject, ItemColor> ITEM_COLOR = objectCondition(c -> c
					.optionalProperty("Primary", TypeFlags.PRIMITIVE)
					.optionalProperty("Secondary", TypeFlags.PRIMITIVE)
					.optionalProperty("Highlight", TypeFlags.PRIMITIVE),
			(o, r) -> {
				Integer primary;
				if(o.hasProperty("Primary")){
					primary = InfernalTypeDsl.RGB.construct(o.expectProperty("Primary").asPrimitive(), r);
					if(primary==null) return null;
				}else primary = null;
				Integer secondary;
				if(o.hasProperty("Secondary")){
					secondary = InfernalTypeDsl.RGB.construct(o.expectProperty("Secondary").asPrimitive(), r);
					if(secondary==null) return null;
				}else secondary = null;
				Integer highlight;
				if(o.hasProperty("Highlight")){
					highlight = InfernalTypeDsl.RGB.construct(o.expectProperty("Highlight").asPrimitive(), r);
					if(highlight==null) return null;
				}else highlight = null;
				return new ItemColor(primary, secondary, highlight);
			});

	public static final Constructor<AmongObject, InfernalType> INFERNAL_TYPE = objectCondition(c -> c
					.property("Weight")
					.optionalProperty("Effects", TypeFlags.LIST)
					.optionalProperty("Abilities")
					.optionalProperty("ItemColor", TypeFlags.OBJECT),
			(o, r) -> {
				DynamicInt weight = Dynamic.DYNAMIC_INT.construct(o.expectProperty("Weight"), r);
				List<InfernalEffect> effects = o.hasProperty("Effects") ? Constructor.listOf(InfernalEffect.INFERNAL_EFFECT)
						.construct(o.expectProperty("Effects").asList(), r) :
						Collections.emptyList();
				AbilityGen abilityGen;
				if(o.hasProperty("Abilities")){
					abilityGen = AbilityGen.ABILITY_GEN.construct(o.expectProperty("Abilities"), r);
					if(abilityGen==null) return null;
				}else abilityGen = null;
				if(weight==null||effects==null) return null;
				ItemColor itemColor;
				if(o.hasProperty("ItemColor")){
					itemColor = InfernalType.ITEM_COLOR.construct(o.expectProperty("ItemColor").asObj(), r);
					if(itemColor==null) return null;
				}else itemColor = null;
				if(r!=null&&o.getName().matches("\\s"))
					r.reportWarning("Avoid using whitespaces in infernal type name, as it is difficult to write in commands", o.sourcePosition());
				return new InfernalType(o.hasName() ? o.getName() : null, weight, itemColor, effects, abilityGen);
			});

	public static final class ItemColor{
		@Nullable private final Integer primary;
		@Nullable private final Integer secondary;
		@Nullable private final Integer highlight;

		public ItemColor(@Nullable Integer primary, @Nullable Integer secondary, @Nullable Integer highlight){
			this.primary = primary;
			this.secondary = secondary;
			this.highlight = highlight;
		}

		@Nullable public Integer getPrimary(){
			return primary;
		}
		@Nullable public Integer getSecondary(){
			return secondary;
		}
		@Nullable public Integer getHighlight(){
			return highlight;
		}

		@Override public String toString(){
			StringBuilder stb = new StringBuilder().append("{");
			if(primary!=null) stb.append("primary=").append(String.format("%06X", primary));
			if(secondary!=null){
				if(primary!=null) stb.append(", ");
				stb.append("secondary=").append(String.format("%06X", secondary));
			}
			if(highlight!=null){
				if(primary!=null||secondary!=null) stb.append(", ");
				stb.append("highlight=").append(String.format("%06X", highlight));
			}
			return stb.append("}").toString();
		}
	}
}
