package ttmp.infernoreborn.contents.recipe.crucible;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import ttmp.infernoreborn.api.essence.EssenceIngredient;
import ttmp.infernoreborn.api.recipe.FluidIngredient;
import ttmp.infernoreborn.api.recipe.QuantifiedIngredient;
import ttmp.infernoreborn.api.recipe.RecipeHelper;
import ttmp.infernoreborn.contents.tile.crucible.Crucible;

import java.util.ArrayList;
import java.util.List;

public final class SimpleCrucibleRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SimpleCrucibleRecipe>{
	@SuppressWarnings("ConstantConditions")
	@Override public SimpleCrucibleRecipe fromJson(ResourceLocation id, JsonObject json){
		QuantifiedIngredient[] ingredients = RecipeHelper.readQuantifiedIngredients(json, "ingredients");
		if(ingredients.length>Crucible.INPUT_INVENTORY_SIZE) throw new JsonParseException("Too many ingredients");
		FluidIngredient<?>[] fluidIngredients = json.has("fluidIngredients") ?
				RecipeHelper.readFluidIngredients(json, "fluidIngredients") : new FluidIngredient[0];
		if(fluidIngredients.length>Crucible.FLUID_TANK_SIZE) throw new JsonParseException("Too many fluid ingredients");
		JsonObject essencesJson = JSONUtils.getAsJsonObject(json, "essences", null);
		EssenceIngredient essences = essencesJson!=null ? EssenceIngredient.read(essencesJson) : EssenceIngredient.nothing();
		int stirTime = JSONUtils.getAsInt(json, "stir", 0);
		if(stirTime<0) throw new JsonParseException("Non-positive stir ticks");

		JsonElement result = json.get("result");
		if(result==null) throw new JsonParseException("No results");
		List<ItemStack> results = new ArrayList<>();
		List<FluidStack> fluidResults = new ArrayList<>();
		if(result.isJsonObject()) parse(result.getAsJsonObject(), results, fluidResults);
		else if(result.isJsonArray()){
			for(JsonElement e : result.getAsJsonArray())
				parse(e.getAsJsonObject(), results, fluidResults);
		}else throw new JsonParseException(
				"Invalid result, must be either object(single result item) or list(multiple result items)");
		return new SimpleCrucibleRecipe(id, ingredients, fluidIngredients, essences, stirTime, results, fluidResults);
	}

	@Override public SimpleCrucibleRecipe fromNetwork(ResourceLocation id, PacketBuffer buf){
		QuantifiedIngredient[] ingredients = new QuantifiedIngredient[buf.readUnsignedByte()];
		for(int i = 0; i<ingredients.length; i++) ingredients[i] = QuantifiedIngredient.read(buf);
		FluidIngredient<?>[] fluidIngredients = new FluidIngredient[buf.readUnsignedByte()];
		for(int i = 0; i<fluidIngredients.length; i++) fluidIngredients[i] = FluidIngredient.readFrom(buf);
		EssenceIngredient essences = EssenceIngredient.read(buf);
		int stir = buf.readInt();
		List<ItemStack> outputs = new ArrayList<>();
		for(int i = buf.readVarInt(); i>0; i--) outputs.add(buf.readItem());
		List<FluidStack> fluidOutputs = new ArrayList<>();
		for(int i = buf.readVarInt(); i>0; i--) fluidOutputs.add(buf.readFluidStack());
		return new SimpleCrucibleRecipe(id, ingredients, fluidIngredients, essences, stir, outputs, fluidOutputs);
	}

	@Override public void toNetwork(PacketBuffer buf, SimpleCrucibleRecipe recipe){
		buf.writeByte(recipe.getQuantifiedIngredients().size());
		for(QuantifiedIngredient ing : recipe.getQuantifiedIngredients()) ing.write(buf);
		recipe.essences().write(buf);
		buf.writeByte(recipe.getFluidIngredients().size());
		for(FluidIngredient<?> ing : recipe.getFluidIngredients()) ing.write(buf);
		recipe.essences().write(buf);
		buf.writeInt(recipe.stir());
		buf.writeVarInt(recipe.outputs().size());
		for(ItemStack output : recipe.outputs()) buf.writeItem(output);
		buf.writeVarInt(recipe.fluidOutputs().size());
		for(FluidStack fluid : recipe.fluidOutputs()) buf.writeFluidStack(fluid);
	}

	private static void parse(JsonObject o, List<ItemStack> results, List<FluidStack> fluidResults){
		if(o.has("fluid")) fluidResults.add(RecipeHelper.readFluid(o));
		else results.add(ShapedRecipe.itemFromJson(o));
	}
}
