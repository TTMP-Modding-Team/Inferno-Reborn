package ttmp.infernoreborn.contents.item;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import ttmp.infernoreborn.contents.ModTags;

import javax.annotation.Nullable;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public class BerserkerArmorItem extends ArmorItem{
	private static final IArmorMaterial MAT = new IArmorMaterial(){
		@Override public int getDurabilityForSlot(EquipmentSlotType slotType){
			switch(slotType){
				case LEGS:
					return 45*15;
				case CHEST:
					return 45*16;
				default:
					return 45*13;
			}
		}
		@Override public int getDefenseForSlot(EquipmentSlotType slotType){
			switch(slotType){
				case LEGS:
					return 6;
				case CHEST:
					return 8;
				default:
					return 3;
			}
		}
		@Override public int getEnchantmentValue(){
			return 9;
		}
		@Override public SoundEvent getEquipSound(){
			return SoundEvents.ARMOR_EQUIP_GENERIC;
		}
		@Nullable private Ingredient repairIngredient;
		@Override public Ingredient getRepairIngredient(){
			if(repairIngredient==null) repairIngredient = Ingredient.of(ModTags.INGOTS_DAMASCUS_STEEL);
			return repairIngredient;
		}
		@Override public String getName(){
			return MODID+":berserker_armor";
		}
		@Override public float getToughness(){
			return 2;
		}
		@Override public float getKnockbackResistance(){
			return .1f;
		}
	};

	public BerserkerArmorItem(EquipmentSlotType slotType, Properties properties){
		super(MAT, slotType, properties);
	}
}
