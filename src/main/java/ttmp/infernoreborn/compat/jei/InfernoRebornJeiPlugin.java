package ttmp.infernoreborn.compat.jei;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
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

import java.util.Arrays;

import static ttmp.infernoreborn.InfernoReborn.MODID;

@JeiPlugin
public class InfernoRebornJeiPlugin implements IModPlugin{
	private final Multimap<RecipeSize, SigilcraftRecipe> SHAPED_SIGIL_CRAFTING_MAP = ArrayListMultimap.create();
	private final Multimap<RecipeSize, SigilcraftRecipe> SHAPED_SIGIL_ENGRAVING_MAP = ArrayListMultimap.create();

	@Override public ResourceLocation getPluginUid(){
		return new ResourceLocation(MODID, "jeiplugin");
	}
	@Override public void registerItemSubtypes(ISubtypeRegistration registration){
		registration.registerSubtypeInterpreter(ModItems.INFERNO_SPARK.get(), (stack, context) -> Arrays.toString(FixedAbilityItem.getAbilities(stack)));
		registration.registerSubtypeInterpreter(ModItems.GENERATOR_INFERNO_SPARK.get(), (stack, context) -> String.valueOf(GeneratorAbilityItem.getGenerator(stack)));
		registration.registerSubtypeInterpreter(ModItems.SIGIL.get(), (stack, context) -> String.valueOf(SigilItem.getSigil(stack)));
	}
	@Override public void registerCategories(IRecipeCategoryRegistration registration){
		IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
		registration.addRecipeCategories(
				new ShapedSigilEngravingRecipeCategory(guiHelper, RecipeSize.SIZE_3X3),
				new ShapedSigilEngravingRecipeCategory(guiHelper, RecipeSize.SIZE_5X5),
				new ShapedSigilEngravingRecipeCategory(guiHelper, RecipeSize.SIZE_7X7),
				new ShapedSigilTableCraftingRecipeCategory(guiHelper, RecipeSize.SIZE_3X3),
				new ShapedSigilTableCraftingRecipeCategory(guiHelper, RecipeSize.SIZE_5X5),
				new ShapedSigilTableCraftingRecipeCategory(guiHelper, RecipeSize.SIZE_7X7)
		);
	}
	@Override public void registerRecipes(IRecipeRegistration registration){
		World world = Minecraft.getInstance().level;
		if(world!=null){
			for(SigilcraftRecipe recipe : world.getRecipeManager().getAllRecipesFor(ModRecipes.SIGILCRAFT_RECIPE_TYPE))
				putSigilcraftRecipe(recipe);
		}
		registration.addRecipes(SHAPED_SIGIL_CRAFTING_MAP.get(RecipeSize.SIZE_3X3), ShapedSigilTableCraftingRecipeCategory.getUidBySize(3));
		registration.addRecipes(SHAPED_SIGIL_CRAFTING_MAP.get(RecipeSize.SIZE_5X5), ShapedSigilTableCraftingRecipeCategory.getUidBySize(5));
		registration.addRecipes(SHAPED_SIGIL_CRAFTING_MAP.get(RecipeSize.SIZE_7X7), ShapedSigilTableCraftingRecipeCategory.getUidBySize(7));
		registration.addRecipes(SHAPED_SIGIL_ENGRAVING_MAP.get(RecipeSize.SIZE_3X3), ShapedSigilEngravingRecipeCategory.getUidBySize(3));
		registration.addRecipes(SHAPED_SIGIL_ENGRAVING_MAP.get(RecipeSize.SIZE_5X5), ShapedSigilEngravingRecipeCategory.getUidBySize(5));
		registration.addRecipes(SHAPED_SIGIL_ENGRAVING_MAP.get(RecipeSize.SIZE_7X7), ShapedSigilEngravingRecipeCategory.getUidBySize(7));

	}

	@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration){
		registration.addRecipeCatalyst(new ItemStack(ModItems.SIGIL_ENGRAVING_TABLE_3X3.get()), ShapedSigilEngravingRecipeCategory.getUidBySize(3), ShapedSigilTableCraftingRecipeCategory.getUidBySize(3));
		registration.addRecipeCatalyst(new ItemStack(ModItems.SIGIL_ENGRAVING_TABLE_5X5.get()), ShapedSigilEngravingRecipeCategory.getUidBySize(3), ShapedSigilEngravingRecipeCategory.getUidBySize(5), ShapedSigilTableCraftingRecipeCategory.getUidBySize(3), ShapedSigilTableCraftingRecipeCategory.getUidBySize(5));
		registration.addRecipeCatalyst(new ItemStack(ModItems.SIGIL_ENGRAVING_TABLE_7X7.get()), ShapedSigilEngravingRecipeCategory.getUidBySize(3), ShapedSigilEngravingRecipeCategory.getUidBySize(5), ShapedSigilEngravingRecipeCategory.getUidBySize(7), ShapedSigilTableCraftingRecipeCategory.getUidBySize(3), ShapedSigilTableCraftingRecipeCategory.getUidBySize(5), ShapedSigilTableCraftingRecipeCategory.getUidBySize(7));
	}

	private void putSigilcraftRecipe(SigilcraftRecipe recipe){
		RecipeSize recipeSize = getRecipeSize((BaseSigilcraftRecipe)recipe);
		if(recipe instanceof ShapedSigilEngravingRecipe)
			SHAPED_SIGIL_ENGRAVING_MAP.put(recipeSize, recipe);
		else if(recipe instanceof ShapedSigilTableCraftingRecipe)
			SHAPED_SIGIL_CRAFTING_MAP.put(recipeSize, recipe);
	}

	private RecipeSize getRecipeSize(BaseSigilcraftRecipe recipe){
		int center = recipe.getCenterIngredient();
		int height = recipe.getRecipeHeight();
		int width = recipe.getRecipeWidth();
		switch(Math.max(Math.max(center%width, width-1-center%width), Math.max(center/width, height-1-center/width))){
			case 1:
				return RecipeSize.SIZE_3X3;
			case 2:
				return RecipeSize.SIZE_5X5;
			case 3:
				return RecipeSize.SIZE_7X7;
			default:
				return null;
		}
	}

}
