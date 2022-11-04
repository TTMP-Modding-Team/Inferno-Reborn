package ttmp.infernoreborn.compat.jei.sigil;

import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import ttmp.infernoreborn.contents.recipe.sigilcraft.ShapedSigilTableCraftingRecipe;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

public class ShapedSigilTableCraftingRecipeCategory extends BaseSigilcraftRecipeCategory<ShapedSigilTableCraftingRecipe>{

	public ShapedSigilTableCraftingRecipeCategory(IGuiHelper guiHelper, RecipeSize size){
		super(guiHelper, size);
	}

	@Override protected ITextComponent createTitle(){
		return new TranslationTextComponent("recipe.infernoreborn.shaped_sigil_table_crafting.x"+getSize().size);
	}

	@Override
	public ResourceLocation getUid(){
		return getUidBySize(getSize());
	}

	@Override public Class<? extends ShapedSigilTableCraftingRecipe> getRecipeClass(){
		return ShapedSigilTableCraftingRecipe.class;
	}

	public static ResourceLocation getUidBySize(RecipeSize size){
		return new ResourceLocation(MODID, "sigil_table_crafting_"+size.size);
	}
}
