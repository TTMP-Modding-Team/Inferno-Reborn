package ttmp.infernoreborn.compat.jei.sigil;

import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.contents.ModBlocks;
import ttmp.infernoreborn.contents.recipe.sigilcraft.BaseSigilcraftRecipe;
import ttmp.infernoreborn.contents.recipe.sigilcraft.SigilcraftRecipe;

import java.util.List;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public abstract class SigilcraftRecipeCategoryBase implements IRecipeCategory<SigilcraftRecipe>{

	protected final int sizeInt;
	protected final IDrawable background;
	protected final IDrawable icon;

	public SigilcraftRecipeCategoryBase(IGuiHelper guiHelper, RecipeSize size){
		this.sizeInt = size.getIntSize();
		if(size==RecipeSize.SIZE_3X3){
			this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.SIGIL_ENGRAVING_TABLE_3X3.get()));
			this.background = guiHelper.createDrawable(getTexture(), 0, 60, 116, 54);
		}else if(size==RecipeSize.SIZE_5X5){
			this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.SIGIL_ENGRAVING_TABLE_5X5.get()));
			this.background = guiHelper.createDrawable(getTexture(), 0, 0, 150, 90);
		}else if(size==RecipeSize.SIZE_7X7){
			this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.SIGIL_ENGRAVING_TABLE_7X7.get()));
			this.background = guiHelper.createDrawable(getTexture(), 0, 0, 126, 159);
		}else{
			this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.SIGIL_ENGRAVING_TABLE_3X3.get()));
			this.background = guiHelper.createDrawable(getTexture(), 0, 0, 116, 54);
		}
	}

	@Override
	public ResourceLocation getUid(){
		return null;
	}

	public static ResourceLocation getUidBySize(int size){
		return null;
	}

	@Override
	public Class<? extends SigilcraftRecipe> getRecipeClass(){
		return SigilcraftRecipe.class;
	}

	@Override
	public String getTitle(){
		return null;
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
	public void draw(SigilcraftRecipe recipe, MatrixStack stack, double mouseX, double mouseY){
		stack.pushPose();
		stack.scale(0.5F, 0.5F, 0.5F);
		stack.popPose();
	}

	@Override
	public void setIngredients(SigilcraftRecipe recipe, IIngredients ingredients){
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
		ingredients.setInputIngredients(recipe.getIngredients());
	}

	@Override
	public void setRecipe(IRecipeLayout layout, SigilcraftRecipe recipe, IIngredients ingredients){
		IGuiItemStackGroup stacks = layout.getItemStacks();

		List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
		List<ItemStack> outputs = ingredients.getOutputs(VanillaTypes.ITEM).get(0);

		stacks.init(0, false, 94, 18);
		stacks.set(0, outputs);

		for(int i = 0; i<sizeInt; i++){
			for(int j = 0; j<sizeInt; j++){
				int index = 1+j+(i*sizeInt);
				stacks.init(index, true, j*18, i*18);
			}
		}
		BaseSigilcraftRecipe sigilRecipe = (BaseSigilcraftRecipe)recipe;
		int center = sigilRecipe.getCenterIngredient();
		int width = sigilRecipe.getRecipeWidth();
		int height = sigilRecipe.getRecipeHeight();
		int dwidth = 0;
		int dheight = 0;
		if(center!=4&&center!=12&&center!=24){
			dwidth = sizeInt/2-center%width;
			dheight = sizeInt/2-center/height;
		}
		int stackIndex = 0;
		for(int i = 0; i<height; i++){
			for(int j = 0; j<width; j++){
				int index = 1+j+dwidth+((i+dheight)*sizeInt);
				stacks.set(index, inputs.get(stackIndex));
				stackIndex++;
			}
		}
	}

	private ResourceLocation getTexture(){
		return new ResourceLocation(MODID, "textures/gui/jei/sigil_engraving_table_"+sizeInt+"x"+sizeInt+".png");
	}
}
