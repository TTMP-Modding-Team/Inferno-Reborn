package ttmp.infernoreborn.compat.jei.sigil;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.util.ResourceLocation;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public class ShapedSigilTableCraftingRecipeCategory extends SigilcraftRecipeCategoryBase{

	public ShapedSigilTableCraftingRecipeCategory(IGuiHelper guiHelper, RecipeSize size){
		super(guiHelper, size);
	}

	@Override
	public ResourceLocation getUid(){
		return new ResourceLocation(MODID, "sigil_tablecrafting_"+sizeInt);
	}

	public static ResourceLocation getUidBySize(int size){
		return new ResourceLocation(MODID, "sigil_tablecrafting_"+size);
	}

	@Override
	public String getTitle(){
		return "SigilTable Crafting Recipe "+sizeInt+"X"+sizeInt;
	}

	@Override
	public IDrawable getBackground(){
		return this.background;
	}

	@Override
	public IDrawable getIcon(){
		return this.icon;
	}

}
