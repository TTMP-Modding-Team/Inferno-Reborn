package ttmp.infernoreborn.contents.item.armor;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import ttmp.infernoreborn.contents.ModItems;

import javax.annotation.Nullable;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public class CrimsonArmorItem extends ArmorItem{
	private static final IArmorMaterial MAT = new IArmorMaterial(){
		@Override public int getDurabilityForSlot(EquipmentSlotType slotType){
			return 3000;
		}
		@Override public int getDefenseForSlot(EquipmentSlotType slotType){
			switch(slotType){
				case HEAD:
					return 0;
				case CHEST:
					return 8;
				case LEGS:
					return 7;
				default:
					return 4;
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
			if(repairIngredient==null) repairIngredient = Ingredient.of(ModItems.CRIMSON_METAL_SCRAP.get());
			return repairIngredient;
		}
		@Override public String getName(){
			return MODID+":crimson_armor";
		}
		@Override public float getToughness(){
			return 1;
		}
		@Override public float getKnockbackResistance(){
			return 0;
		}
	};

	public CrimsonArmorItem(EquipmentSlotType slotType, Properties properties){
		super(MAT, slotType, properties);
	}
}
