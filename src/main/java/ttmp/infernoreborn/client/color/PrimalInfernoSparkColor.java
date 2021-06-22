package ttmp.infernoreborn.client.color;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

public class PrimalInfernoSparkColor implements IItemColor{
	@Override public int getColor(ItemStack stack, int i){
		switch(i){
			case 0:
			case 2:
				return ColorUtils.PRIMAL_SPARK_PRIMARY_COLOR.nextColor();
			case 1:
				return ColorUtils.PRIMAL_SPARK_SECONDARY_COLOR.nextColor();
			default:
				return -1;
		}
	}
}
