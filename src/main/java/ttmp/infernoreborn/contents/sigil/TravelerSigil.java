package ttmp.infernoreborn.contents.sigil;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import ttmp.infernoreborn.compat.patchouli.sigil.SigilPageBuilder;
import ttmp.infernoreborn.contents.ModAttributes;
import ttmp.infernoreborn.util.SigilSlot;

public class TravelerSigil extends AttributeSigil{
	public TravelerSigil(Properties properties){
		super(properties);
	}

	@Override protected void applyAttributes(SigilSlot slot, ListMultimap<Attribute, AttributeModifier> attributes){
		addToModifier(attributes, Attributes.MOVEMENT_SPEED, slot==SigilSlot.BODY ? .1 : .05, Operation.MULTIPLY_BASE);
	}

	@Override protected void createSigilBookEntryContent(SigilPageBuilder builder){
		builder.effectsFor(SigilSlot.BODY)
				.attribute(Attributes.MOVEMENT_SPEED, .1, Operation.MULTIPLY_BASE);
		builder.effectsForArmor(SigilSlot.CURIO)
				.attribute(Attributes.MOVEMENT_SPEED, .05, Operation.MULTIPLY_BASE);
	}
}
