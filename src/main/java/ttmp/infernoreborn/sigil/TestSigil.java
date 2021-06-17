package ttmp.infernoreborn.sigil;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import ttmp.infernoreborn.sigil.context.SigilEventContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class TestSigil extends Sigil{
	private static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");

	private final AttributeModifier BODY_ATK = new AttributeModifier(UUID.fromString("65111075-953f-461c-b25c-b04ad5e46a82"), "Test Sigil", 1, Operation.ADDITION);
	private final AttributeModifier ARMOR_HEAD_ATK = new AttributeModifier(UUID.fromString("ff771d1d-1324-419d-8a6f-0e074b0518e5"), "Test Sigil", 1, Operation.ADDITION);
	private final AttributeModifier ARMOR_CHEST_ATK = new AttributeModifier(UUID.fromString("e76b93da-21e6-488d-8a59-af0cfbb3121b"), "Test Sigil", 1, Operation.ADDITION);
	private final AttributeModifier ARMOR_LEGS_ATK = new AttributeModifier(UUID.fromString("a360ed35-8a13-4886-8ab1-418d9783b2ea"), "Test Sigil", 1, Operation.ADDITION);
	private final AttributeModifier ARMOR_FEET_ATK = new AttributeModifier(UUID.fromString("d4d7784e-57bb-44d5-be54-39a57587d808"), "Test Sigil", 1, Operation.ADDITION);

	public TestSigil(Properties properties){
		super(properties);
	}

	@Override public boolean canBeAttachedTo(SigilEventContext context){
		return true;
	}
	@Override public void applyAttributes(SigilEventContext ctx, @Nullable EquipmentSlotType equipmentSlotType, ListMultimap<Attribute, AttributeModifier> attributeMap){
		if(equipmentSlotType==null){
			attributeMap.put(Attributes.ATTACK_DAMAGE, BODY_ATK);
			return;
		}
		ctx.withItemContext(itemContext -> {
			ItemStack stack = itemContext.stack();
			EquipmentSlotType equipmentSlotForItem = MobEntity.getEquipmentSlotForItem(stack);
			if(equipmentSlotForItem==equipmentSlotType){
				switch(equipmentSlotType){
					case MAINHAND:
						List<AttributeModifier> list = attributeMap.get(Attributes.ATTACK_DAMAGE);
						for(int i = 0; i<list.size(); i++){
							AttributeModifier m = list.get(i);
							if(m.getId().equals(BASE_ATTACK_DAMAGE_UUID)){
								list.set(i, new AttributeModifier(m.getId(), m.getName(), m.getAmount()+3, m.getOperation()));
								return true;
							}
						}
						attributeMap.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "", 3, Operation.ADDITION));
						break;
					case FEET:
						attributeMap.put(Attributes.ATTACK_DAMAGE, ARMOR_FEET_ATK);
						break;
					case LEGS:
						attributeMap.put(Attributes.ATTACK_DAMAGE, ARMOR_LEGS_ATK);
						break;
					case CHEST:
						attributeMap.put(Attributes.ATTACK_DAMAGE, ARMOR_CHEST_ATK);
						break;
					case HEAD:
						attributeMap.put(Attributes.ATTACK_DAMAGE, ARMOR_HEAD_ATK);
						break;
					// case OFFHAND:
				}
			}
			return true;
		});
	}
}
