package ttmp.infernoreborn.client.color;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import ttmp.infernoreborn.contents.ability.generator.scheme.AbilityGeneratorScheme;
import ttmp.infernoreborn.contents.ability.generator.scheme.ItemDisplay;
import ttmp.infernoreborn.contents.item.ability.GeneratorAbilityItem;

public class GeneratorInfernoSparkColor implements IItemColor{
	@Override public int getColor(ItemStack stack, int i){
		switch(i){
			case 0:
				AbilityGeneratorScheme scheme = GeneratorAbilityItem.getScheme(stack);
				if(scheme==null) return -1;
				ItemDisplay itemDisplay = scheme.getItemDisplay();
				if(itemDisplay==null) return -1;
				return itemDisplay.getColor();
			case 1:
				return ColorUtils.PRIMAL_SPARK_SECONDARY_COLOR.nextColor();
			default:
				return -1;
		}
	}
}
