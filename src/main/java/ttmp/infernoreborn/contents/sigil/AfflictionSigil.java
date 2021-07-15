package ttmp.infernoreborn.contents.sigil;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import ttmp.infernoreborn.contents.sigil.context.SigilEventContext;
import ttmp.infernoreborn.util.LivingUtils;

import javax.annotation.Nullable;

public class AfflictionSigil extends AttributeSigil{
	public AfflictionSigil(Properties properties){
		super(properties);
	}

	@Override protected boolean canBeAttachedTo(SigilEventContext ctx, @Nullable EquipmentSlotType equipmentSlotType){
		return equipmentSlotType!=EquipmentSlotType.OFFHAND;
	}

	@Override protected void applyAttributes(Mode mode, ListMultimap<Attribute, AttributeModifier> attributes){
		switch(mode){
			case MAINHAND:
				addToModifier(attributes, Attributes.ATTACK_DAMAGE, LivingUtils.getAttackDamageId(), 3, Operation.ADDITION);
				break;
			case BODY:
				addToModifier(attributes, Attributes.ATTACK_DAMAGE, 3, Operation.ADDITION);
				break;
			case HEAD:
			case CHEST:
			case LEGS:
			case FEET:
				addToModifier(attributes, Attributes.ATTACK_DAMAGE, 1, Operation.ADDITION);
				break;
		}
	}
}
