package ttmp.infernoreborn.datagen.builder;

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
import ttmp.infernoreborn.contents.sigil.Sigil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Consumer;

public class ShapedSigilEngravingRecipeBuilder{
	private final BaseShapedSigilcraftRecipeBuilder builder = new BaseShapedSigilcraftRecipeBuilder(){
		@Override protected IFinishedRecipe createResult(ResourceLocation id, ResourceLocation advancementId){
			return new Result(id, sigil, group!=null ? group : "", patterns, key, advancement, advancementId, Objects.requireNonNull(centerIngredient));
		}

		@Override protected boolean allow1x1Recipe(){
			return false;
		}
	};

	private final Sigil sigil;

	public ShapedSigilEngravingRecipeBuilder(Sigil sigil){
		this.sigil = sigil;
	}

	public ShapedSigilEngravingRecipeBuilder define(Character key, ITag<Item> tag){
		return define(key, Ingredient.of(tag));
	}
	public ShapedSigilEngravingRecipeBuilder define(Character key, IItemProvider item){
		return define(key, Ingredient.of(item));
	}
	public ShapedSigilEngravingRecipeBuilder define(Character key, Ingredient ingredient){
		builder.define(key, ingredient);
		return this;
	}

	public ShapedSigilEngravingRecipeBuilder defineAsCenter(Character key){
		builder.setCenterIngredient(key);
		return this;
	}

	public ShapedSigilEngravingRecipeBuilder pattern(String pattern){
		builder.pattern(pattern);
		return this;
	}

	public ShapedSigilEngravingRecipeBuilder unlockedBy(String criterion, ICriterionInstance instance){
		builder.unlockedBy(criterion, instance);
		return this;
	}

	public ShapedSigilEngravingRecipeBuilder group(String group){
		builder.setGroup(group);
		return this;
	}

	public void save(Consumer<IFinishedRecipe> consumer, ResourceLocation id){
		builder.save(consumer, id);
	}

	public static class Result implements IFinishedRecipe{
		private final ResourceLocation id;
		private final Sigil sigil;
		private final String group;
		private final List<String> pattern;
		private final Map<Character, Ingredient> key;
		private final Advancement.Builder advancement;
		private final ResourceLocation advancementId;
		private final char center;

		public Result(ResourceLocation id,
		              Sigil sigil,
		              String group,
		              List<String> pattern,
		              Map<Character, Ingredient> key,
		              Advancement.Builder advancement,
		              ResourceLocation advancementId,
		              char center){
			this.id = id;
			this.sigil = sigil;
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
			o.addProperty("sigil", Objects.requireNonNull(this.sigil.getRegistryName()).toString());
			o.addProperty("center", center);
		}
		@Override public ResourceLocation getId(){
			return id;
		}
		@Override public IRecipeSerializer<?> getType(){
			return ModRecipes.SHAPED_SIGIL_ENGRAVING.get();
		}
		@Nullable @Override public JsonObject serializeAdvancement(){
			return advancement.serializeToJson();
		}
		@Nullable @Override public ResourceLocation getAdvancementId(){
			return advancementId;
		}
	}
}
