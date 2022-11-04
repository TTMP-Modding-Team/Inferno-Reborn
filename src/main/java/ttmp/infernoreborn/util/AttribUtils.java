package ttmp.infernoreborn.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import ttmp.infernoreborn.InfernoReborn;

public final class AttribUtils{
	private AttribUtils(){}

	public static double getAttrib(LivingEntity entity, Attribute attrib){
		ModifiableAttributeInstance a1 = entity.getAttribute(attrib);
		if(a1!=null) return a1.getValue();
		InfernoReborn.LOGGER.warn("Cannot find {} from {}", attrib, entity);
		return attrib.getDefaultValue();
	}
}
