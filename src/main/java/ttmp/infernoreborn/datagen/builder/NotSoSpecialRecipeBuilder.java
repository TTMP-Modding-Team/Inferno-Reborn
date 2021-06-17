package ttmp.infernoreborn.datagen.builder;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

/**
 * For recipes with only advancements needed
 */
public class NotSoSpecialRecipeBuilder{
	private final Advancement.Builder advancement = Advancement.Builder.advancement();
	private final IRecipeSerializer<?> serializer;

	public NotSoSpecialRecipeBuilder(IRecipeSerializer<?> serializer){
		this.serializer = serializer;
	}

	public NotSoSpecialRecipeBuilder unlockedBy(String name, ICriterionInstance criterion){
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
			@Override public void serializeRecipeData(JsonObject object){}
			@Override public IRecipeSerializer<?> getType(){
				return serializer;
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
		if(this.advancement.getCriteria().isEmpty()){
			throw new IllegalStateException("No way of obtaining recipe "+id);
		}
	}
}
