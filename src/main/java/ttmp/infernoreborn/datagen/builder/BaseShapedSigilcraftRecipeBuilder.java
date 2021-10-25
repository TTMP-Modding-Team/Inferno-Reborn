package ttmp.infernoreborn.datagen.builder;

import com.google.common.collect.Sets;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public abstract class BaseShapedSigilcraftRecipeBuilder{
	@Nullable public String group;
	public final List<String> patterns = new ArrayList<>();
	public final Map<Character, Ingredient> key = new HashMap<>();
	@Nullable public Character centerIngredient;
	public final Advancement.Builder advancement = Advancement.Builder.advancement();

	public void setGroup(@Nullable String group){
		this.group = group;
	}
	public void pattern(String pattern){
		if(!patterns.isEmpty()&&patterns.get(0).length()!=pattern.length())
			throw new IllegalArgumentException("Fuck you");
		patterns.add(pattern);
	}
	public void define(Character character, Ingredient i){
		if(key.containsKey(character)) throw new IllegalArgumentException("Symbol '"+character+"' is already defined!");
		if(character==' ') throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
		key.put(character, i);
	}

	public void setCenterIngredient(@Nullable Character centerIngredient){
		this.centerIngredient = centerIngredient;
	}

	public void unlockedBy(String criterion, ICriterionInstance instance){
		this.advancement.addCriterion(criterion, instance);
	}

	public void save(Consumer<IFinishedRecipe> consumer, ResourceLocation id){
		this.ensureValid(id);
		this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(IRequirementsStrategy.OR);
		consumer.accept(createResult(id, new ResourceLocation(id.getNamespace(), "recipes/sigilcraft/"+id.getPath())));
	}

	protected abstract IFinishedRecipe createResult(ResourceLocation id, ResourceLocation advancementId);

	protected void ensureValid(ResourceLocation id){
		if(this.patterns.isEmpty()) throw new IllegalStateException("No pattern is defined for shaped recipe "+id+"!");

		Set<Character> leftover = Sets.newHashSet(this.key.keySet());
		leftover.remove(' ');
		leftover.remove(centerIngredient);

		for(String s : this.patterns){
			for(int i = 0; i<s.length(); ++i){
				char c = s.charAt(i);
				if(!this.key.containsKey(c)&&c!=' '&&(centerIngredient==null||c!=centerIngredient))
					throw new IllegalStateException("Pattern in recipe "+id+" uses undefined symbol '"+c+"'");
				leftover.remove(c);
			}
		}

		if(!leftover.isEmpty()) throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe "+id);
		//if(this.advancement.getCriteria().isEmpty()) throw new IllegalStateException("No way of obtaining recipe "+id);

		if(!allow1x1Recipe()&&this.patterns.size()==1&&this.patterns.get(0).length()==1)
			throw new IllegalStateException("1x1 recipe is not allowed for recipe type "+id+" is using");
	}

	protected boolean allow1x1Recipe(){
		return true;
	}
}
