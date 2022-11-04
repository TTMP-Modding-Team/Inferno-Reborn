package ttmp.infernoreborn.contents.sigil;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import ttmp.infernoreborn.api.sigil.SigilSlot;
import ttmp.infernoreborn.api.sigil.page.SigilPageBuilder;
import ttmp.infernoreborn.contents.ModAttributes;

public class FeatherFallSigil extends AttributeSigil{
	public FeatherFallSigil(Properties properties){
		super(properties);
	}

	@Override protected void applyAttributes(SigilSlot slot, ListMultimap<Attribute, AttributeModifier> attributes){
		addToModifier(attributes, ModAttributes.FALLING_DAMAGE_RESISTANCE.get(), slot==SigilSlot.BODY ? 1 : .25, AttributeModifier.Operation.MULTIPLY_BASE);
	}

	@Override protected void createSigilBookEntryContent(SigilPageBuilder builder){
		builder.effectsFor(SigilSlot.BODY)
				.attribute(ModAttributes.FALLING_DAMAGE_RESISTANCE.get(), 1, AttributeModifier.Operation.MULTIPLY_BASE);
		builder.effectsFor(SigilSlot.ARMOR, SigilSlot.CURIO)
				.attribute(ModAttributes.FALLING_DAMAGE_RESISTANCE.get(), .25, AttributeModifier.Operation.MULTIPLY_BASE);
	}
}
