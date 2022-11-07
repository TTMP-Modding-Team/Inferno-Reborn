package datagen.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import ttmp.infernoreborn.api.crucible.CrucibleRecipe;
import ttmp.infernoreborn.api.essence.EssenceIngredient;
import ttmp.infernoreborn.api.essence.EssenceType;
import ttmp.infernoreborn.api.recipe.FluidIngredient;
import ttmp.infernoreborn.api.recipe.FluidTagIngredient;
import ttmp.infernoreborn.api.recipe.QuantifiedIngredient;
import ttmp.infernoreborn.api.recipe.SimpleFluidIngredient;
import ttmp.infernoreborn.contents.ModRecipes;
import ttmp.infernoreborn.contents.tile.crucible.Crucible;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CrucibleRecipeBuilder{
	private final List<ItemStack> outputs = new ArrayList<>();
	private final List<FluidStack> fluidOutputs = new ArrayList<>();

	private final List<QuantifiedIngredient> ingredients = new ArrayList<>();
	private final List<FluidIngredient<?>> fluidIngredients = new ArrayList<>();
	private final EssenceIngredient.Builder essences = EssenceIngredient.builder();

	private int stir = 0;

	private final Advancement.Builder advancement = Advancement.Builder.advancement();

	public CrucibleRecipeBuilder output(IItemProvider itemProvider){
		return output(new ItemStack(itemProvider));
	}
	public CrucibleRecipeBuilder output(IItemProvider itemProvider, int count){
		return output(new ItemStack(itemProvider, count));
	}
	public CrucibleRecipeBuilder output(ItemStack result){
		this.outputs.add(result);
		return this;
	}
	public CrucibleRecipeBuilder output(Fluid fluid, int amount){
		return output(new FluidStack(fluid, amount));
	}
	public CrucibleRecipeBuilder output(FluidStack result){
		this.fluidOutputs.add(result);
		return this;
	}

	public CrucibleRecipeBuilder ingredient(Ingredient ingredient, int quantity){
		if(quantity<=0) throw new IllegalArgumentException("quantity");
		this.ingredients.add(new QuantifiedIngredient(ingredient, quantity));
		return this;
	}

	public CrucibleRecipeBuilder ingredient(Fluid fluid, int amount){
		return ingredient(new SimpleFluidIngredient(amount, fluid));
	}
	public CrucibleRecipeBuilder ingredient(ITag.INamedTag<Fluid> fluidTag, int amount){
		return ingredient(new FluidTagIngredient(fluidTag.getName(), fluidTag, amount));
	}
	public CrucibleRecipeBuilder water(){
		return water(CrucibleRecipe.DEFAULT_WATER_CONSUMPTION);
	}
	public CrucibleRecipeBuilder water(int amount){
		return ingredient(FluidTags.WATER, amount);
	}
	public CrucibleRecipeBuilder ingredient(FluidIngredient<?> ingredient){
		this.fluidIngredients.add(ingredient);
		return this;
	}

	public CrucibleRecipeBuilder essence(EssenceType type, int amount){
		if(amount<=0) throw new IllegalArgumentException("amount");
		essences.add(type, amount);
		return this;
	}

	public CrucibleRecipeBuilder anyEssence(int amount){
		if(amount<=0) throw new IllegalArgumentException("amount");
		essences.any(amount);
		return this;
	}

	public CrucibleRecipeBuilder stirTime(int stirTime){
		if(stirTime<0) throw new IllegalArgumentException("stirTime");
		this.stir = stirTime;
		return this;
	}

	public CrucibleRecipeBuilder unlockedBy(String name, ICriterionInstance criterion){
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
				if(!ingredients.isEmpty()){
					JsonArray arr = new JsonArray();
					for(QuantifiedIngredient ing : ingredients) arr.add(ing.serialize());
					object.add("ingredients", arr);
				}
				if(!fluidIngredients.isEmpty()){
					JsonArray arr = new JsonArray();
					for(FluidIngredient<?> ing : fluidIngredients) arr.add(ing.write());
					object.add("fluidIngredients", arr);
				}
				EssenceIngredient essences = CrucibleRecipeBuilder.this.essences.build();
				if(!essences.isEmpty()) object.add("essences", essences.toJsonObject());
				if(stir!=0) object.addProperty("stir", stir);
				JsonArray arr = new JsonArray();
				for(ItemStack output : outputs)
					arr.add(BuilderUtils.result(output));
				for(FluidStack fluid : fluidOutputs)
					arr.add(BuilderUtils.result(fluid));
				object.add("result", arr.size()==1 ? arr.get(0) : arr);
			}
			@Override public IRecipeSerializer<?> getType(){
				return ModRecipes.CRUCIBLE.get();
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
		if(ingredients.size()>Crucible.INPUT_INVENTORY_SIZE) throw new IllegalStateException("Too many ingredients");
		if(fluidIngredients.size()>Crucible.FLUID_TANK_SIZE)
			throw new IllegalStateException("Too many fluid ingredients");
		for(FluidIngredient<?> ing : fluidIngredients)
			if(ing.getAmount()>Crucible.FLUID_TANK_CAPACITY)
				throw new IllegalStateException("Too much fluid required: "+ing);
	}
}
