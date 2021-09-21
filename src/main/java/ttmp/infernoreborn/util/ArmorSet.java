package ttmp.infernoreborn.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ArmorSet{
	@Nullable private final Supplier<Item> head;
	@Nullable private final Supplier<Item> chest;
	@Nullable private final Supplier<Item> legs;
	@Nullable private final Supplier<Item> feet;

	public ArmorSet(@Nullable Supplier<Item> head, @Nullable Supplier<Item> chest, @Nullable Supplier<Item> legs, @Nullable Supplier<Item> feet){
		this.head = head;
		this.chest = chest;
		this.legs = legs;
		this.feet = feet;
	}

	public boolean uses(EquipmentSlotType type){
		switch(type){
			case FEET:
				return feet!=null;
			case LEGS:
				return legs!=null;
			case CHEST:
				return chest!=null;
			case HEAD:
				return head!=null;
			default:
				throw new IllegalArgumentException("type");
		}
	}

	public boolean qualifies(LivingEntity entity){
		return (head==null||entity.getItemBySlot(EquipmentSlotType.HEAD).getItem()==head.get())&&
				(chest==null||entity.getItemBySlot(EquipmentSlotType.CHEST).getItem()==chest.get())&&
				(legs==null||entity.getItemBySlot(EquipmentSlotType.LEGS).getItem()==legs.get())&&
				(feet==null||entity.getItemBySlot(EquipmentSlotType.FEET).getItem()==feet.get());
	}
}
