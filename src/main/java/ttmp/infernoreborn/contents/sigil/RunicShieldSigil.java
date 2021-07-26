package ttmp.infernoreborn.contents.sigil;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import ttmp.infernoreborn.compat.patchouli.sigil.SigilPageBuilder;
import ttmp.infernoreborn.contents.ModAttributes;
import ttmp.infernoreborn.util.SigilSlot;

public class RunicShieldSigil extends AttributeSigil{
	public RunicShieldSigil(Properties properties){
		super(properties);
	}

	@Override protected void applyAttributes(SigilSlot slot, ListMultimap<Attribute, AttributeModifier> attributes){
		addToModifier(attributes, ModAttributes.SHIELD.get(), slot==SigilSlot.BODY ? 10 : 2, Operation.ADDITION);
	}

	@Override protected void createSigilBookEntryContent(SigilPageBuilder builder){
		builder.effectsFor(SigilSlot.BODY)
				.attribute(ModAttributes.SHIELD.get(), 10, Operation.ADDITION);
		builder.effectsFor(SigilSlot.ARMOR, SigilSlot.CURIO)
				.attribute(ModAttributes.SHIELD.get(), 2, Operation.ADDITION);
	}
}
