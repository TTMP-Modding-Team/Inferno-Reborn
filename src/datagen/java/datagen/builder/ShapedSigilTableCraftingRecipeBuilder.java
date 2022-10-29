package datagen.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.contents.ModRecipes;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Consumer;

public class ShapedSigilTableCraftingRecipeBuilder{
	private final BaseShapedSigilcraftRecipeBuilder builder = new BaseShapedSigilcraftRecipeBuilder(){
		@Override protected IFinishedRecipe createResult(ResourceLocation id, ResourceLocation advancementId){
			return new Result(id, result, count, group!=null ? group : "", patterns, key, advancement, advancementId, Objects.requireNonNull(centerIngredient));
		}
	};
	private final Item result;
	private final int count;

	public ShapedSigilTableCraftingRecipeBuilder(IItemProvider itemProvider){
		this(itemProvider, 1);
	}
	public ShapedSigilTableCraftingRecipeBuilder(IItemProvider itemProvider, int count){
		this.result = itemProvider.asItem();
		this.count = count;
	}

	public ShapedSigilTableCraftingRecipeBuilder define(Character key, ITag<Item> tag){
		return define(key, Ingredient.of(tag));
	}
	public ShapedSigilTableCraftingRecipeBuilder define(Character key, IItemProvider item){
		return define(key, Ingredient.of(item));
	}
	public ShapedSigilTableCraftingRecipeBuilder define(Character key, Ingredient ingredient){
		builder.define(key, ingredient);
		return this;
	}

	public ShapedSigilTableCraftingRecipeBuilder defineAsCenter(Character key, ITag<Item> tag){
		builder.setCenterIngredient(key);
		return define(key, tag);
	}
	public ShapedSigilTableCraftingRecipeBuilder defineAsCenter(Character key, IItemProvider item){
		builder.setCenterIngredient(key);
		return define(key, item);
	}
	public ShapedSigilTableCraftingRecipeBuilder defineAsCenter(Character key, Ingredient ingredient){
		builder.setCenterIngredient(key);
		return define(key, ingredient);
	}

	public ShapedSigilTableCraftingRecipeBuilder pattern(String pattern){
		builder.pattern(pattern);
		return this;
	}

	public ShapedSigilTableCraftingRecipeBuilder unlockedBy(String criterion, ICriterionInstance instance){
		builder.unlockedBy(criterion, instance);
		return this;
	}

	public ShapedSigilTableCraftingRecipeBuilder group(String group){
		builder.setGroup(group);
		return this;
	}

	public void save(Consumer<IFinishedRecipe> consumer, ResourceLocation id){
		builder.save(consumer, id);
	}

	public static class Result implements IFinishedRecipe{
		private final ResourceLocation id;
		private final Item result;
		private final int count;
		private final String group;
		private final List<String> pattern;
		private final Map<Character, Ingredient> key;
		private final Advancement.Builder advancement;
		private final ResourceLocation advancementId;
		private final char center;

		public Result(ResourceLocation id,
		              Item result,
		              int count,
		              String group,
		              List<String> pattern,
		              Map<Character, Ingredient> key,
		              Advancement.Builder advancement,
		              ResourceLocation advancementId,
		              char center){
			this.id = id;
			this.result = result;
			this.count = count;
			this.group = group;
			this.pattern = pattern;
			this.key = key;
			this.advancement = advancement;
			this.advancementId = advancementId;
			this.center = center;
		}

		@Override public void serializeRecipeData(JsonObject o){
			if(!this.group.isEmpty()) o.addProperty("group", this.group);
			JsonArray arr = new JsonArray();
			for(String s : this.pattern) arr.add(s);
			o.add("pattern", arr);
			JsonObject jsonobject = new JsonObject();

			for(Entry<Character, Ingredient> e : this.key.entrySet())
				jsonobject.add(String.valueOf(e.getKey()), e.getValue().toJson());

			o.add("key", jsonobject);
			o.add("result", BuilderUtils.result(this.result, this.count));
			o.addProperty("center", center);
		}

		@Override public IRecipeSerializer<?> getType(){
			return ModRecipes.SHAPED_SIGIL_TABLE_CRAFTING.get();
		}
		@Override public ResourceLocation getId(){
			return this.id;
		}
		@Nullable @Override public JsonObject serializeAdvancement(){
			return this.advancement.serializeToJson();
		}
		@Nullable @Override public ResourceLocation getAdvancementId(){
			return this.advancementId;
		}
	}
}
