package ttmp.infernoreborn.contents.sigil;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import ttmp.infernoreborn.compat.patchouli.sigil.SigilPageBuilder;
import ttmp.infernoreborn.util.LivingUtils;
import ttmp.infernoreborn.util.SigilSlot;

public class AfflictionSigil extends AttributeSigil{
	public AfflictionSigil(Properties properties){
		super(properties);
	}

	@Override protected void applyAttributes(SigilSlot slot, ListMultimap<Attribute, AttributeModifier> attributes){
		switch(slot){
			case MAINHAND:
				addToModifier(attributes, Attributes.ATTACK_DAMAGE, LivingUtils.getAttackDamageId(), 3, Operation.ADDITION);
				break;
			case BODY:
				addToModifier(attributes, Attributes.ATTACK_DAMAGE, 3, Operation.ADDITION);
				break;
			default:
				addToModifier(attributes, Attributes.ATTACK_DAMAGE, 1, Operation.ADDITION);
		}
	}

	@Override protected void createSigilBookEntryContent(SigilPageBuilder builder){
		builder.effectsFor(SigilSlot.MAINHAND, SigilSlot.BODY)
				.attribute(Attributes.ATTACK_DAMAGE, 3, Operation.ADDITION);
		builder.effectsFor(SigilSlot.ARMOR, SigilSlot.CURIO)
				.attribute(Attributes.ATTACK_DAMAGE, 1, Operation.ADDITION);
	}
}
