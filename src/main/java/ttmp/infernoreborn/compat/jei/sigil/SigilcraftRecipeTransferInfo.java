package ttmp.infernoreborn.compat.jei.sigil;

import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.contents.container.SigilEngravingTableContainer;

import java.util.ArrayList;
import java.util.List;

public class SigilcraftRecipeTransferInfo<C extends SigilEngravingTableContainer> implements IRecipeTransferInfo<C>{
	private final Class<C> containerClass;
	private final ResourceLocation recipeCategoryUid;
	private final int inventorySlotStart;
	private final int inventorySlotCount;
	private final RecipeSize size;

	public SigilcraftRecipeTransferInfo(Class<C> containerClass, ResourceLocation recipeCategoryUid, RecipeSize size){
		this.containerClass = containerClass;
		this.recipeCategoryUid = recipeCategoryUid;
		this.inventorySlotStart = size.size*size.size+1;
		this.inventorySlotCount = 36;
		this.size = size;
	}
	@Override public Class<C> getContainerClass(){
		return containerClass;
	}
	@Override public ResourceLocation getRecipeCategoryUid(){
		return recipeCategoryUid;
	}
	@Override public boolean canHandle(C container){
		return true;
	}
	@Override public List<Slot> getRecipeSlots(C container){
		if(isRecipeUidMatching(RecipeSize.X3))
			return getSlotsMatchesWithSize(container, RecipeSize.X3);
		else if(isRecipeUidMatching(RecipeSize.X5))
			return getSlotsMatchesWithSize(container, RecipeSize.X5);
		else
			return getSlotsMatchesWithSize(container, RecipeSize.X7);
	}

	@Override public List<Slot> getInventorySlots(C container){
		List<Slot> slots = new ArrayList<>();
		for(int i = inventorySlotStart; i<inventorySlotStart+inventorySlotCount; i++){
			Slot slot = container.getSlot(i);
			slots.add(slot);
		}
		return slots;
	}
	private List<Slot> getSlotsMatchesWithSize(C container, RecipeSize inputSize){
		int tableSize = size.size;
		int recipeSize = inputSize.size;
		List<Slot> slots = new ArrayList<>();
		int recipeSlotMiddle = (tableSize*tableSize)/2+1;
		int startSlot = recipeSlotMiddle-(tableSize*(recipeSize/2))-recipeSize/2;
		for(int i = 0; i<recipeSize; i++){
			for(int j = 0; j<recipeSize; j++){
				Slot slot = container.getSlot(startSlot+j+(i*tableSize));
				slots.add(slot);
			}
		}
		return slots;
	}
	private boolean isRecipeUidMatching(RecipeSize size){
		return recipeCategoryUid.equals(ShapedSigilTableCraftingRecipeCategory.getUidBySize(size))||recipeCategoryUid.equals(ShapedSigilEngravingRecipeCategory.getUidBySize(size));
	}
}
