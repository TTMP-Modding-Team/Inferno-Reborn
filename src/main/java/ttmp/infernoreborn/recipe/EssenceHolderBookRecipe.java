package ttmp.infernoreborn.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import ttmp.infernoreborn.capability.EssenceHolder;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.ModRecipes;
import ttmp.infernoreborn.item.TheBookItem;
import ttmp.infernoreborn.util.EssenceType;

public class EssenceHolderBookRecipe implements ICraftingRecipe{
	private final ResourceLocation id;

	public EssenceHolderBookRecipe(ResourceLocation id){
		this.id = id;
	}

	@Override public boolean matches(CraftingInventory inventory, World world){
		boolean bookSeen = false, essenceHolderSeen = false;
		for(int i = 0; i<inventory.getContainerSize(); i++){
			ItemStack stack = inventory.getItem(i);
			if(stack.isEmpty()) continue;
			if(stack.getItem()==ModItems.BOOK_OF_THE_UNSPEAKABLE.get()){
				if(bookSeen) return false;
				else if(TheBookItem.hasEssenceHolder(stack)) return false;
				bookSeen = true;
			}else if(stack.getItem()==ModItems.ESSENCE_HOLDER.get()){
				if(essenceHolderSeen) return false;
				essenceHolderSeen = true;
			}else return false;
		}

		return bookSeen&&essenceHolderSeen;
	}
	@Override public ItemStack assemble(CraftingInventory inventory){
		ItemStack book = null;
		ItemStack essenceHolder = null;
		for(int i = 0; i<inventory.getContainerSize(); i++){
			ItemStack stack = inventory.getItem(i);
			if(stack.isEmpty()) continue;
			if(stack.getItem()==ModItems.BOOK_OF_THE_UNSPEAKABLE.get()){
				book = stack;
			}else if(stack.getItem()==ModItems.ESSENCE_HOLDER.get()){
				essenceHolder = stack;
			}
		}
		if(book==null) return ItemStack.EMPTY;
		else if(essenceHolder==null) return book;

		ItemStack out = book.copy();
		TheBookItem.setHasEssenceHolder(out, true);
		//noinspection ConstantConditions
		EssenceHolder h = out.getCapability(EssenceHolder.capability).orElse(null);
		//noinspection ConstantConditions
		EssenceHolder h2 = essenceHolder.getCapability(EssenceHolder.capability).orElse(null);
		//noinspection ConstantConditions
		if(h!=null&&h2!=null){
			for(EssenceType type : EssenceType.values())
				h.setEssence(type, h2.getEssence(type));
		}

		return out;
	}
	@Override public boolean canCraftInDimensions(int x, int y){
		return x*y>=2;
	}
	@Override public NonNullList<Ingredient> getIngredients(){
		return NonNullList.of(Ingredient.EMPTY, Ingredient.of(ModItems.BOOK_OF_THE_UNSPEAKABLE.get()), Ingredient.of(ModItems.ESSENCE_HOLDER.get()));
	}
	@Override public ItemStack getResultItem(){
		ItemStack stack = new ItemStack(ModItems.BOOK_OF_THE_UNSPEAKABLE.get());
		TheBookItem.setHasEssenceHolder(stack, true);
		return stack;
	}
	@Override public ResourceLocation getId(){
		return id;
	}
	@Override public IRecipeSerializer<?> getSerializer(){
		return ModRecipes.ESSENCE_HOLDER_BOOK.get();
	}
}
