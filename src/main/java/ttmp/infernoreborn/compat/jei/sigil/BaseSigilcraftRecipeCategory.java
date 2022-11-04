package ttmp.infernoreborn.compat.jei.sigil;

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
import ttmp.infernoreborn.contents.recipe.sigilcraft.BaseSigilcraftRecipe;

import java.util.List;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

public abstract class BaseSigilcraftRecipeCategory<T extends BaseSigilcraftRecipe> implements IRecipeCategory<T>{
	public static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/jei/sigil_engraving_table.png");

	private final RecipeSize size;
	private final IDrawable background;
	private final IDrawable icon;

	private final ITextComponent title;

	public BaseSigilcraftRecipeCategory(IGuiHelper guiHelper, RecipeSize size){
		this.size = size;
		this.icon = guiHelper.createDrawableIngredient(size.icon());
		this.background = guiHelper.createDrawable(TEXTURE, size.backgroundU, size.backgroundV, size.backgroundWidth, size.backgroundHeight);

		this.title = createTitle();
	}

	protected abstract ITextComponent createTitle();

	public RecipeSize getSize(){
		return size;
	}

	@Override
	public IDrawable getBackground(){
		return this.background;
	}

	@Override
	public IDrawable getIcon(){
		return this.icon;
	}

	@Override
	@Deprecated
	public String getTitle(){
		return this.title.getString();
	}
	@Override
	public ITextComponent getTitleAsTextComponent(){
		return this.title;
	}

	@Override
	public void setIngredients(T recipe, IIngredients ingredients){
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
		ingredients.setInputIngredients(recipe.getIngredients());
	}

	@Override
	public void setRecipe(IRecipeLayout layout, T recipe, IIngredients ingredients){
		IGuiItemStackGroup stacks = layout.getItemStacks();

		stacks.init(0, false, size.size*18+15, size.size/2*18);
		stacks.set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));

		for(int y = 0; y<size.size; y++){
			for(int x = 0; x<size.size; x++){
				stacks.init(size.toIndex(x, y)+1, true, x*18, y*18);
			}
		}

		List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
		int xOrigin = size.size/2-recipe.getCenterX();
		int yOrigin = size.size/2-recipe.getCenterY();

		int stackIndex = 0;
		for(int y = 0; y<recipe.getRecipeHeight(); y++){
			for(int x = 0; x<recipe.getRecipeWidth(); x++, stackIndex++){
				stacks.set(size.toIndex(x+xOrigin, y+yOrigin)+1, inputs.get(stackIndex));
			}
		}
	}
}
