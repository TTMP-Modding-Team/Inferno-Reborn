package ttmp.infernoreborn.client.color;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import ttmp.infernoreborn.contents.item.ability.AbilityColorPickerItem;

public class AbilityColorPickerColor implements IItemColor{
	@Override public int getColor(ItemStack stack, int i){
		switch(i){
			case 0: return AbilityColorPickerItem.getPrimaryColor(stack);
			case 1: return AbilityColorPickerItem.getSecondaryColor(stack);
			case 2: return AbilityColorPickerItem.getHighlightColor(stack);
			default: return -1;
		}
	}
}
