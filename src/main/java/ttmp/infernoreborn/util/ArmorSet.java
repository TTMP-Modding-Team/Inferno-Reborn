package ttmp.infernoreborn.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

public interface ArmorSet{
	boolean qualifies(LivingEntity entity);

	class ItemSet implements ArmorSet{
		private final EnumMap<EquipmentSlotType, Supplier<Item>> items = new EnumMap<>(EquipmentSlotType.class);

		public ItemSet(@Nullable Supplier<Item> head, @Nullable Supplier<Item> chest, @Nullable Supplier<Item> legs, @Nullable Supplier<Item> feet){
			items.put(EquipmentSlotType.HEAD, head);
			items.put(EquipmentSlotType.CHEST, chest);
			items.put(EquipmentSlotType.LEGS, legs);
			items.put(EquipmentSlotType.FEET, feet);
		}
		public ItemSet(Map<EquipmentSlotType, Supplier<Item>> set){
			items.putAll(set);
		}

		@Override public boolean qualifies(LivingEntity entity){
			for(Entry<EquipmentSlotType, Supplier<Item>> e : items.entrySet()){
				if(e.getValue()==null) continue;
				if(entity.getItemBySlot(e.getKey()).getItem()!=e.getValue().get()) return false;
			}
			return true;
		}
	}
}
