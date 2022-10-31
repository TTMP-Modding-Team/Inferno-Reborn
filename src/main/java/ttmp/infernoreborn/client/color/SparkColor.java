package ttmp.infernoreborn.client.color;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.contents.item.ability.FixedAbilityItem;

public class SparkColor implements IItemColor{
	@Override public int getColor(ItemStack stack, int i){
		Ability[] abilities = FixedAbilityItem.getAbilities(stack);

		switch(i){
			case 0: return ColorUtils.getPrimaryColorBlend(abilities);
			case 1: return ColorUtils.getSecondaryColorBlend(abilities);
			case 2: return ColorUtils.getHighlightColorBlend(abilities);
			default: return -1;
		}
	}
}
