package ttmp.infernoreborn.contents.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import ttmp.infernoreborn.contents.ModContainers;
import ttmp.infernoreborn.inventory.BaseInventory;

public class FuelBasedStoveContainer extends Container{
	public static FuelBasedStoveContainer furnace(int id, PlayerInventory playerInventory){
		return furnace(id, playerInventory, new BaseInventory(1)
				.setItemValidator((slot, stack) -> ForgeHooks.getBurnTime(stack, IRecipeType.SMELTING)>0));
	}
	public static FuelBasedStoveContainer furnace(int id, PlayerInventory playerInventory, IItemHandlerModifiable stoveInventory){
		return new FuelBasedStoveContainer(ModContainers.FURNACE_STOVE.get(), id, playerInventory, stoveInventory);
	}

	public static FuelBasedStoveContainer foundry(int id, PlayerInventory playerInventory){
		return foundry(id, playerInventory, new BaseInventory(1)
				.setItemValidator((slot, stack) -> ForgeHooks.getBurnTime(stack, IRecipeType.BLASTING)>=1600));
	}
	public static FuelBasedStoveContainer foundry(int id, PlayerInventory playerInventory, IItemHandlerModifiable stoveInventory){
		return new FuelBasedStoveContainer(ModContainers.FOUNDRY_STOVE.get(), id, playerInventory, stoveInventory);
	}

	public static FuelBasedStoveContainer nether(int id, PlayerInventory playerInventory){
		return nether(id, playerInventory, new BaseInventory(1)
				.setItemValidator((slot, stack) -> stack.getItem()==Items.BLAZE_ROD));
	}
	public static FuelBasedStoveContainer nether(int id, PlayerInventory playerInventory, IItemHandlerModifiable stoveInventory){
		return new FuelBasedStoveContainer(ModContainers.NETHER_STOVE.get(), id, playerInventory, stoveInventory);
	}

	private final IItemHandlerModifiable stoveInventory;

	protected FuelBasedStoveContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, IItemHandlerModifiable stoveInventory){
		super(type, id);
		this.stoveInventory = stoveInventory;
		this.addSlot(new SlotItemHandler(stoveInventory, 0, 80, 1));

		for(int y = 0; y<3; ++y)
			for(int x = 0; x<9; ++x)
				this.addSlot(new Slot(playerInventory, x+y*9+9, 8+x*18, 36+y*18));
		for(int i1 = 0; i1<9; ++i1)
			this.addSlot(new Slot(playerInventory, i1, 8+i1*18, 94));
	}

	@Override public boolean stillValid(PlayerEntity player){
		return true;
	}

	@Override public ItemStack quickMoveStack(PlayerEntity player, int slotIndex){
		ItemStack returns = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotIndex);
		if(slot!=null&&slot.hasItem()){
			ItemStack stackAtSlot = slot.getItem();
			returns = stackAtSlot.copy();
			if(slotIndex==0){
				if(!this.moveItemStackTo(stackAtSlot, 1, 1+36, true)) return ItemStack.EMPTY;
			}else if(stoveInventory.isItemValid(0, stackAtSlot)){
				if(!this.moveItemStackTo(stackAtSlot, 0, 1, false)) return ItemStack.EMPTY;
			}else if(slotIndex<1+27){
				if(!this.moveItemStackTo(stackAtSlot, 1+27, 1+36, false)) return ItemStack.EMPTY;
			}else if(!this.moveItemStackTo(stackAtSlot, 1, 1+27, false)) return ItemStack.EMPTY;

			if(stackAtSlot.isEmpty()) slot.set(ItemStack.EMPTY);
			else slot.setChanged();
		}

		return returns;
	}
}
