package ttmp.infernoreborn.contents.sigil;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import ttmp.infernoreborn.compat.patchouli.sigil.SigilPageBuilder;
import ttmp.infernoreborn.contents.ModAttributes;
import ttmp.infernoreborn.util.SigilSlot;

public class BlessingOfMercurySigil extends AttributeSigil{
	public BlessingOfMercurySigil(Properties properties){
		super(properties);
	}

	// TODO 1블럭 오르내리기

	@Override protected void applyAttributes(SigilSlot slot, ListMultimap<Attribute, AttributeModifier> attributes){
		if(slot==SigilSlot.BODY){
			addToModifier(attributes, Attributes.MOVEMENT_SPEED, .2, AttributeModifier.Operation.MULTIPLY_BASE);
			addToModifier(attributes, ModAttributes.FALLING_DAMAGE_RESISTANCE.get(), 1, AttributeModifier.Operation.MULTIPLY_BASE);
			addToModifier(attributes, Attributes.ATTACK_SPEED, .25, AttributeModifier.Operation.MULTIPLY_BASE);
		}else if(slot==SigilSlot.MAINHAND){
			addToModifier(attributes, Attributes.ATTACK_SPEED, .5, AttributeModifier.Operation.MULTIPLY_BASE);
		}else{
			addToModifier(attributes, Attributes.MOVEMENT_SPEED, .1, AttributeModifier.Operation.MULTIPLY_BASE);
			addToModifier(attributes, ModAttributes.FALLING_DAMAGE_RESISTANCE.get(), .5, AttributeModifier.Operation.MULTIPLY_BASE);
		}
	}

	@Override protected void createSigilBookEntryContent(SigilPageBuilder builder){
		builder.effectsFor(SigilSlot.BODY)
				.attribute(Attributes.MOVEMENT_SPEED, .2, AttributeModifier.Operation.MULTIPLY_BASE)
				.attribute(ModAttributes.FALLING_DAMAGE_RESISTANCE.get(), 1, AttributeModifier.Operation.MULTIPLY_BASE)
				.attribute(Attributes.ATTACK_SPEED, .25, AttributeModifier.Operation.MULTIPLY_BASE);
		builder.effectsFor(SigilSlot.MAINHAND)
				.attribute(Attributes.ATTACK_SPEED, .5, AttributeModifier.Operation.MULTIPLY_BASE);
		builder.effectsFor(SigilSlot.ARMOR, SigilSlot.CURIO)
				.attribute(Attributes.MOVEMENT_SPEED, .1, AttributeModifier.Operation.MULTIPLY_BASE)
				.attribute(ModAttributes.FALLING_DAMAGE_RESISTANCE.get(), .5, AttributeModifier.Operation.MULTIPLY_BASE);
	}
}
