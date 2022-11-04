package ttmp.infernoreborn.contents.sigil;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import ttmp.infernoreborn.api.sigil.SigilSlot;
import ttmp.infernoreborn.api.sigil.page.SigilPageBuilder;

public class FaintAfflictionSigil extends AttributeSigil{
	public FaintAfflictionSigil(Properties properties){
		super(properties);
	}

	@Override protected void applyAttributes(SigilSlot slot, ListMultimap<Attribute, AttributeModifier> attributes){
		addToModifier(attributes, Attributes.ATTACK_DAMAGE, 1, Operation.ADDITION);
	}

	@Override protected void createSigilBookEntryContent(SigilPageBuilder builder){
		builder.effectsFor(SigilSlot.BODY, SigilSlot.MAINHAND)
				.attribute(Attributes.ATTACK_DAMAGE, 1, Operation.ADDITION);
	}
}
