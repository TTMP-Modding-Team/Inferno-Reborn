package ttmp.infernoreborn.compat.jei.sigil;

import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.util.ResourceLocation;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public class ShapedSigilEngravingRecipeCategory extends SigilcraftRecipeCategoryBase{

	public ShapedSigilEngravingRecipeCategory(IGuiHelper guiHelper, RecipeSize size){
		super(guiHelper, size);
	}

	@Override
	public ResourceLocation getUid(){
		return new ResourceLocation(MODID, "sigil_engraving_"+sizeInt);
	}

	public static ResourceLocation getUidBySize(int size){
		return new ResourceLocation(MODID, "sigil_engraving_"+size);
	}

	@Override
	public String getTitle(){
		return "Sigil Engraving "+sizeInt+"X"+sizeInt;
	}
}
