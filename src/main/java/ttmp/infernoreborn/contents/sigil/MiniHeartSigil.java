package ttmp.infernoreborn.contents.sigil;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import ttmp.infernoreborn.compat.patchouli.sigil.SigilPageBuilder;
import ttmp.infernoreborn.util.SigilSlot;

public class MiniHeartSigil extends AttributeSigil{
	public MiniHeartSigil(Properties properties){
		super(properties);
	}

	@Override protected void applyAttributes(SigilSlot slot, ListMultimap<Attribute, AttributeModifier> attributes){
		addToModifier(attributes, Attributes.MAX_HEALTH, slot==SigilSlot.BODY ? 20 : 4, Operation.ADDITION);
	}

	@Override protected void createSigilBookEntryContent(SigilPageBuilder builder){
		builder.effectsFor(SigilSlot.BODY)
				.attribute(Attributes.MAX_HEALTH, 10, Operation.ADDITION);
		builder.effectsFor(SigilSlot.ARMOR, SigilSlot.CURIO)
				.attribute(Attributes.MAX_HEALTH, 4, Operation.ADDITION);
	}
}
