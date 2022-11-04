package ttmp.infernoreborn.compat.jei.sigil;

import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import ttmp.infernoreborn.contents.recipe.sigilcraft.ShapedSigilEngravingRecipe;

import java.util.List;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

public class ShapedSigilEngravingRecipeCategory extends BaseSigilcraftRecipeCategory<ShapedSigilEngravingRecipe>{
	public ShapedSigilEngravingRecipeCategory(IGuiHelper guiHelper, RecipeSize size){
		super(guiHelper, size);
	}

	@Override protected ITextComponent createTitle(){
		return new TranslationTextComponent("recipe.infernoreborn.shaped_sigil_engraving.x"+getSize().size);
	}

	@Override public ResourceLocation getUid(){
		return getUidBySize(getSize());
	}

	@Override public Class<? extends ShapedSigilEngravingRecipe> getRecipeClass(){
		return ShapedSigilEngravingRecipe.class;
	}

	@Override public void setRecipe(IRecipeLayout layout, ShapedSigilEngravingRecipe recipe, IIngredients ingredients){
		IGuiItemStackGroup stacks = layout.getItemStacks();

		int size = getSize().size;
		int centerIndex = getSize().centerIndex();

		stacks.init(0, false, size*18+15, size/2*18);
		stacks.set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));

		for(int y = 0; y<size; y++){
			for(int x = 0; x<size; x++){
				int index = getSize().toIndex(x, y);
				if(centerIndex==index) continue;
				stacks.init(index+1, true, x*18, y*18);
			}
		}

		List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
		int xOrigin = size/2-recipe.getCenterX();
		int yOrigin = size/2-recipe.getCenterY();

		int stackIndex = 0;
		for(int y = 0; y<recipe.getRecipeHeight(); y++){
			for(int x = 0; x<recipe.getRecipeWidth(); x++, stackIndex++){
				int index = getSize().toIndex(x+xOrigin, y+yOrigin);
				if(centerIndex==index) continue;
				stacks.set(index+1, inputs.get(stackIndex));
			}
		}
	}

	@Override public void draw(ShapedSigilEngravingRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY){
		Minecraft.getInstance().textureManager.bind(TEXTURE);
		GuiUtils.drawTexturedModalRect(matrixStack, 1+getSize().size/2*18, 1+getSize().size/2*18, 256-16, 256-16, 16, 16, 0);
	}

	public static ResourceLocation getUidBySize(RecipeSize size){
		return new ResourceLocation(MODID, "sigil_engraving_"+size.size);
	}
}
