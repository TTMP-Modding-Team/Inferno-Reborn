package ttmp.infernoreborn.client.color;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

import java.util.Random;

public class EssenceHolderBookSparkColor implements IItemColor{
	private static final Random RNG = new Random();

	private final ColorBlender color = new ColorBlender(2000){
		@Override protected int createNewColor(){
			return ColorUtils.hslToRgb(RNG.nextDouble()*360, .75, .5);
		}
	};

	@Override public int getColor(ItemStack stack, int i){
		return i==1 ? color.nextColor() : -1;
	}
}
