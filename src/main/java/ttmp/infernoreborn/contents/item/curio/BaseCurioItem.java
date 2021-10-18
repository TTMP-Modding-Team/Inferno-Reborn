package ttmp.infernoreborn.contents.item.curio;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class BaseCurioItem extends Item implements ICurioItem{
	public BaseCurioItem(Properties properties){
		super(properties);
	}

	@Override public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack){
		return true;
	}
}
