package ttmp.infernoreborn.infernaltype.dsl.abilitygen;

import among.TypeFlags;
import among.construct.ConditionedConstructor;
import among.construct.ConstructRule;
import among.construct.Constructor;
import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.api.ability.Ability;
import ttmp.infernoreborn.infernaltype.InfernalGenContext;
import ttmp.infernoreborn.infernaltype.dsl.SwitchDsl;
import ttmp.infernoreborn.infernaltype.dsl.SwitchDsl.Cases;
import ttmp.infernoreborn.infernaltype.dsl.dynamic.Dynamic;
import ttmp.infernoreborn.infernaltype.dsl.dynamic.DynamicBool;
import ttmp.infernoreborn.infernaltype.dsl.dynamic.DynamicInt;

import java.util.List;
import java.util.Map;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

public interface AbilityGen{
	List<Ability> generate(InfernalGenContext context);

	void validate();

	ConstructRule<AbilityGen> ABILITY_GEN = ConstructRule.make(_b -> _b
			.primitive("", () -> NoAbilityGen.INSTANCE)
			.primitive((p, r) -> {
				String v = p.getValue();
				ResourceLocation l = ResourceLocation.tryParse(v);
				if(l!=null) return new OneAbilityGen(v.contains(":") ? l : new ResourceLocation(MODID, l.getPath()));
				if(r!=null) r.reportError("Cannot read value '"+v+"' as resource location", p.sourcePosition());
				return null;
			})
			.list("", (l, r) -> {
				List<AbilityGen> abilityGens = Constructor.listOf(AbilityGen.ABILITY_GEN).construct(l, r);
				return abilityGens==null ? null : new AbilityListGen(abilityGens);
			})
			.list("choose", (l, r) -> {
				List<ChooseAbilityGen.Entry> entries = Constructor.listOf(AbilityGen.CHOOSE_ENTRY).construct(l, r);
				return entries==null ? null : new ChooseAbilityGen(entries);
			})
			.list("chooseMultiple", ConditionedConstructor.listCondition(c -> c
							.minSize(2),
					(l, r) -> {
						DynamicInt times = Dynamic.DYNAMIC_INT.construct(l.get(0), r);
						List<ChooseAbilityGen.Entry> list = Constructor.listOrElementOf(AbilityGen.CHOOSE_ENTRY).construct(l.get(1), r);
						if(times==null||list==null) return null;
						return new ChooseAbilityGen(list, times);
					}))
			.list("if", ConditionedConstructor.listCondition(c -> c
							.minSize(2),
					(l, r) -> {
						DynamicBool condition = Dynamic.DYNAMIC_BOOL.construct(l.get(0), r);
						AbilityGen ifThen = AbilityGen.ABILITY_GEN.construct(l.get(1), r);
						AbilityGen elseThen;
						if(l.size()>=3){
							elseThen = AbilityGen.ABILITY_GEN.construct(l.get(2), r);
							if(elseThen==null) return null;
						}else elseThen = null;
						if(condition==null||ifThen==null) return null;
						return new IfAbilityGen(condition, ifThen, elseThen);
					}))
			.list("switch", ConditionedConstructor.listCondition(c -> c
							.minSize(2)
							.elementType(1, TypeFlags.OBJECT),
					(l, r) -> {
						Dynamic value = Dynamic.DYNAMIC.construct(l.get(0), r);
						if(value==null) return null;
						Map<String, AbilityGen> cases = Maps.transformEntries(l.get(1).asObj().properties(),
								(k, v) -> v!=null ? AbilityGen.ABILITY_GEN.construct(v, r) : null);
						for(AbilityGen g : cases.values()) if(g==null) return null;
						AbilityGen defaultValue;
						if(l.size()>=3){
							defaultValue = AbilityGen.ABILITY_GEN.construct(l.get(2), r);
							if(defaultValue==null) return null;
						}else defaultValue = null;
						Cases<AbilityGen> c = SwitchDsl.caseFor(value, cases, defaultValue,
								r!=null ? r.reportAt(l.get(1).sourcePosition()) : null, false);
						if(c==null) return null;
						return new SwitchAbilityGen(value, c, defaultValue);
					}))
			.errorMessage("Invalid ability")
	);

	ConstructRule<ChooseAbilityGen.Entry> CHOOSE_ENTRY = ConstructRule.make(_b -> _b
			.obj("weighted", ConditionedConstructor.objectCondition(c -> c
					.property("weight")
					.property("element"), (o, r) -> {
				DynamicInt weight = Dynamic.DYNAMIC_INT.construct(o.expectProperty("weight"), r);
				AbilityGen element = ABILITY_GEN.construct(o.expectProperty("element"), r);
				return weight==null||element==null ? null : new ChooseAbilityGen.Entry(element, weight);
			}))
			.all(ABILITY_GEN.then((g, r) -> new ChooseAbilityGen.Entry(g)))
			.errorMessage("Invalid ability")
	);

	static void loadClass(){}
}
