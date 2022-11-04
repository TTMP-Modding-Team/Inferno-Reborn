package ttmp.infernoreborn.api.sigil;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import ttmp.infernoreborn.api.RecipeTypes;

public interface SigilcraftRecipe extends IRecipe<SigilcraftInventory>{
	@Override default IRecipeType<?> getType(){
		return RecipeTypes.sigilcraft();
	}
}
