package ttmp.infernoreborn.api.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.api.Simulation;

public final class RecipeHelper{
	private RecipeHelper(){}

	public static Simulation<Void> consume(IInventory inventory, QuantifiedIngredient[] ingredients){
		int[] consumptions = new int[inventory.getContainerSize()];
		for(QuantifiedIngredient ing : ingredients){
			int needed = ing.getQuantity();
			boolean seen = false;
			for(int i = 0; i<inventory.getContainerSize()&&needed>0; i++){
				ItemStack stack = inventory.getItem(i);
				if(!stack.isEmpty()&&ing.getIngredient().test(stack)){
					int amountLeft = Math.min(needed, stack.getCount()-consumptions[i]);
					consumptions[i] += amountLeft;
					needed -= amountLeft;
					seen = true;
					if(needed<=0) break;
				}
			}
			if(!seen||needed>0) return Simulation.fail();
		}
		return Simulation.success(() -> {
			for(int i = 0; i<consumptions.length; i++)
				inventory.removeItem(i, consumptions[i]);
			return null;
		});
	}

	public static Simulation<Void> consume(FluidTankAccessor tank, FluidIngredient<?>[] ingredients){
		int[] consumptions = new int[tank.tanks()];

		for(FluidIngredient<?> ing : ingredients){
			int needed = ing.getAmount();
			boolean seen = false;
			for(int i = 0; i<tank.tanks(); i++){
				FluidStack stack = tank.fluidAt(i);
				if(!stack.isEmpty()&&ing.test(stack)){
					int amountLeft = Math.min(needed, stack.getAmount()-consumptions[i]);
					consumptions[i] += amountLeft;
					needed -= amountLeft;
					seen = true;
					if(needed<=0) break;
				}
			}
			if(!seen||needed>0) return Simulation.fail();
		}
		return Simulation.success(() -> {
			for(int i = 0; i<consumptions.length; i++)
				tank.drain(i, consumptions[i]);
			return null;
		});
	}

	public static QuantifiedIngredient[] readQuantifiedIngredients(JsonObject o, String propertyName){
		JsonArray ingredientsJson = JSONUtils.getAsJsonArray(o, propertyName);
		QuantifiedIngredient[] ingredients = new QuantifiedIngredient[ingredientsJson.size()];
		for(int i = 0; i<ingredientsJson.size(); i++)
			ingredients[i] = new QuantifiedIngredient(ingredientsJson.get(i).getAsJsonObject());
		return ingredients;
	}

	public static FluidIngredient<?>[] readFluidIngredients(JsonObject o, String propertyName){
		JsonArray ingredientsJson = JSONUtils.getAsJsonArray(o, propertyName);
		FluidIngredient<?>[] ingredients = new FluidIngredient[ingredientsJson.size()];
		for(int i = 0; i<ingredientsJson.size(); i++)
			ingredients[i] = FluidIngredient.readFrom(ingredientsJson.get(i).getAsJsonObject());
		return ingredients;
	}

	public static FluidStack readFluid(JsonObject o){
		String fluidString = JSONUtils.getAsString(o, "fluid");
		ResourceLocation fluidId = ResourceLocation.tryParse(fluidString);
		if(fluidId==null) throw new JsonParseException("Invalid fluid ID '"+fluidString+"'");
		Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidId);
		if(fluid==null) throw new JsonParseException("Unknown fluid '"+fluidId+"'");
		if(!o.has("amount")) throw new JsonParseException("Amount of the fluid should be specified");
		int amount = JSONUtils.getAsInt(o, "amount");
		if(amount<=0) throw new JsonParseException("Non-positive fluid amount");
		return new FluidStack(fluid, amount);
	}

	public interface FluidTankAccessor{
		int tanks();
		FluidStack fluidAt(int tank);
		void drain(int tank, int amount);
	}
}
