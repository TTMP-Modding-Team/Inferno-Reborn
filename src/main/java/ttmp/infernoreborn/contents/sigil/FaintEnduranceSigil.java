package ttmp.infernoreborn.contents.sigil;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import ttmp.infernoreborn.compat.patchouli.sigil.SigilPageBuilder;
import ttmp.infernoreborn.contents.ModAttributes;
import ttmp.infernoreborn.util.SigilSlot;

public class FaintEnduranceSigil extends AttributeSigil{
	public FaintEnduranceSigil(Properties properties){
		super(properties);
	}

	@Override protected void applyAttributes(SigilSlot slot, ListMultimap<Attribute, AttributeModifier> attributes){
		addToModifier(attributes, ModAttributes.DAMAGE_RESISTANCE.get(), .025, Operation.ADDITION);
	}

	@Override protected void createSigilBookEntryContent(SigilPageBuilder builder){
		builder.effectsFor(SigilSlot.BODY, SigilSlot.ARMOR, SigilSlot.CURIO)
				.attribute(ModAttributes.DAMAGE_RESISTANCE.get(), .025, Operation.ADDITION);
	}
}
