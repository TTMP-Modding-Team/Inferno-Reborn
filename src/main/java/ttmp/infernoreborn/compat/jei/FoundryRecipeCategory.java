package ttmp.infernoreborn.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import ttmp.infernoreborn.contents.ModBlocks;
import ttmp.infernoreborn.contents.recipe.foundry.FoundryRecipe;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public class FoundryRecipeCategory implements IRecipeCategory<FoundryRecipe>{
	private final IDrawable background;
	private final IDrawable icon;

	public static final ResourceLocation UID = new ResourceLocation(MODID, "foundry");

	public FoundryRecipeCategory(IGuiHelper guiHelper){
		background = guiHelper.createDrawable(new ResourceLocation(MODID, "textures/gui/foundry.png"), 0, 168, 176, 181);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.FOUNDRY.get()));
	}

	@Override
	public ResourceLocation getUid(){
		return UID;
	}

	@Override
	public Class<? extends FoundryRecipe> getRecipeClass(){
		return FoundryRecipe.class;
	}

	@Override
	@Deprecated
	public String getTitle(){
		return getTitleAsTextComponent().getString();
	}

	@Override
	public ITextComponent getTitleAsTextComponent(){
		return ModBlocks.FOUNDRY.get().getName();
	}

	@Override
	public IDrawable getBackground(){
		return background;
	}

	@Override
	public IDrawable getIcon(){
		return icon;
	}

	@Override
	public void setIngredients(FoundryRecipe recipe, IIngredients ingredients){
		ingredients.setInputIngredients(recipe.getIngredients());
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FoundryRecipe recipe, IIngredients ingredients){
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 0, 0);
		guiItemStacks.init(1, true, 49, 0);
		guiItemStacks.init(2, false, 107, 0);

		guiItemStacks.set(ingredients);
	}
}
