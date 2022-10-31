package ttmp.infernoreborn.infernaltype.dsl;

import among.construct.Constructor;
import among.obj.AmongPrimitive;


public final class InfernalTypeDsl{
	private InfernalTypeDsl(){}

	public static final Constructor<AmongPrimitive, Integer> RGB = Constructor.tryConstruct((a, r) -> {
		int i = Integer.parseUnsignedInt(a.getValue(), 16);
		if(i>=0&&i<=0xFFFFFF) return i;
		if(r!=null) r.reportError("Invalid RGB color", a.sourcePosition());
		return null;
	}, "Invalid RGB color", false);
}
