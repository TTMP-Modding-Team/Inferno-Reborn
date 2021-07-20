package ttmp.infernoreborn.compat.jei.sigil;

import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import ttmp.infernoreborn.contents.recipe.sigilcraft.ShapedSigilEngravingRecipe;

import static ttmp.infernoreborn.InfernoReborn.MODID;

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

	@Override public void draw(ShapedSigilEngravingRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY){
		Minecraft.getInstance().textureManager.bind(TEXTURE);
		GuiUtils.drawTexturedModalRect(matrixStack, 1+getSize().size/2*18, 1+getSize().size/2*18, 256-16, 256-16, 16, 16, 0);
	}

	public static ResourceLocation getUidBySize(RecipeSize size){
		return new ResourceLocation(MODID, "sigil_engraving_"+size.size);
	}
}
