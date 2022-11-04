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

	private static IRecipeType<SigilcraftRecipe> sigilcraftRecipeType;
	private static IRecipeType<FoundryRecipe> foundryRecipeType;
	private static IRecipeType<CrucibleRecipe> crucibleRecipeType;

	public static IRecipeType<SigilcraftRecipe> sigilcraftRecipeType(){
		return sigilcraftRecipeType;
	}
	public static IRecipeType<FoundryRecipe> foundryRecipeType(){
		return foundryRecipeType;
	}
	public static IRecipeType<CrucibleRecipe> crucibleRecipeType(){
		return crucibleRecipeType;
	}

	@SuppressWarnings("unchecked")
	public static void setTypes(){
		RecipeTypes.sigilcraftRecipeType = (IRecipeType<SigilcraftRecipe>)Registry.RECIPE_TYPE.get(new ResourceLocation(MODID+":sigilcraft"));
		RecipeTypes.foundryRecipeType = (IRecipeType<FoundryRecipe>)Registry.RECIPE_TYPE.get(new ResourceLocation(MODID+":foundry"));
		RecipeTypes.crucibleRecipeType = (IRecipeType<CrucibleRecipe>)Registry.RECIPE_TYPE.get(new ResourceLocation(MODID+":crucible"));
	}
}
