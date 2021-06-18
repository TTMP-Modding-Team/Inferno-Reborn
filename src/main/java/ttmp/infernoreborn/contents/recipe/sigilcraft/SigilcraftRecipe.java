package ttmp.infernoreborn.contents.recipe.sigilcraft;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import ttmp.infernoreborn.contents.ModRecipes;
import ttmp.infernoreborn.inventory.SigilcraftInventory;

public interface SigilcraftRecipe extends IRecipe<SigilcraftInventory>{
	@Override default IRecipeType<?> getType(){
		return ModRecipes.SIGILCRAFT_RECIPE_TYPE;
	}
}
