package ttmp.infernoreborn.api;

import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import ttmp.infernoreborn.api.crucible.CrucibleRecipe;
import ttmp.infernoreborn.api.foundry.FoundryRecipe;
import ttmp.infernoreborn.api.sigil.SigilcraftRecipe;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

public final class RecipeTypes{
	private RecipeTypes(){}

	private static IRecipeType<SigilcraftRecipe> sigilcraft;
	private static IRecipeType<FoundryRecipe> foundry;
	private static IRecipeType<CrucibleRecipe> crucible;

	public static IRecipeType<SigilcraftRecipe> sigilcraft(){
		return sigilcraft;
	}
	public static IRecipeType<FoundryRecipe> foundry(){
		return foundry;
	}
	public static IRecipeType<CrucibleRecipe> crucible(){
		return crucible;
	}

	@SuppressWarnings("unchecked")
	public static void setTypes(){
		sigilcraft = (IRecipeType<SigilcraftRecipe>)Registry.RECIPE_TYPE.get(new ResourceLocation(MODID+":sigilcraft"));
		foundry = (IRecipeType<FoundryRecipe>)Registry.RECIPE_TYPE.get(new ResourceLocation(MODID+":foundry"));
		crucible = (IRecipeType<CrucibleRecipe>)Registry.RECIPE_TYPE.get(new ResourceLocation(MODID+":crucible"));
	}
}
