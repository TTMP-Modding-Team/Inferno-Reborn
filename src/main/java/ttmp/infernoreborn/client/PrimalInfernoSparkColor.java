package ttmp.infernoreborn.client;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

import java.util.Random;

public class PrimalInfernoSparkColor implements IItemColor{
	private static final Random RNG = new Random();

	private final ColorBlender primary = new ColorBlender(2000){
		@Override protected int createNewColor(){
			return ItemColorUtils.hslToRgb(RNG.nextDouble()*360, .75, .9);
		}
	};
	private final ColorBlender secondary = new ColorBlender(200){
		@Override protected int createNewColor(){
			return ItemColorUtils.hslToRgb(RNG.nextDouble()*360, .75, .7);
		}
	};

	@Override public int getColor(ItemStack stack, int i){
		switch(i){
			case 0:
			case 2:
				return primary.nextColor();
			case 1:
				return secondary.nextColor();
			default:
				return -1;
		}
	}
}
