package ttmp.infernoreborn.contents.sigil;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import ttmp.infernoreborn.compat.patchouli.sigil.SigilPageBuilder;
import ttmp.infernoreborn.contents.ModAttributes;
import ttmp.infernoreborn.util.SigilSlot;

public class EnduranceSigil extends AttributeSigil{
	public EnduranceSigil(Properties properties){
		super(properties);
	}

	@Override protected void applyAttributes(SigilSlot slot, ListMultimap<Attribute, AttributeModifier> attributes){
		addToModifier(attributes, ModAttributes.DAMAGE_RESISTANCE.get(), slot==SigilSlot.BODY ? .1 : .05, Operation.MULTIPLY_BASE);
	}

	@Override protected void createSigilBookEntryContent(SigilPageBuilder builder){
		builder.effectsFor(SigilSlot.BODY)
				.attribute(ModAttributes.DAMAGE_RESISTANCE.get(), .1, Operation.MULTIPLY_BASE);
		builder.effectsFor(SigilSlot.ARMOR, SigilSlot.CURIO)
				.attribute(ModAttributes.DAMAGE_RESISTANCE.get(), .05, Operation.MULTIPLY_BASE);
	}
}
