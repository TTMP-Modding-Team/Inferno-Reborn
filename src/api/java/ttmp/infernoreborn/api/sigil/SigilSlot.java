package ttmp.infernoreborn.api.sigil;

import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Locale;

public enum SigilSlot{
	/**
	 * Literally ANY slot, including both item and non-item slots.
	 */
	ANY,
	/**
	 * Body slots. "non-item slot" if you want it to be more generic.
	 */
	BODY,
	/**
	 * Generic item slots. Matches every item regardless its content.
	 */
	ITEM,
	/**
	 * Mainhand item slots, a.k.a weapons and shit
	 */
	MAINHAND,
	/**
	 * Offhand item slots, like shields and such
	 */
	OFFHAND,
	/**
	 * Generic armor slot, includes all 4 armor pieces
	 */
	ARMOR,
	/**
	 * Helmet item slot
	 */
	HEAD,
	/**
	 * Chestplate item slot
	 */
	CHEST,
	/**
	 * Leggings item slot
	 */
	LEGS,
	/**
	 * Boots item slot
	 */
	FEET,
	/**
	 * Generic curio slots
	 */
	CURIO;

	public boolean isAvailableForItem(ItemStack stack){
		switch(this){
			case ANY:
			case ITEM:
				return true;
			case BODY:
				return false;
			case MAINHAND:
				return isEquipmentSlotEquals(stack, EquipmentSlotType.MAINHAND);
			case OFFHAND:
				return isEquipmentSlotEquals(stack, EquipmentSlotType.OFFHAND);
			case ARMOR:
				return isEquipmentSlotArmor(stack);
			case HEAD:
				return isEquipmentSlotEquals(stack, EquipmentSlotType.HEAD);
			case LEGS:
				return isEquipmentSlotEquals(stack, EquipmentSlotType.LEGS);
			case CHEST:
				return isEquipmentSlotEquals(stack, EquipmentSlotType.CHEST);
			case FEET:
				return isEquipmentSlotEquals(stack, EquipmentSlotType.FEET);
			case CURIO:
				return !CuriosApi.getCuriosHelper().getCurioTags(stack.getItem()).isEmpty();
			default:
				throw new IllegalStateException("Unreachable");
		}
	}

	private static boolean isEquipmentSlotEquals(ItemStack stack, EquipmentSlotType equipmentSlotType){
		return MobEntity.getEquipmentSlotForItem(stack)==equipmentSlotType;
	}

	private static boolean isEquipmentSlotArmor(ItemStack stack){
		return MobEntity.getEquipmentSlotForItem(stack).getType()==EquipmentSlotType.Group.ARMOR;
	}

	public boolean isAvailableWithoutItem(){
		return this==ANY||this==BODY;
	}

	public TranslationTextComponent getName(){
		return new TranslationTextComponent("tooltip.infernoreborn.sigil.slot."+this.name().toLowerCase(Locale.ROOT));
	}

	public static SigilSlot of(EquipmentSlotType equipmentSlotType){
		switch(equipmentSlotType){
			case MAINHAND:
				return MAINHAND;
			case OFFHAND:
				return OFFHAND;
			case FEET:
				return FEET;
			case LEGS:
				return LEGS;
			case CHEST:
				return CHEST;
			case HEAD:
				return HEAD;
			default:
				throw new IllegalStateException("Unreachable");
		}
	}
}
