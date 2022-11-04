package ttmp.infernoreborn.contents.recipe.foundry;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.api.QuantifiedIngredient;
import ttmp.infernoreborn.api.Simulation;
import ttmp.infernoreborn.api.essence.EssenceIngredient;
import ttmp.infernoreborn.api.foundry.FoundryInventory;
import ttmp.infernoreborn.api.foundry.FoundryRecipe;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.ModRecipes;
import ttmp.infernoreborn.contents.recipe.RecipeHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SimpleFoundryRecipe implements FoundryRecipe{
	private final ResourceLocation id;

	private final QuantifiedIngredient[] ingredients;
	private final EssenceIngredient essences;

	private final int processingTime;

	private final ItemStack result;
	private final ItemStack byproduct;

	public SimpleFoundryRecipe(
			ResourceLocation id,
			QuantifiedIngredient[] ingredients,
			EssenceIngredient essences,
			int processingTime,
			ItemStack result,
			ItemStack byproduct){
		this.id = id;
		this.ingredients = ingredients;
		this.essences = essences;
		this.processingTime = processingTime;
		this.result = result;
		this.byproduct = byproduct;
	}

	public int getProcessingTime(){
		return processingTime;
	}

	@Override public Simulation<Result> consume(FoundryInventory inv){
		return Simulation.combineWithoutResult(
				RecipeHelper.consume(inv, this.ingredients),
				essences.isEmpty() ? Simulation.success() :
						inv.getEssenceHandler()==null ? Simulation.fail() :
								inv.getEssenceHandler().consume(essences)
		).ifThen(v -> new Result(processingTime, result.copy(), byproduct.copy()));
	}

	@Override public ItemStack getResultItem(){
		return result;
	}
	@Override public ItemStack getByproduct(){
		return byproduct;
	}
	@Override public int processingTime(){
		return processingTime;
	}
	@Override public List<QuantifiedIngredient> getQuantifiedIngredients(){
		return Collections.unmodifiableList(Arrays.asList(ingredients));
	}
	@Override public EssenceIngredient getEssences(){
		return essences;
	}

	@Override public ResourceLocation getId(){
		return id;
	}
	@Override public IRecipeSerializer<?> getSerializer(){
		return ModRecipes.FOUNDRY.get();
	}

	@Deprecated @Override public NonNullList<ItemStack> getRemainingItems(FoundryInventory inv){
		return NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
	}
	@Deprecated @Override public NonNullList<Ingredient> getIngredients(){
		return NonNullList.create();
	}
	@Override public boolean isSpecial(){
		return false;
	}
	@Override public ItemStack getToastSymbol(){
		return new ItemStack(ModItems.FOUNDRY.get());
	}
}
