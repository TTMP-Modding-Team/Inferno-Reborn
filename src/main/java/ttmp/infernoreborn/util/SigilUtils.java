package ttmp.infernoreborn.util;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import ttmp.infernoreborn.contents.sigil.Sigil;
import ttmp.infernoreborn.contents.sigil.context.SigilEventContext;
import ttmp.infernoreborn.contents.sigil.holder.SigilHolder;

public final class SigilUtils{
	private SigilUtils(){}

	public static void applyAttributes(SigilHolder sigilHolder, SigilSlot slot, ListMultimap<Attribute, AttributeModifier> modifierMap){
		if(sigilHolder.isEmpty()) return;
		SigilEventContext context = sigilHolder.createContext();
		for(Sigil sigil : sigilHolder.getSigils()) sigil.applyAttributes(context, slot, modifierMap);
	}
}
