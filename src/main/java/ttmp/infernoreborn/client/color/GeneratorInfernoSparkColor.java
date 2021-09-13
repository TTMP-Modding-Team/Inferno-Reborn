package ttmp.infernoreborn.client.color;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import ttmp.infernoreborn.contents.item.ability.GeneratorAbilityItem;
import ttmp.infernoreborn.infernaltype.InfernalType;
import ttmp.infernoreborn.infernaltype.ItemDisplay;

public class GeneratorInfernoSparkColor implements IItemColor{
	@Override public int getColor(ItemStack stack, int i){
		switch(i){
			case 0:
				InfernalType type = GeneratorAbilityItem.getType(stack);
				if(type==null) return -1;
				ItemDisplay itemDisplay = type.getItemDisplay();
				if(itemDisplay==null) return -1;
				return itemDisplay.getColor();
			case 1:
				return ColorUtils.PRIMAL_SPARK_SECONDARY_COLOR.nextColor();
			default:
				return -1;
		}
	}
}
