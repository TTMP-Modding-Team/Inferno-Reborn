package ttmp.infernoreborn.contents.item.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import ttmp.infernoreborn.contents.ModAttributes;
import ttmp.infernoreborn.contents.ModItems;

import javax.annotation.Nullable;
import java.util.UUID;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

public class TerrastoneArmorItem extends ArmorItem{
	private static final UUID ATTR = UUID.fromString("f757419d-cf84-40fe-b10c-fe665044ac9c");
	private static final IArmorMaterial MAT = new IArmorMaterial(){
		@Override public int getDurabilityForSlot(EquipmentSlotType slotType){
			return 3000;
		}
		@Override public int getDefenseForSlot(EquipmentSlotType slotType){
			switch(slotType){
				case HEAD:
					return 1;
				case CHEST:
					return 4;
				case LEGS:
					return 4;
				default:
					return 1;
			}
		}
		@Override public int getEnchantmentValue(){
			return 18;
		}
		@Override public SoundEvent getEquipSound(){
			return SoundEvents.ARMOR_EQUIP_GENERIC;
		}
		@Nullable private Ingredient repairIngredient;
		@Override public Ingredient getRepairIngredient(){
			if(repairIngredient==null) repairIngredient = Ingredient.of(ModItems.TERRASTONE.get());
			return repairIngredient;
		}
		@Override public String getName(){
			return MODID+":terrastone";
		}
		@Override public float getToughness(){
			return 0;
		}
		@Override public float getKnockbackResistance(){
			return .5f;
		}
	};

	public TerrastoneArmorItem(EquipmentSlotType slotType, Properties properties){
		super(MAT, slotType, properties);
	}

	@Override public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType slot){
		if(slot!=this.getSlot()) return super.getDefaultAttributeModifiers(slot);
		return ImmutableMultimap.<Attribute, AttributeModifier>builder().putAll(super.getDefaultAttributeModifiers(slot))
				.put(ModAttributes.DAMAGE_RESISTANCE.get(), new AttributeModifier(ATTR, "Armor Modifier", .1, AttributeModifier.Operation.MULTIPLY_BASE))
				.build();
	}
}
