package ttmp.infernoreborn.client.color;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

public class EssenceHolderBookSparkColor implements IItemColor{
	@Override public int getColor(ItemStack stack, int i){
		return i==1 ? ColorUtils.ESSENCE_HOLDER_COLOR.nextColor() : -1;
	}
}
