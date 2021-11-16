package ttmp.infernoreborn.compat.jei.sigil;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.config.ServerInfo;
import mezz.jei.network.Network;
import mezz.jei.network.packets.PacketRecipeTransfer;
import mezz.jei.transfer.RecipeTransferUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.contents.recipe.sigilcraft.BaseSigilcraftRecipe;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static ttmp.infernoreborn.InfernoReborn.LOGGER;

public class SigilcraftRecipeTransferHandler<C extends Container> implements IRecipeTransferHandler<C>{
	private final Class<C> containerClass;
	private final RecipeSize containerSize;
	private final RecipeSize recipeSize;
	private final boolean engraving;
	private final boolean bodySigilStation;
	private final IRecipeTransferHandlerHelper helper;
	private final IStackHelper stackHelper;

	public SigilcraftRecipeTransferHandler(Class<C> containerClass,
	                                       RecipeSize containerSize,
	                                       RecipeSize recipeSize,
	                                       boolean engraving,
	                                       boolean bodySigilStation,
	                                       IRecipeTransferHandlerHelper helper,
	                                       IStackHelper stackHelper){
		if(bodySigilStation&&!engraving)
			throw new IllegalArgumentException("Body sigil station but not engraving?");
		this.containerClass = containerClass;
		this.containerSize = containerSize;
		this.recipeSize = recipeSize;
		this.engraving = engraving;
		this.bodySigilStation = bodySigilStation;
		this.helper = helper;
		this.stackHelper = stackHelper;
	}

	@Override public Class<C> getContainerClass(){
		return containerClass;
	}

	@Nullable @Override public IRecipeTransferError transferRecipe(C container, Object recipe, IRecipeLayout recipeLayout, PlayerEntity player, boolean maxTransfer, boolean doTransfer){
		if(!ServerInfo.isJeiOnServer())
			return helper.createUserErrorWithTooltip(new TranslationTextComponent("jei.tooltip.error.recipe.transfer.no.server"));
		if(containerSize.ordinal()<recipeSize.ordinal())
			return helper.createUserErrorWithTooltip(new TranslationTextComponent("recipe.infernoreborn.error.sigilcraft.too_large"));

		if(!(recipe instanceof BaseSigilcraftRecipe)) return helper.createInternalError();
		BaseSigilcraftRecipe r = (BaseSigilcraftRecipe)recipe;
		if(!r.canCraftInDimensions(containerSize.size, containerSize.size))
			return helper.createUserErrorWithTooltip(new TranslationTextComponent("recipe.infernoreborn.error.sigilcraft.too_large"));

		Map<Integer, Slot> inventorySlots = getInventorySlots(container);
		Map<Integer, Slot> craftingSlots = getCraftingSlots(container);

		int inputCount = 0;
		IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
		for(IGuiIngredient<ItemStack> ingredient : itemStackGroup.getGuiIngredients().values()){
			if(ingredient.isInput()&&!ingredient.getAllIngredients().isEmpty()){
				inputCount++;
			}
		}

		if(inputCount>craftingSlots.size()){
			LOGGER.error("Recipe Transfer helper {} does not work for container {}. "+
							"{} ingredients are marked as inputs in IRecipeCategory#setRecipe, but there are only {} crafting slots defined for the recipe transfer helper.",
					getContainerClass(), container.getClass(), inputCount, craftingSlots.size());
			return helper.createInternalError();
		}

		Map<Integer, ItemStack> availableItemStacks = new HashMap<>();

		for(Slot slot : craftingSlots.values()){
			ItemStack stack = slot.getItem();
			if(!stack.isEmpty()){
				if(!slot.mayPickup(player)){
					LOGGER.error("Recipe Transfer helper {} does not work for container {}. Player can't move item out of Crafting Slot number {}", getContainerClass(), container.getClass(), slot.index);
					return helper.createInternalError();
				}
				availableItemStacks.put(slot.index, stack.copy());
			}
		}

		for(Slot slot : inventorySlots.values()){
			ItemStack stack = slot.getItem();
			if(!stack.isEmpty()) availableItemStacks.put(slot.index, stack.copy());
		}

		RecipeTransferUtil.MatchingItemsResult matchingItemsResult = RecipeTransferUtil.getMatchingItems(stackHelper, availableItemStacks, itemStackGroup.getGuiIngredients());

		if(matchingItemsResult.missingItems.size()>0)
			return helper.createUserErrorForSlots(new TranslationTextComponent("jei.tooltip.error.recipe.transfer.missing"), matchingItemsResult.missingItems);

		List<Integer> craftingSlotIndexes = new ArrayList<>(craftingSlots.keySet());
		Collections.sort(craftingSlotIndexes);

		List<Integer> inventorySlotIndexes = new ArrayList<>(inventorySlots.keySet());
		Collections.sort(inventorySlotIndexes);

		// check that the slots exist and can be altered
		for(Entry<Integer, Integer> entry : matchingItemsResult.matchingItems.entrySet()){
			int craftNumber = entry.getKey();
			int slotNumber = craftingSlotIndexes.get(craftNumber);
			if(slotNumber<0||slotNumber>=container.slots.size()){
				LOGGER.error("Recipes Transfer Helper {} references slot {} outside of the inventory's size {}", getContainerClass(), slotNumber, container.slots.size());
				return helper.createInternalError();
			}
		}

		if(doTransfer){
			Map<Integer, Integer> m2 = new HashMap<>();
			for(Entry<Integer, Integer> e : matchingItemsResult.matchingItems.entrySet())
				m2.put(mapSlot(e.getKey()), e.getValue());
			Network.sendPacketToServer(new PacketRecipeTransfer(m2, craftingSlotIndexes, inventorySlotIndexes, maxTransfer, true));
		}

		return null;
	}

	private int mapSlot(int index){
		boolean afterCenter = index >= recipeSize.size * recipeSize.size/2;
		if(afterCenter) index = index + 1;
		int xOffset = containerSize.size/2-recipeSize.size/2;
		int yOffset = containerSize.size/2-recipeSize.size/2;
		int x = index%recipeSize.size;
		int y = index/recipeSize.size;
		int ind = containerSize.toIndex(x+xOffset, y+yOffset);
		return afterCenter ? ind-1 : ind;
	}

	protected Map<Integer, Slot> getCraftingSlots(C container){
		Map<Integer, Slot> slots = new HashMap<>();
		int squareContainerSize = containerSize.size*containerSize.size;
		if(bodySigilStation){
			for(int i = 0; i<squareContainerSize-1; i++)
				slots.put(i, container.getSlot(i));
		}else{
			for(int i = 0; i<squareContainerSize; i++)
				if(!engraving||i!=containerSize.centerIndex())
					slots.put(i+1, container.getSlot(i+1));
		}
		return slots;
	}

	protected Map<Integer, Slot> getInventorySlots(C container){
		Map<Integer, Slot> slots = new HashMap<>();
		for(int s = containerSize.size*containerSize.size+(bodySigilStation ? -1 : 1), i = s; i<s+36; i++)
			slots.put(i, container.getSlot(i));
		return slots;
	}
}
