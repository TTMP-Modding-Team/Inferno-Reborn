package ttmp.infernoreborn.contents.sigil;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.inventory.EquipmentSlotType;
import ttmp.infernoreborn.contents.ModAttributes;
import ttmp.infernoreborn.contents.sigil.context.SigilEventContext;

import javax.annotation.Nullable;

public class EnduranceSigil extends AttributeSigil{
	public EnduranceSigil(Properties properties){
		super(properties);
	}

	@Override protected boolean canBeAttachedTo(SigilEventContext ctx, @Nullable EquipmentSlotType equipmentSlotType){
		return equipmentSlotType==null||equipmentSlotType.getType()==EquipmentSlotType.Group.ARMOR;
	}

	@Override protected void applyAttributes(Mode mode, ListMultimap<Attribute, AttributeModifier> attributes){
		switch(mode){
			case BODY:
				addToModifier(attributes, ModAttributes.DAMAGE_RESISTANCE.get(), .1, Operation.MULTIPLY_BASE);
				break;
			case HEAD:
			case CHEST:
			case LEGS:
			case FEET:
				addToModifier(attributes, ModAttributes.DAMAGE_RESISTANCE.get(), .05, Operation.MULTIPLY_BASE);
				break;
		}
	}
}
