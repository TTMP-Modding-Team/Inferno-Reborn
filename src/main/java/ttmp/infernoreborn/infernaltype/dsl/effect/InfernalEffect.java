package ttmp.infernoreborn.infernaltype.dsl.effect;

import among.TypeFlags;
import among.construct.ConditionedConstructor;
import among.construct.ConstructRule;
import among.construct.Constructor;
import among.obj.Among;
import net.minecraft.network.PacketBuffer;
import org.apache.commons.lang3.ArrayUtils;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;

public interface InfernalEffect{
	void apply(InfernalGenContext context);

	static ParticleEffect particle(int[] colors){
		return new ParticleEffect(colors);
	}

	Constructor<Among, InfernalEffect> INFERNAL_EFFECT = ConstructRule.make(_b -> _b
			.list("Particle", ConditionedConstructor.listCondition(c -> c
							.minSize(1)
							.elementType(TypeFlags.PRIMITIVE),
					Constructor.listOf(
							Constructor.tryConstruct(
									Constructor.generifyValue((a, _r) -> {
										int i = Integer.parseUnsignedInt(a.getValue(), 16);
										if(i>=0&&i<=0xFFFFFF) return i;
										if(_r!=null) _r.reportError("Invalid color", a.sourcePosition());
										return null;
									}), "Invalid color", false)
					).then((ints, reportHandler) -> particle(ArrayUtils.toPrimitive(ints.toArray(new Integer[0]))))))
	);
}
