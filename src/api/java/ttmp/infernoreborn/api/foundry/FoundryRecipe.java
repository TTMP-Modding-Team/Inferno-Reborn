package ttmp.infernoreborn.api.foundry;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import ttmp.infernoreborn.api.QuantifiedIngredient;
import ttmp.infernoreborn.api.RecipeTypes;
import ttmp.infernoreborn.api.WackyRecipe;
import ttmp.infernoreborn.api.essence.EssenceIngredient;

import java.util.List;

public interface FoundryRecipe extends WackyRecipe<FoundryInventory, FoundryRecipe.Result>{
	int DEFAULT_PROCESSING_TIME = 1000;

	/**
	 * @return Processing time for preview purposes
	 */
	int processingTime();
	/**
	 * @return List of quantified ingredients for preview purposes
	 */
	List<QuantifiedIngredient> getQuantifiedIngredients();
	/**
	 * @return List of essence requirements for preview purposes
	 */
	EssenceIngredient getEssences();
	/**
	 * @return Byproduct item for preview purposes
	 */
	ItemStack getByproduct();

	@Override default IRecipeType<?> getType(){
		return RecipeTypes.foundryRecipeType();
	}

	/**
	 * Result of crucible recipes. ItemStacks inside this object are safe to use/modify.
	 */
	final class Result{
		private final int processingTime;
		private final ItemStack result;
		private final ItemStack byproduct;

		public Result(int processingTime, ItemStack result, ItemStack byproduct){
			this.processingTime = processingTime;
			this.result = result;
			this.byproduct = byproduct;
		}

		public int getProcessingTime(){
			return processingTime;
		}
		public ItemStack getResult(){
			return result;
		}
		public ItemStack getByproduct(){
			return byproduct;
		}

		@Override public String toString(){
			return "Result{"+
					"processingTime="+processingTime+
					", result="+result+
					", byproduct="+byproduct+
					'}';
		}
	}
}
