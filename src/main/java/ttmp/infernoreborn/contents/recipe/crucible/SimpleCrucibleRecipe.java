package ttmp.infernoreborn.contents.recipe.crucible;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import ttmp.infernoreborn.api.Simulation;
import ttmp.infernoreborn.api.crucible.CrucibleInventory;
import ttmp.infernoreborn.api.crucible.CrucibleRecipe;
import ttmp.infernoreborn.api.essence.EssenceIngredient;
import ttmp.infernoreborn.api.recipe.FluidIngredient;
import ttmp.infernoreborn.api.recipe.QuantifiedIngredient;
import ttmp.infernoreborn.api.recipe.RecipeHelper;
import ttmp.infernoreborn.contents.ModRecipes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleCrucibleRecipe implements CrucibleRecipe{
	private final ResourceLocation id;
	private final QuantifiedIngredient[] ingredients;
	private final FluidIngredient<?>[] fluidIngredients;
	private final EssenceIngredient essences;
	private final int stir;
	private final List<ItemStack> outputs;
	private final List<FluidStack> fluidOutputs;

	public SimpleCrucibleRecipe(
			ResourceLocation id,
			QuantifiedIngredient[] ingredients,
			FluidIngredient<?>[] fluidIngredients,
			EssenceIngredient essences,
			int stir,
			List<ItemStack> outputs,
			List<FluidStack> fluidOutputs){
		this.id = id;
		this.ingredients = ingredients;
		this.fluidIngredients = fluidIngredients;
		this.essences = essences;
		this.stir = stir;
		this.outputs = outputs;
		this.fluidOutputs = fluidOutputs;
	}

	@Override public int stir(CrucibleInventory inventory){
		return stir;
	}
	@Override public List<QuantifiedIngredient> getQuantifiedIngredients(){
		return Collections.unmodifiableList(Arrays.asList(ingredients));
	}
	@Override public List<FluidIngredient<?>> getFluidIngredients(){
		return Collections.unmodifiableList(Arrays.asList(fluidIngredients));
	}
	@Override public EssenceIngredient essences(){
		return essences;
	}
	@Override public int stir(){
		return stir;
	}
	@Override public List<ItemStack> outputs(){
		return Collections.unmodifiableList(outputs);
	}
	@Override public List<FluidStack> fluidOutputs(){
		return Collections.unmodifiableList(fluidOutputs);
	}

	@Override public Simulation<Result> consume(CrucibleInventory inv){
		return essences.getTotalEssenceConsumption()>inv.heat().maxEssence() ?
				Simulation.fail() :
				Simulation.combineWithoutResult(
						RecipeHelper.consume(inv, this.ingredients),
						RecipeHelper.consume(inv.fluidInput(), this.fluidIngredients),
						inv.essences().consume(essences)
				).ifThen(v -> new Result(
						outputs.stream().map(s -> s.copy()).collect(Collectors.toList()),
						fluidOutputs.stream().map(s -> s.copy()).collect(Collectors.toList())));
	}

	@Override public ResourceLocation getId(){
		return id;
	}
	@Override public IRecipeSerializer<?> getSerializer(){
		return ModRecipes.CRUCIBLE.get();
	}
}
