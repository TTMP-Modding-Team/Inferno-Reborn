package ttmp.infernoreborn.sigil;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.Hand;
import ttmp.infernoreborn.sigil.context.ItemContext;
import ttmp.infernoreborn.sigil.context.SigilEventContext;

import javax.annotation.Nullable;

public abstract class AttributeSigil extends Sigil{
	public AttributeSigil(Properties properties){
		super(properties);
	}

	@Override public void applyAttributes(SigilEventContext ctx, @Nullable EquipmentSlotType equipmentSlotType, ListMultimap<Attribute, AttributeModifier> attributes){
		if(equipmentSlotType==null) applyBodyAttributes(ctx, attributes);
		else{
			ItemContext itemContext = ctx.getAsItemContext();
			if(itemContext==null||!shouldApplyAttribute(itemContext, equipmentSlotType)) return;
			switch(equipmentSlotType){
				case MAINHAND:
					applyHeldAttributes(itemContext, Hand.MAIN_HAND, attributes);
					break;
				case OFFHAND:
					applyHeldAttributes(itemContext, Hand.OFF_HAND, attributes);
					break;
				case HEAD:
				case LEGS:
				case CHEST:
				case FEET:
					applyArmorAttributes(itemContext, equipmentSlotType, attributes);
					break;
			}
		}
	}

	protected abstract void applyBodyAttributes(SigilEventContext ctx, ListMultimap<Attribute, AttributeModifier> attributes);
	protected abstract void applyHeldAttributes(ItemContext ctx, Hand hand, ListMultimap<Attribute, AttributeModifier> attributes);
	protected abstract void applyArmorAttributes(ItemContext ctx, EquipmentSlotType armorType, ListMultimap<Attribute, AttributeModifier> attributes);

	protected boolean shouldApplyAttribute(ItemContext ctx, EquipmentSlotType equipmentSlotType){
		EquipmentSlotType equipmentSlotForItem = MobEntity.getEquipmentSlotForItem(ctx.stack());
		return equipmentSlotForItem==equipmentSlotType;
	}
}
