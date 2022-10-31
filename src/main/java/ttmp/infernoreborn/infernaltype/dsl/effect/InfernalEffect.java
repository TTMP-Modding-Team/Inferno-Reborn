package ttmp.infernoreborn.infernaltype.dsl.effect;

import among.TypeFlags;
import among.construct.ConditionedConstructor;
import among.construct.ConstructRule;
import among.construct.Constructor;
import among.obj.Among;
import org.apache.commons.lang3.ArrayUtils;
import ttmp.infernoreborn.infernaltype.InfernalGenContext;
import ttmp.infernoreborn.infernaltype.dsl.InfernalTypeDsl;

public interface InfernalEffect{
	void apply(InfernalGenContext context);

	static ParticleEffect particle(int[] colors){
		return new ParticleEffect(colors);
	}

	Constructor<Among, InfernalEffect> INFERNAL_EFFECT = ConstructRule.make(_b -> _b
			.list("Particle", ConditionedConstructor.listCondition(c -> c
							.minSize(1)
							.elementType(TypeFlags.PRIMITIVE),
					Constructor.listOf(Constructor.generifyValue(InfernalTypeDsl.RGB))
							.then((ints, reportHandler) ->
									particle(ArrayUtils.toPrimitive(ints.toArray(new Integer[0]))))))
	);
}
