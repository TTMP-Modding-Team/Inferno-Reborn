package ttmp.infernoreborn.contents.sigil;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.Hand;
import ttmp.infernoreborn.contents.sigil.context.ItemContext;
import ttmp.infernoreborn.contents.sigil.context.SigilEventContext;
import ttmp.infernoreborn.util.LivingUtils;

import java.util.UUID;

public class AttackDamageSigil extends AttributeSigil{
	private static final AttributeModifier BODY = new AttributeModifier(UUID.fromString("65111075-953f-461c-b25c-b04ad5e46a82"), "", 1, Operation.ADDITION);
	private static final AttributeModifier[] ARMOR_MODIFIERS = new AttributeModifier[4];

	static{
		String[] uuids = {
				"ff771d1d-1324-419d-8a6f-0e074b0518e5",
				"e76b93da-21e6-488d-8a59-af0cfbb3121b",
				"a360ed35-8a13-4886-8ab1-418d9783b2ea",
				"d4d7784e-57bb-44d5-be54-39a57587d808"
		};
		for(int i = 0; i<ARMOR_MODIFIERS.length; i++){
			ARMOR_MODIFIERS[i] = new AttributeModifier(UUID.fromString(uuids[i]), "", 1, Operation.ADDITION);
		}
	}

	public AttackDamageSigil(Properties properties){
		super(properties);
	}

	@Override protected void applyBodyAttributes(SigilEventContext ctx, ListMultimap<Attribute, AttributeModifier> attributes){
		attributes.put(Attributes.ATTACK_DAMAGE, BODY);
	}

	@Override protected void applyHeldAttributes(ItemContext ctx, Hand hand, ListMultimap<Attribute, AttributeModifier> attributes){
		if(hand==Hand.MAIN_HAND)
			LivingUtils.addToModifier(attributes, Attributes.ATTACK_DAMAGE, LivingUtils.getAttackDamageId(), 3, Operation.ADDITION);
	}

	@Override protected void applyArmorAttributes(ItemContext ctx, EquipmentSlotType armorType, ListMultimap<Attribute, AttributeModifier> attributes){
		attributes.put(Attributes.ATTACK_DAMAGE, ARMOR_MODIFIERS[armorType.getIndex()]);
	}
}
