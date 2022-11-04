package ttmp.infernoreborn.contents.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import ttmp.infernoreborn.api.ability.Ability;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.ModRecipes;
import ttmp.infernoreborn.contents.item.ability.FixedAbilityItem;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CombineSparkRecipe implements ICraftingRecipe{
	private final ResourceLocation id;

	public CombineSparkRecipe(ResourceLocation id){
		this.id = id;
	}

	@Override public boolean matches(CraftingInventory inventory, World world){
		return !collectAbilities(inventory).isEmpty();
	}

	@Override public ItemStack assemble(CraftingInventory inventory){
		Set<Ability> abilities = collectAbilities(inventory);
		ItemStack stack = new ItemStack(ModItems.INFERNO_SPARK.get());
		FixedAbilityItem.setAbilities(stack, abilities.toArray(new Ability[0]));
		return stack;
	}

	private static Set<Ability> collectAbilities(CraftingInventory inventory){
		Set<Ability> abilities = new HashSet<>();
		int seen = 0;
		for(int i = 0; i<inventory.getContainerSize(); i++){
			ItemStack stack = inventory.getItem(i);
			if(stack.isEmpty()) continue;
			if(stack.getItem()==ModItems.INFERNO_SPARK.get()){
				for(Ability ability : FixedAbilityItem.getAbilities(stack)){
					if(!abilities.add(ability)) return Collections.emptySet();
				}
				seen++;
			}else return Collections.emptySet();
		}
		return seen>=2 ? abilities : Collections.emptySet();
	}

	@Override public boolean canCraftInDimensions(int x, int y){
		return x*y>=2;
	}

	@Override public ItemStack getResultItem(){
		return ItemStack.EMPTY;
	}

	@Override public ResourceLocation getId(){
		return id;
	}

	@Override public IRecipeSerializer<?> getSerializer(){
		return ModRecipes.SPARK_COMBINE.get();
	}

	@Override public boolean isSpecial(){
		return true;
	}
}
