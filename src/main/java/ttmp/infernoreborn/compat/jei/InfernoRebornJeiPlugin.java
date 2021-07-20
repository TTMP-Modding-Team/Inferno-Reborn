package ttmp.infernoreborn.compat.jei;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.client.screen.SigilEngravingTableScreen;
import ttmp.infernoreborn.compat.jei.sigil.RecipeSize;
import ttmp.infernoreborn.compat.jei.sigil.ShapedSigilEngravingRecipeCategory;
import ttmp.infernoreborn.compat.jei.sigil.ShapedSigilTableCraftingRecipeCategory;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.ModRecipes;
import ttmp.infernoreborn.contents.item.FixedAbilityItem;
import ttmp.infernoreborn.contents.item.GeneratorAbilityItem;
import ttmp.infernoreborn.contents.item.SigilItem;
import ttmp.infernoreborn.contents.recipe.sigilcraft.BaseSigilcraftRecipe;
import ttmp.infernoreborn.contents.recipe.sigilcraft.ShapedSigilEngravingRecipe;
import ttmp.infernoreborn.contents.recipe.sigilcraft.ShapedSigilTableCraftingRecipe;
import ttmp.infernoreborn.contents.recipe.sigilcraft.SigilcraftRecipe;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ttmp.infernoreborn.InfernoReborn.MODID;

@JeiPlugin
public class InfernoRebornJeiPlugin implements IModPlugin{
	@Override public ResourceLocation getPluginUid(){
		return new ResourceLocation(MODID, MODID);
	}
	@Override public void registerItemSubtypes(ISubtypeRegistration registration){
		registration.registerSubtypeInterpreter(ModItems.INFERNO_SPARK.get(), (stack, context) -> Arrays.toString(FixedAbilityItem.getAbilities(stack)));
		registration.registerSubtypeInterpreter(ModItems.GENERATOR_INFERNO_SPARK.get(), (stack, context) -> String.valueOf(GeneratorAbilityItem.getGenerator(stack)));
		registration.registerSubtypeInterpreter(ModItems.SIGIL.get(), (stack, context) -> String.valueOf(SigilItem.getSigil(stack)));
	}
	@Override public void registerCategories(IRecipeCategoryRegistration registration){
		IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
		registration.addRecipeCategories(
				new ShapedSigilEngravingRecipeCategory(guiHelper, RecipeSize.X3),
				new ShapedSigilEngravingRecipeCategory(guiHelper, RecipeSize.X5),
				new ShapedSigilEngravingRecipeCategory(guiHelper, RecipeSize.X7),
				new ShapedSigilTableCraftingRecipeCategory(guiHelper, RecipeSize.X3),
				new ShapedSigilTableCraftingRecipeCategory(guiHelper, RecipeSize.X5),
				new ShapedSigilTableCraftingRecipeCategory(guiHelper, RecipeSize.X7)
		);
	}
	@Override public void registerRecipes(IRecipeRegistration registration){
		ClientWorld world = Minecraft.getInstance().level;
		if(world==null) return;

		Multimap<RecipeSize, ShapedSigilEngravingRecipe> sigilEngravingRecipes = ArrayListMultimap.create();
		Multimap<RecipeSize, ShapedSigilTableCraftingRecipe> sigilTableCraftingRecipes = ArrayListMultimap.create();

		for(SigilcraftRecipe recipe : world.getRecipeManager().getAllRecipesFor(ModRecipes.SIGILCRAFT_RECIPE_TYPE)){
			if(recipe instanceof ShapedSigilEngravingRecipe){
				ShapedSigilEngravingRecipe r = (ShapedSigilEngravingRecipe)recipe;
				RecipeSize recipeSize = getRecipeSize(r);
				if(recipeSize!=null) sigilEngravingRecipes.put(recipeSize, r);
			}else if(recipe instanceof ShapedSigilTableCraftingRecipe){
				ShapedSigilTableCraftingRecipe r = (ShapedSigilTableCraftingRecipe)recipe;
				RecipeSize recipeSize = getRecipeSize(r);
				if(recipeSize!=null) sigilTableCraftingRecipes.put(recipeSize, r);
			}
		}
		for(RecipeSize s : RecipeSize.values()){
			registration.addRecipes(sigilEngravingRecipes.get(s), ShapedSigilEngravingRecipeCategory.getUidBySize(s));
			registration.addRecipes(sigilTableCraftingRecipes.get(s), ShapedSigilTableCraftingRecipeCategory.getUidBySize(s));
		}
	}

	@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration){
		for(RecipeSize size : RecipeSize.values()){
			List<ResourceLocation> recipeCategories = new ArrayList<>();
			for(RecipeSize size2 : RecipeSize.values()){
				recipeCategories.add(ShapedSigilEngravingRecipeCategory.getUidBySize(size2));
				recipeCategories.add(ShapedSigilTableCraftingRecipeCategory.getUidBySize(size2));
				if(size==size2) break;
			}
			registration.addRecipeCatalyst(size.icon(), recipeCategories.toArray(new ResourceLocation[0]));
		}
	}

	@Override public void registerGuiHandlers(IGuiHandlerRegistration registration){
		registration.addRecipeClickArea(SigilEngravingTableScreen.X3.class, 137, 57, 10, 4, ShapedSigilEngravingRecipeCategory.getUidBySize(RecipeSize.X3), ShapedSigilTableCraftingRecipeCategory.getUidBySize(RecipeSize.X3));
		registration.addRecipeClickArea(SigilEngravingTableScreen.X5.class, 137, 93, 10, 4, ShapedSigilEngravingRecipeCategory.getUidBySize(RecipeSize.X5), ShapedSigilTableCraftingRecipeCategory.getUidBySize(RecipeSize.X5));
		registration.addRecipeClickArea(SigilEngravingTableScreen.X7.class, 144, 119, 10, 4, ShapedSigilEngravingRecipeCategory.getUidBySize(RecipeSize.X7), ShapedSigilTableCraftingRecipeCategory.getUidBySize(RecipeSize.X7));
	}
	@Nullable private RecipeSize getRecipeSize(BaseSigilcraftRecipe recipe){
		int height = recipe.getRecipeHeight();
		int width = recipe.getRecipeWidth();
		switch(Math.max(Math.max(recipe.getCenterX(), width-1-recipe.getCenterX()), Math.max(recipe.getCenterY(), height-1-recipe.getCenterY()))){
			case 0:
			case 1:
				return RecipeSize.X3;
			case 2:
				return RecipeSize.X5;
			case 3:
				return RecipeSize.X7;
			default:
				return null;
		}
	}
}
