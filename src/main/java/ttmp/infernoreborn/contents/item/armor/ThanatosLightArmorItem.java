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

public class ThanatosLightArmorItem extends ArmorItem {
    private static final IArmorMaterial MAT = new IArmorMaterial() {
        @Override
        public int getDurabilityForSlot(EquipmentSlotType slotType) {
            switch (slotType) {
                case FEET:
                    return 37 * 13;
                case LEGS:
                    return 37 * 15;
                case CHEST:
                    return 37 * 16;
                default:
                    return 0;
            }
        }

        @Override
        public int getDefenseForSlot(EquipmentSlotType slotType) {
            switch (slotType) {
                case FEET:
                    return 3;
                case LEGS:
                    return 7;
                case CHEST:
                    return 8;
                default:
                    return 0;
            }
        }

        @Override
        public int getEnchantmentValue() {
            return 18;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_GENERIC;
        }

        @Nullable
        private Ingredient repairIngredient;

        @Override
        public Ingredient getRepairIngredient() {
            if (repairIngredient == null) repairIngredient = Ingredient.of(ModItems.DEATH_INFUSED_LEATHER.get());
            return repairIngredient;
        }

        @Override
        public String getName() {
            return MODID + ":thanatos_light_armor";
        }

        @Override
        public float getToughness() {
            return 1;
        }

        @Override
        public float getKnockbackResistance() {
            return 0;
        }
    };

    public ThanatosLightArmorItem(EquipmentSlotType slotType, Properties properties) {
        super(MAT, slotType, properties);
    }
}