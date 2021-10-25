package ttmp.infernoreborn.compat.jei.sigil;

import net.minecraft.item.ItemStack;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.recipe.sigilcraft.BaseSigilcraftRecipe;

public enum RecipeSize{
	X3(3, 0, 0, 92, 54),
	X5(5, 256-128, 0, 128, 90),
	X7(7, 0, 256-126, 164, 126);

	public final int size;
	public final int backgroundU;
	public final int backgroundV;
	public final int backgroundWidth;
	public final int backgroundHeight;

	RecipeSize(int size,
	           int backgroundU,
	           int backgroundV,
	           int backgroundWidth,
	           int backgroundHeight){
		this.size = size;
		this.backgroundU = backgroundU;
		this.backgroundV = backgroundV;
		this.backgroundWidth = backgroundWidth;
		this.backgroundHeight = backgroundHeight;
	}

	public ItemStack icon(){
		switch(this){
			case X3:
				return new ItemStack(ModItems.SIGIL_ENGRAVING_TABLE_3X3.get());
			case X5:
				return new ItemStack(ModItems.SIGIL_ENGRAVING_TABLE_5X5.get());
			default: // case X7:
				return new ItemStack(ModItems.SIGIL_ENGRAVING_TABLE_7X7.get());
		}
	}

	public int indexToX(int pos){
		return BaseSigilcraftRecipe.indexToX(pos, size);
	}
	public int indexToY(int pos){
		return BaseSigilcraftRecipe.indexToY(pos, size);
	}
	public int toIndex(int x, int y){
		return BaseSigilcraftRecipe.toIndex(x, y, size);
	}

	public int centerIndex(){
		return toIndex(size/2, size/2);
	}
}
