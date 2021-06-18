package ttmp.infernoreborn.contents.sigil;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.Hand;
import ttmp.infernoreborn.contents.ModAttributes;
import ttmp.infernoreborn.contents.sigil.context.ItemContext;
import ttmp.infernoreborn.contents.sigil.context.SigilEventContext;

import java.util.UUID;

public class ShieldSigil extends AttributeSigil{
	private static final AttributeModifier BODY = new AttributeModifier(UUID.fromString("e58e11be-7912-4d44-80ed-37181c624455"), "", 10, Operation.ADDITION);
	private static final AttributeModifier[] ARMOR_MODIFIERS = new AttributeModifier[4];

	static{
		String[] uuids = {
				"3c60c4ea-6ad2-41f8-bb8b-007f6eb027db",
				"02b1a056-e0ca-4f29-82cb-ad294ab5a185",
				"ed9ba95e-52fc-4d49-b42d-caa3d8a434d0",
				"0588b338-15d9-4bf2-809c-4c7c6d8a18f6"
		};
		for(int i = 0; i<ARMOR_MODIFIERS.length; i++){
			ARMOR_MODIFIERS[i] = new AttributeModifier(UUID.fromString(uuids[i]), "", 5, Operation.ADDITION);
		}
	}

	public ShieldSigil(Properties properties){
		super(properties);
	}

	@Override public boolean canBeAttachedTo(SigilEventContext context){
		ItemContext itemContext = context.getAsItemContext();
		if(itemContext==null) return context.isLivingContext();
		for(EquipmentSlotType slotType : EquipmentSlotType.values()){
			if(slotType.getType()==EquipmentSlotType.Group.ARMOR&&shouldApplyAttribute(itemContext, slotType)) return true;
		}
		return false;
	}

	@Override protected boolean shouldApplyAttribute(ItemContext ctx, EquipmentSlotType equipmentSlotType){
		return equipmentSlotType.getType()==EquipmentSlotType.Group.ARMOR&&super.shouldApplyAttribute(ctx, equipmentSlotType);
	}

	@Override protected void applyBodyAttributes(SigilEventContext ctx, ListMultimap<Attribute, AttributeModifier> attributes){
		attributes.put(ModAttributes.SHIELD.get(), BODY);
	}

	@Override protected void applyHeldAttributes(ItemContext ctx, Hand hand, ListMultimap<Attribute, AttributeModifier> attributes){}

	@Override protected void applyArmorAttributes(ItemContext ctx, EquipmentSlotType armorType, ListMultimap<Attribute, AttributeModifier> attributes){
		attributes.put(ModAttributes.SHIELD.get(), ARMOR_MODIFIERS[armorType.getIndex()]);
	}
}
