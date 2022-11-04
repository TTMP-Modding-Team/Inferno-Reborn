package ttmp.infernoreborn.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import ttmp.infernoreborn.api.QuantifiedIngredient;
import ttmp.infernoreborn.api.essence.EssenceIngredient;
import ttmp.infernoreborn.api.essence.EssenceType;
import ttmp.infernoreborn.api.foundry.FoundryRecipe;
import ttmp.infernoreborn.contents.ModBlocks;
import ttmp.infernoreborn.contents.ModItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

public class FoundryRecipeCategory implements IRecipeCategory<FoundryRecipe>{
	private final IDrawable background;
	private final IDrawableAnimated progressBar;
	private final IDrawable holder;
	private final Map<EssenceType, IDrawable> essenceIcons = new EnumMap<>(EssenceType.class);

	private final IDrawable icon;

	public static final ResourceLocation UID = new ResourceLocation(MODID, "foundry");
	private static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/jei/foundry.png");

	public FoundryRecipeCategory(IGuiHelper guiHelper){
		this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 118, 46);
		this.progressBar = guiHelper.createAnimatedDrawable(
				guiHelper.createDrawable(TEXTURE, 226, 0, 30, 9), 200,
				IDrawableAnimated.StartDirection.LEFT, false);
		this.holder = guiHelper.createDrawableIngredient(new ItemStack(ModItems.ESSENCE_HOLDER.get()));

		for(EssenceType type : EssenceType.values())
			this.essenceIcons.put(type, guiHelper.createDrawableIngredient(new ItemStack(type.getEssenceItem())));

		this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.FOUNDRY.get()));
	}

	@Override public ResourceLocation getUid(){
		return UID;
	}
	@Override public Class<? extends FoundryRecipe> getRecipeClass(){
		return FoundryRecipe.class;
	}
	@Deprecated @Override public String getTitle(){
		return getTitleAsTextComponent().getString();
	}
	@Override public ITextComponent getTitleAsTextComponent(){
		return ModBlocks.FOUNDRY.get().getName();
	}
	@Override public IDrawable getBackground(){
		return background;
	}
	@Override public IDrawable getIcon(){
		return icon;
	}

	@Override public void setIngredients(FoundryRecipe recipe, IIngredients ingredients){
		List<List<ItemStack>> in = new ArrayList<>();
		for(QuantifiedIngredient qi : recipe.getQuantifiedIngredients())
			in.add(Arrays.stream(qi.getIngredient().getItems()).map(s -> {
				ItemStack copy = s.copy();
				copy.setCount(qi.getQuantity());
				return copy;
			}).collect(Collectors.toList()));
		if(in.size()==1) in.add(Collections.emptyList());

		ingredients.setInputLists(VanillaTypes.ITEM, in);

		List<ItemStack> out = new ArrayList<>();
		if(!recipe.getResultItem().isEmpty()) out.add(recipe.getResultItem());
		if(!recipe.getByproduct().isEmpty()) out.add(recipe.getByproduct());
		ingredients.setOutputs(VanillaTypes.ITEM, out);
	}

	@Override public void setRecipe(IRecipeLayout recipeLayout, FoundryRecipe recipe, IIngredients ingredients){
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 5, 5);
		guiItemStacks.init(1, true, 23, 5);

		guiItemStacks.init(2, false, 77, 5);
		guiItemStacks.init(3, false, 95, 5);

		guiItemStacks.set(ingredients);
	}

	@Override public void draw(FoundryRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY){
		progressBar.draw(matrixStack, 44, 9);

		EssenceIngredient essences = recipe.getEssences();
		boolean flag = false;
		for(EssenceType type : EssenceType.values()){
			if(essences.getEssenceConsumptionFor(type)<=0) continue;
			if(!flag){
				flag = true;
				holder.draw(matrixStack, 2, 27);
			}
			essenceIcons.get(type).draw(matrixStack, 15+type.ordinal()*8, type.ordinal()%2==0 ? 27 : 32);
		}
		// TODO indicator for 'any' consumption
	}

	@Override public List<ITextComponent> getTooltipStrings(FoundryRecipe recipe, double mouseX, double mouseY){
		EssenceIngredient essences = recipe.getEssences();
		if(mouseX<2||mouseY<27||mouseX>=2+16||mouseY>=27+16||essences.isEmpty()) return Collections.emptyList();
		List<ITextComponent> list = new ArrayList<>();
		for(EssenceType type : EssenceType.values()){
			int essence = essences.getEssenceConsumptionFor(type);
			if(essence>0)
				list.add(new TranslationTextComponent("item.infernoreborn.essence_holder.desc.essences."+type.id, essence));
		}
		return list;
		// TODO indicator for 'any' consumption
	}

	// TODO handle click???????
}
