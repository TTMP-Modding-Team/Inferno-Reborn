package ttmp.infernoreborn.contents.recipe;

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
import ttmp.infernoreborn.api.QuantifiedIngredient;
import ttmp.infernoreborn.api.Simulation;

public final class RecipeHelper{
	private RecipeHelper(){}

	public static Simulation<Void> consume(IInventory inventory, QuantifiedIngredient[] ingredients){
		int[] consumptions = new int[inventory.getContainerSize()];
		for(QuantifiedIngredient ing : ingredients){
			int needed = ing.getQuantity();
			for(int i = 0; i<inventory.getContainerSize()&&needed>0; i++){
				ItemStack stack = inventory.getItem(i);
				if(!stack.isEmpty()&&ing.getIngredient().test(stack)){
					int amountLeft = Math.min(needed, stack.getCount()-consumptions[i]);
					consumptions[i] += amountLeft;
					needed -= amountLeft;
				}
			}
			if(needed>0) return Simulation.fail();
		}
		return Simulation.success(() -> {
			for(int i = 0; i<consumptions.length; i++)
				inventory.removeItem(i, consumptions[i]);
			return null;
		});
	}

	public static QuantifiedIngredient[] readQuantifiedIngredients(JsonObject o, String propertyName){
		JsonArray ingredientsJson = JSONUtils.getAsJsonArray(o, propertyName);
		QuantifiedIngredient[] ingredients = new QuantifiedIngredient[ingredientsJson.size()];
		for(int i = 0; i<ingredientsJson.size(); i++){
			ingredients[i] = new QuantifiedIngredient(
					ingredientsJson.get(i).getAsJsonObject());
		}
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
}
