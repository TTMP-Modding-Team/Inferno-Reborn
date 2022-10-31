package ttmp.infernoreborn.client.color;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import ttmp.infernoreborn.contents.item.ability.GeneratorAbilityItem;

public class GeneratorSparkColor implements IItemColor{
	@Override public int getColor(ItemStack stack, int i){
		switch(i){
			case 0: return GeneratorAbilityItem.getPrimaryColor(stack, ColorUtils.PRIMAL_SPARK_PRIMARY_COLOR.nextColor());
			case 1: return GeneratorAbilityItem.getSecondaryColor(stack, ColorUtils.PRIMAL_SPARK_SECONDARY_COLOR.nextColor());
			case 2: return GeneratorAbilityItem.getHighlightColor(stack, ColorUtils.PRIMAL_SPARK_PRIMARY_COLOR.nextColor());
			default: return -1;
		}
	}
}
