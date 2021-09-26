package ttmp.infernoreborn.contents.recipe.sigilcraft;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.contents.ModRecipes;
import ttmp.infernoreborn.inventory.SigilcraftInventory;

import java.util.Map;

public class ShapedSigilTableCraftingRecipe extends BaseSigilcraftRecipe{
	private final ItemStack result;

	public ShapedSigilTableCraftingRecipe(ResourceLocation id,
	                                      String group,
	                                      int width,
	                                      int height,
	                                      NonNullList<Ingredient> ingredients,
	                                      ItemStack result,
	                                      int coreIngredient,
	                                      boolean mirror){
		super(id, group, width, height, ingredients, coreIngredient, mirror);
		this.result = result;
	}

	@Override public ItemStack assemble(SigilcraftInventory inv){
		return result.copy();
	}
	@Override public ItemStack getResultItem(){
		return result;
	}
	@Override public IRecipeSerializer<?> getSerializer(){
		return ModRecipes.SHAPED_SIGIL_TABLE_CRAFTING.get();
	}

	public static class Serializer extends BaseSigilcraftRecipeSerializer<ShapedSigilTableCraftingRecipe>{
		@Override public ShapedSigilTableCraftingRecipe fromJson(ResourceLocation id, JsonObject object){
			String group = JSONUtils.getAsString(object, "group", "");
			Map<String, Ingredient> map = keyFromJson(JSONUtils.getAsJsonObject(object, "key"));
			String[] patterns = shrink(patternFromJson(JSONUtils.getAsJsonArray(object, "pattern")));
			int width = patterns[0].length();
			int height = patterns.length;
			int center = readCenter(object, width, height);
			NonNullList<Ingredient> ingredients = dissolvePattern(patterns, map, width, height, center, false);
			ItemStack result = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(object, "result"));
			boolean mirror = JSONUtils.getAsBoolean(object, "mirror", false);

			return new ShapedSigilTableCraftingRecipe(id,
					group,
					width,
					height,
					ingredients,
					result,
					center,
					mirror);
		}

		@Override public ShapedSigilTableCraftingRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer){
			int x = buffer.readVarInt();
			int y = buffer.readVarInt();
			String group = buffer.readUtf(32767);
			NonNullList<Ingredient> ingredients = NonNullList.withSize(x*y, Ingredient.EMPTY);

			for(int i = 0; i<ingredients.size(); ++i)
				ingredients.set(i, Ingredient.fromNetwork(buffer));

			ItemStack result = buffer.readItem();
			return new ShapedSigilTableCraftingRecipe(id, group, x, y, ingredients, result, buffer.readVarInt(), buffer.readBoolean());
		}

		@Override public void toNetwork(PacketBuffer buffer, ShapedSigilTableCraftingRecipe recipe){
			buffer.writeVarInt(recipe.getRecipeWidth());
			buffer.writeVarInt(recipe.getRecipeHeight());
			buffer.writeUtf(recipe.getGroup());

			for(Ingredient ingredient : recipe.getIngredients())
				ingredient.toNetwork(buffer);

			buffer.writeItem(recipe.getResultItem());
			buffer.writeVarInt(recipe.getCenterIngredient());
			buffer.writeBoolean(recipe.isMirror());
		}
	}
}
