package datagen.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.contents.ModRecipes;
import ttmp.infernoreborn.contents.recipe.foundry.FoundryRecipe;
import ttmp.infernoreborn.util.EssenceHolder;
import ttmp.infernoreborn.util.EssenceType;
import ttmp.infernoreborn.util.QuantifiedIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FoundryRecipeBuilder{
	private final ItemStack result;
	private final ItemStack byproduct;

	private final List<QuantifiedIngredient> ingredients = new ArrayList<>();
	private final EssenceHolder essences = new EssenceHolder();

	private int processingTime = FoundryRecipe.DEFAULT_PROCESSING_TIME;

	private final Advancement.Builder advancement = Advancement.Builder.advancement();

	public FoundryRecipeBuilder(ItemStack result){
		this(result, ItemStack.EMPTY);
	}
	public FoundryRecipeBuilder(ItemStack result, ItemStack byproduct){
		this.result = result;
		this.byproduct = byproduct;
	}

	public FoundryRecipeBuilder ingredient(Ingredient ingredient, int quantity){
		if(quantity<=0) throw new IllegalArgumentException("quantity");
		this.ingredients.add(new QuantifiedIngredient(ingredient, quantity));
		return this;
	}
	public FoundryRecipeBuilder essence(EssenceType type, int amount){
		if(amount<=0) throw new IllegalArgumentException("amount");
		essences.setEssence(type, amount);
		return this;
	}

	public FoundryRecipeBuilder processingTime(int processingTime){
		if(processingTime<=0) throw new IllegalArgumentException("processingTime");
		this.processingTime = processingTime;
		return this;
	}

	public FoundryRecipeBuilder unlockedBy(String name, ICriterionInstance criterion){
		this.advancement.addCriterion(name, criterion);
		return this;
	}

	public void save(Consumer<IFinishedRecipe> consumer, ResourceLocation id){
		this.ensureValid(id);
		this.advancement.parent(new ResourceLocation("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
				.rewards(AdvancementRewards.Builder.recipe(id))
				.requirements(IRequirementsStrategy.OR);
		consumer.accept(new IFinishedRecipe(){
			@Override public void serializeRecipeData(JsonObject object){
				JsonArray arr = new JsonArray();
				for(QuantifiedIngredient ing : ingredients) arr.add(ing.serialize());
				object.add("ingredients", arr);
				if(!essences.isEmpty()){
					JsonObject essencesJson = new JsonObject();
					for(EssenceType t : EssenceType.values()){
						int essence = essences.getEssence(t);
						if(essence>0) essencesJson.addProperty(t.id, essence);
					}
					object.add("essences", essencesJson);
				}
				if(processingTime!=FoundryRecipe.DEFAULT_PROCESSING_TIME) object.addProperty("processingTime", processingTime);
				object.add("result", BuilderUtils.result(result));
				if(!byproduct.isEmpty()) object.add("byproduct", BuilderUtils.result(byproduct));
			}
			@Override public IRecipeSerializer<?> getType(){
				return ModRecipes.FOUNDRY.get();
			}
			@Override public ResourceLocation getId(){
				return id;
			}
			@Override public JsonObject serializeAdvancement(){
				return advancement.serializeToJson();
			}
			@Override public ResourceLocation getAdvancementId(){
				return new ResourceLocation(id.getNamespace(), "recipes/special/"+id.getPath());
			}
		});
	}

	private void ensureValid(ResourceLocation id){
		// if(this.advancement.getCriteria().isEmpty()) throw new IllegalStateException("No way of obtaining recipe "+id);
		if(ingredients.isEmpty()) throw new IllegalStateException("No ingredients");
		if(ingredients.size()>2) throw new IllegalStateException("Too many ingredients");
	}
}
