package ttmp.infernoreborn.util;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import ttmp.infernoreborn.contents.sigil.Sigil;
import ttmp.infernoreborn.contents.sigil.context.SigilEventContext;
import ttmp.infernoreborn.contents.sigil.holder.SigilHolder;

import javax.annotation.Nullable;

public final class SigilUtils{
	private SigilUtils(){}

	public static void applyAttributes(SigilHolder sigilHolder, @Nullable EquipmentSlotType equipmentSlotType, ListMultimap<Attribute, AttributeModifier> modifierMap){
		if(sigilHolder.isEmpty()) return;
		SigilEventContext context = sigilHolder.createContext();
		for(Sigil sigil : sigilHolder.getSigils()) sigil.applyAttributes(context, equipmentSlotType, modifierMap);
	}
}
