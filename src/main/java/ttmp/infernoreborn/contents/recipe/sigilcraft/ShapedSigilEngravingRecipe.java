package ttmp.infernoreborn.contents.recipe.sigilcraft;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.api.sigil.Sigil;
import ttmp.infernoreborn.api.sigil.SigilEngravingRecipe;
import ttmp.infernoreborn.api.sigil.SigilHolder;
import ttmp.infernoreborn.api.sigil.SigilcraftInventory;
import ttmp.infernoreborn.contents.ModRecipes;
import ttmp.infernoreborn.contents.Sigils;
import ttmp.infernoreborn.contents.item.SigilItem;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

public class ShapedSigilEngravingRecipe extends BaseSigilcraftRecipe implements SigilEngravingRecipe{
	private final Sigil sigil;

	public ShapedSigilEngravingRecipe(ResourceLocation id,
	                                  String group,
	                                  int width,
	                                  int height,
	                                  NonNullList<Ingredient> ingredients,
	                                  Sigil sigil,
	                                  int coreIngredient,
	                                  boolean mirror){
		super(id, group, width, height, ingredients, coreIngredient, mirror);
		this.sigil = sigil;
	}

	@Override protected boolean matchCore(SigilcraftInventory inv){
		ItemStack engraving = inv.getCenterItem();
		SigilHolder h = SigilHolder.of(engraving);
		return h!=null&&h.canAdd(sigil);
	}

	@Override public ItemStack assemble(SigilcraftInventory inv){
		ItemStack engraving = inv.getCenterItem().copy();
		SigilHolder h = SigilHolder.of(engraving);
		if(h!=null) h.add(sigil);
		return engraving;
	}
	@Nullable @Override public Sigil tryEngrave(SigilHolder sigilHolder, SigilcraftInventory inv){
		return sigilHolder.canAdd(sigil)&&(matches(inv, false)||isMirror()&&matches(inv, true)) ? sigil : null;
	}

	@Override public ItemStack getResultItem(){
		return SigilItem.createStack(sigil);
	}

	@Override public IRecipeSerializer<?> getSerializer(){
		return ModRecipes.SHAPED_SIGIL_ENGRAVING.get();
	}

	public static class Serializer extends BaseSigilcraftRecipeSerializer<ShapedSigilEngravingRecipe>{
		@Override public ShapedSigilEngravingRecipe fromJson(ResourceLocation id, JsonObject object){
			String group = JSONUtils.getAsString(object, "group", "");
			Map<String, Ingredient> map = keyFromJson(JSONUtils.getAsJsonObject(object, "key"));
			String[] patterns = shrink(patternFromJson(JSONUtils.getAsJsonArray(object, "pattern")));
			int width = patterns[0].length();
			int height = patterns.length;
			int center = readCenter(object, width, height);
			NonNullList<Ingredient> ingredients = dissolvePattern(patterns, map, width, height, center, true);
			Sigil sigil = Sigils.getRegistry().getValue(new ResourceLocation(JSONUtils.getAsString(object, "sigil")));
			boolean mirror = JSONUtils.getAsBoolean(object, "mirror", false);

			return new ShapedSigilEngravingRecipe(id,
					group,
					width,
					height,
					ingredients,
					Objects.requireNonNull(sigil),
					center,
					mirror);
		}

		@Override public ShapedSigilEngravingRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer){
			int x = buffer.readVarInt();
			int y = buffer.readVarInt();
			String group = buffer.readUtf(32767);
			NonNullList<Ingredient> ingredients = NonNullList.withSize(x*y, Ingredient.EMPTY);

			for(int i = 0; i<ingredients.size(); ++i)
				ingredients.set(i, Ingredient.fromNetwork(buffer));

			Sigil sigil = Sigils.getRegistry().getValue(buffer.readVarInt());
			return new ShapedSigilEngravingRecipe(id, group, x, y, ingredients, sigil, buffer.readVarInt(), buffer.readBoolean());
		}

		@Override public void toNetwork(PacketBuffer buffer, ShapedSigilEngravingRecipe recipe){
			buffer.writeVarInt(recipe.getRecipeWidth());
			buffer.writeVarInt(recipe.getRecipeHeight());
			buffer.writeUtf(recipe.getGroup());

			for(Ingredient ingredient : recipe.getIngredients())
				ingredient.toNetwork(buffer);

			buffer.writeVarInt(Sigils.getRegistry().getID(recipe.sigil));
			buffer.writeVarInt(recipe.getCenterIngredient());
			buffer.writeBoolean(recipe.isMirror());
		}
	}
}
