package ttmp.infernoreborn.contents.recipe.crucible;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import ttmp.infernoreborn.api.QuantifiedIngredient;
import ttmp.infernoreborn.api.Simulation;
import ttmp.infernoreborn.api.crucible.CrucibleInventory;
import ttmp.infernoreborn.api.crucible.CrucibleRecipe;
import ttmp.infernoreborn.api.essence.EssenceIngredient;
import ttmp.infernoreborn.contents.ModRecipes;
import ttmp.infernoreborn.contents.recipe.RecipeHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleCrucibleRecipe implements CrucibleRecipe{
	private final ResourceLocation id;
	private final QuantifiedIngredient[] ingredients;
	private final EssenceIngredient essences;
	private final int waterConsumption;
	private final int stir;
	private final List<ItemStack> outputs;
	private final List<FluidStack> fluidOutputs;

	public SimpleCrucibleRecipe(
			ResourceLocation id,
			QuantifiedIngredient[] ingredients,
			EssenceIngredient essences,
			int waterConsumption,
			int stir,
			List<ItemStack> outputs,
			List<FluidStack> fluidOutputs){
		this.id = id;
		this.ingredients = ingredients;
		this.essences = essences;
		this.waterConsumption = waterConsumption;
		this.stir = stir;
		this.outputs = outputs;
		this.fluidOutputs = fluidOutputs;
	}

	@Override public int waterConsumption(CrucibleInventory inventory){
		return waterConsumption;
	}
	@Override public int stir(CrucibleInventory inventory){
		return stir;
	}
	@Override public List<QuantifiedIngredient> getQuantifiedIngredients(){
		return Collections.unmodifiableList(Arrays.asList(ingredients));
	}
	@Override public EssenceIngredient essences(){
		return essences;
	}
	@Override public int waterConsumption(){
		return waterConsumption;
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
		return waterConsumption>inv.waterLevel()||
				essences.getTotalEssenceConsumption()>inv.heat().maxEssence() ?
				Simulation.fail() :
				Simulation.combineWithoutResult(
						RecipeHelper.consume(inv, this.ingredients),
						inv.essences().consume(essences)
				).ifThen(v -> new Result(waterConsumption,
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
