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

import static ttmp.infernoreborn.InfernoReborn.MODID;

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

		List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
		List<ItemStack> outputs = ingredients.getOutputs(VanillaTypes.ITEM).get(0);

		stacks.init(0, false, size.size*18+15, size.size/2*18);
		stacks.set(0, outputs);

		for(int y = 0; y<size.size; y++){
			for(int x = 0; x<size.size; x++){
				int index = 1+x+(y*size.size);
				stacks.init(index, true, x*18, y*18);
			}
		}
		int width = recipe.getRecipeWidth();
		int height = recipe.getRecipeHeight();
		int xOrigin = size.size/2-recipe.getCenterX();
		int yOrigin = size.size/2-recipe.getCenterY();

		int stackIndex = 0;
		for(int y = 0; y<height; y++){
			for(int x = 0; x<width; x++){
				int index = 1+x+xOrigin+(y+yOrigin)*size.size;
				stacks.set(index, inputs.get(stackIndex));
				stackIndex++;
			}
		}
	}
}
