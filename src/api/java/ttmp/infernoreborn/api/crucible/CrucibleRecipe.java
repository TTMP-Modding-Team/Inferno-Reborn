package ttmp.infernoreborn.api.crucible;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.fluids.FluidStack;
import ttmp.infernoreborn.api.recipe.FluidIngredient;
import ttmp.infernoreborn.api.recipe.QuantifiedIngredient;
import ttmp.infernoreborn.api.recipe.RecipeTypes;
import ttmp.infernoreborn.api.recipe.WackyRecipe;
import ttmp.infernoreborn.api.essence.EssenceIngredient;

import java.util.List;

public interface CrucibleRecipe extends WackyRecipe<CrucibleInventory, CrucibleRecipe.Result>{
	int DEFAULT_WATER_CONSUMPTION = 250;

	/**
	 * @return Required stir ticks, not preview
	 */
	int stir(CrucibleInventory inventory);

	/**
	 * @return List of quantified ingredients for preview purposes
	 */
	List<QuantifiedIngredient> getQuantifiedIngredients();
	/**
	 * @return List of fluid ingredients for preview purposes
	 */
	List<FluidIngredient<?>> getFluidIngredients();
	/**
	 * @return List of essence requirements for preview purposes
	 */
	EssenceIngredient essences();
	/**
	 * @return Required stir ticks for preview purposes
	 */
	int stir();
	/**
	 * @return List of outputs for preview purposes
	 */
	List<ItemStack> outputs();
	/**
	 * @return List of fluid outputs for preview purposes
	 */
	List<FluidStack> fluidOutputs();

	@Override default IRecipeType<?> getType(){
		return RecipeTypes.crucible();
	}

	@Deprecated @Override default ItemStack getResultItem(){
		return ItemStack.EMPTY;
	}

	/**
	 * Result of crucible recipes. Item/FluidStacks inside are safe to use/modify.
	 */
	final class Result{
		private final List<ItemStack> outputs;
		private final List<FluidStack> fluidOutputs;

		public Result(List<ItemStack> outputs, List<FluidStack> fluidOutputs){
			this.outputs = outputs;
			this.fluidOutputs = fluidOutputs;
		}

		public List<ItemStack> outputs(){
			return outputs;
		}
		public List<FluidStack> fluidOutputs(){
			return fluidOutputs;
		}

		@Override public String toString(){
			return "Result{"+
					"outputs="+outputs+
					", fluidOutputs="+fluidOutputs+
					'}';
		}
	}
}
