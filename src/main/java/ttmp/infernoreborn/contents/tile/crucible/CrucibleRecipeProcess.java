package ttmp.infernoreborn.contents.tile.crucible;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import ttmp.infernoreborn.contents.recipe.crucible.CrucibleRecipe;
import ttmp.infernoreborn.inventory.CrucibleInventory;
import ttmp.infernoreborn.util.Simulation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class CrucibleRecipeProcess{
	@Nullable private final CrucibleRecipe recipe;
	@Nullable private final ResourceLocation recipeId;
	@Nullable private final Simulation<CrucibleRecipe.Result> simulation;
	private final int waterRequirement;
	private final int maxStir;
	private int currentStir;

	public CrucibleRecipeProcess(@Nonnull CrucibleRecipe recipe, Simulation<CrucibleRecipe.Result> simulation, CrucibleInventory inventory){
		if(!simulation.isSuccess()) throw new IllegalArgumentException("Trying to make process for mismatching recipe");
		this.recipe = recipe;
		this.recipeId = recipe.getId();
		this.simulation = simulation;
		this.waterRequirement = recipe.waterConsumption(inventory);
		this.maxStir = recipe.stir(inventory);
	}
	public CrucibleRecipeProcess(CompoundNBT tag){
		this.recipe = null;
		this.recipeId = tag.contains("Recipe", Constants.NBT.TAG_STRING) ?
				ResourceLocation.tryParse(tag.getString("Recipe")) : null;
		this.simulation = null;
		this.waterRequirement = tag.getInt("WaterRequirement");
		this.maxStir = tag.getInt("Stir");
		this.currentStir = tag.getInt("CurrentStir");
	}

	@Nullable public CrucibleRecipe getRecipe(){
		return recipe;
	}
	@Nullable public ResourceLocation getRecipeId(){
		return recipeId;
	}
	@Nullable public Simulation<CrucibleRecipe.Result> getSimulation(){
		return simulation;
	}
	public int getWaterRequirement(){
		return waterRequirement;
	}
	public int getCurrentStir(){
		return currentStir;
	}
	public void setCurrentStir(int currentStir){
		this.currentStir = currentStir;
	}

	public void incrementStir(){
		if(currentStir<maxStir) currentStir++;
	}
	public boolean isWorkComplete(){
		return currentStir>=maxStir;
	}

	public CompoundNBT write(){
		CompoundNBT tag = new CompoundNBT();
		if(recipeId!=null) tag.putString("Recipe", recipeId.toString());
		tag.putInt("WaterRequirement", waterRequirement);
		tag.putInt("Stir", maxStir);
		tag.putInt("CurrentStir", currentStir);
		return tag;
	}
}
