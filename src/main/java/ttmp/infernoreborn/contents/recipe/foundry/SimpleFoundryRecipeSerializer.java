package ttmp.infernoreborn.contents.recipe.foundry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import ttmp.infernoreborn.contents.recipe.EssenceIngredient;
import ttmp.infernoreborn.contents.recipe.RecipeHelper;
import ttmp.infernoreborn.util.QuantifiedIngredient;

public class SimpleFoundryRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SimpleFoundryRecipe>{
	@SuppressWarnings("ConstantConditions")
	@Override public SimpleFoundryRecipe fromJson(ResourceLocation id, JsonObject o){
		QuantifiedIngredient[] ingredients = RecipeHelper.readQuantifiedIngredients(o, "ingredients");
		if(ingredients.length==0) throw new JsonParseException("No ingredients");
		if(ingredients.length>2) throw new JsonParseException("Too many ingredients");
		JsonObject essencesJson = JSONUtils.getAsJsonObject(o, "essences", null);
		EssenceIngredient essences = essencesJson!=null ? EssenceIngredient.read(essencesJson) : EssenceIngredient.nothing();
		int processingTime = JSONUtils.getAsInt(o, "processingTime", FoundryRecipe.DEFAULT_PROCESSING_TIME);
		ItemStack result = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(o, "result"));
		JsonObject byproductJson = JSONUtils.getAsJsonObject(o, "byproduct", null);
		ItemStack byproduct = byproductJson!=null ? ShapedRecipe.itemFromJson(byproductJson) : ItemStack.EMPTY;
		return new SimpleFoundryRecipe(id, ingredients, essences, processingTime, result, byproduct);
	}

	@Override public SimpleFoundryRecipe fromNetwork(ResourceLocation id, PacketBuffer buf){
		QuantifiedIngredient[] ingredients = new QuantifiedIngredient[buf.readUnsignedByte()];
		for(int i = 0; i<ingredients.length; i++) ingredients[i] = QuantifiedIngredient.read(buf);
		EssenceIngredient essences = EssenceIngredient.read(buf);
		int processingTime = buf.readVarInt();
		ItemStack result = buf.readItem();
		ItemStack byproduct = buf.readItem();
		return new SimpleFoundryRecipe(id, ingredients, essences, processingTime, result, byproduct);
	}

	@Override public void toNetwork(PacketBuffer buf, SimpleFoundryRecipe recipe){
		buf.writeByte(recipe.getQuantifiedIngredients().size());
		for(QuantifiedIngredient ing : recipe.getQuantifiedIngredients()) ing.write(buf);
		recipe.getEssences().write(buf);
		buf.writeVarInt(recipe.getProcessingTime());
		buf.writeItem(recipe.getResultItem());
		buf.writeItem(recipe.getByproduct());
	}
}
