package ttmp.infernoreborn.api.crucible;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.fluids.FluidStack;
import ttmp.infernoreborn.api.QuantifiedIngredient;
import ttmp.infernoreborn.api.RecipeTypes;
import ttmp.infernoreborn.api.WackyRecipe;
import ttmp.infernoreborn.api.essence.EssenceIngredient;

import java.util.List;

public interface CrucibleRecipe extends WackyRecipe<CrucibleInventory, CrucibleRecipe.Result>{
	int DEFAULT_WATER_CONSUMPTION = 250;

	/**
	 * @return Water consumption, not preview
	 */
	int waterConsumption(CrucibleInventory inventory);
	/**
	 * @return Required stir ticks, not preview
	 */
	int stir(CrucibleInventory inventory);

	/**
	 * @return List of quantified ingredients for preview purposes
	 */
	List<QuantifiedIngredient> getQuantifiedIngredients();
	/**
	 * @return List of essence requirements for preview purposes
	 */
	EssenceIngredient essences();
	/**
	 * @return Water consumption for preview purposes
	 */
	int waterConsumption();
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
		return RecipeTypes.crucibleRecipeType();
	}

	@Deprecated @Override default ItemStack getResultItem(){
		return ItemStack.EMPTY;
	}

	/**
	 * Result of crucible recipes. Item/FluidStacks inside are safe to use/modify.
	 */
	final class Result{
		private final int waterRequirement;
		private final List<ItemStack> outputs;
		private final List<FluidStack> fluidOutputs;

		public Result(int waterRequirement, List<ItemStack> outputs, List<FluidStack> fluidOutputs){
			this.waterRequirement = waterRequirement;
			this.outputs = outputs;
			this.fluidOutputs = fluidOutputs;
		}

		public int waterRequirement(){
			return waterRequirement;
		}
		public List<ItemStack> outputs(){
			return outputs;
		}
		public List<FluidStack> fluidOutputs(){
			return fluidOutputs;
		}

		@Override public String toString(){
			return "Result{"+
					"waterRequirement="+waterRequirement+
					", outputs="+outputs+
					", fluidOutputs="+fluidOutputs+
					'}';
		}
	}
}
