package ttmp.infernoreborn.contents.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import ttmp.infernoreborn.contents.ModContainers;
import ttmp.infernoreborn.contents.sigil.holder.SigilHolder;

import javax.annotation.Nullable;

public class SigilScrapperContainer extends Container{
	private final IInventory sigilScrapperInventory = new Inventory(1);
	private final IWorldPosCallable world;

	public SigilScrapperContainer(int id, PlayerInventory playerInventory){
		this(id, playerInventory, IWorldPosCallable.NULL);
	}
	public SigilScrapperContainer(int id, PlayerInventory playerInventory, IWorldPosCallable world){
		super(ModContainers.SIGIL_SCRAPPER.get(), id);
		this.world = world;

		addSlot(new Slot(sigilScrapperInventory, 0, 3, 3));

		for(int y = 0; y<3; ++y)
			for(int x = 0; x<9; ++x)
				this.addSlot(new Slot(playerInventory, x+y*9+9, 8+x*18, 88+y*18));
		for(int i1 = 0; i1<9; ++i1)
			this.addSlot(new Slot(playerInventory, i1, 8+i1*18, 146));
	}

	public ItemStack getStack(){
		return sigilScrapperInventory.getItem(0);
	}

	public void setStack(ItemStack stack){
		this.sigilScrapperInventory.setItem(0, stack);
	}

	@Nullable public SigilHolder getSigilHolder(){
		return SigilHolder.of(sigilScrapperInventory.getItem(0));
	}

	@Override public boolean stillValid(PlayerEntity pPlayer){
		return true;
	}

	@Override public ItemStack quickMoveStack(PlayerEntity player, int slotIndex){
		ItemStack returns = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotIndex);
		if(slot!=null&&slot.hasItem()){
			ItemStack stackAtSlot = slot.getItem();
			returns = stackAtSlot.copy();
			if(slotIndex<1){
				if(!this.moveItemStackTo(stackAtSlot, 1, this.slots.size(), true)) return ItemStack.EMPTY;
			}else if(!this.moveItemStackTo(stackAtSlot, 0, 1, false)) return ItemStack.EMPTY;

			if(stackAtSlot.isEmpty()) slot.set(ItemStack.EMPTY);
			else slot.setChanged();
		}

		return returns;
	}

	@Override public void removed(PlayerEntity pPlayer){
		super.removed(pPlayer);
		this.world.execute((level, pos) -> this.clearContainer(pPlayer, level, this.sigilScrapperInventory));
	}
}
