package ttmp.infernoreborn.contents.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import ttmp.infernoreborn.capability.Caps;
import ttmp.infernoreborn.contents.ModContainers;
import ttmp.infernoreborn.contents.tile.FoundryTile;
import ttmp.infernoreborn.util.EssenceType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.IntSupplier;

public class FoundryContainer extends Container{
	private final IItemHandlerModifiable foundry;

	private final IntReferenceHolder process;
	private final IntReferenceHolder maxProcess;
	@Nullable private final IntSupplier processSupplier;
	@Nullable private final IntSupplier maxProcessSupplier;

	public FoundryContainer(int windowId, PlayerInventory playerInventory){
		this(windowId, playerInventory, new FoundryTile.ItemHandler(), null, null);
	}
	public FoundryContainer(int windowId,
	                        PlayerInventory playerInventory,
	                        IItemHandlerModifiable foundry,
	                        @Nullable IntSupplier processSupplier,
	                        @Nullable IntSupplier maxProcessSupplier){
		super(ModContainers.FOUNDRY.get(), windowId);
		this.foundry = foundry;
		this.process = addDataSlot(IntReferenceHolder.standalone());
		this.maxProcess = addDataSlot(IntReferenceHolder.standalone());
		this.processSupplier = processSupplier;
		this.maxProcessSupplier = maxProcessSupplier;

		this.addSlot(new SlotItemHandler(foundry, FoundryTile.ESSENCE_HOLDER_SLOT, 8, 11));
		this.addSlot(new SlotItemHandler(foundry, FoundryTile.ESSENCE_INPUT_SLOT, 8, 29));
		this.addSlot(new SlotItemHandler(foundry, FoundryTile.INPUT_SLOT_1, 44, 20));
		this.addSlot(new SlotItemHandler(foundry, FoundryTile.INPUT_SLOT_2, 62, 20));
		this.addSlot(new ExtractOnlySlotItemHandler(foundry, FoundryTile.OUTPUT_SLOT_1, 116, 20));
		this.addSlot(new ExtractOnlySlotItemHandler(foundry, FoundryTile.OUTPUT_SLOT_2, 134, 20));

		for(int y = 0; y<3; ++y)
			for(int x = 0; x<9; ++x)
				this.addSlot(new Slot(playerInventory, x+y*9+9, 8+x*18, 74+y*18));
		for(int i1 = 0; i1<9; ++i1)
			this.addSlot(new Slot(playerInventory, i1, 8+i1*18, 132));
	}

	public IItemHandlerModifiable getFoundry(){
		return foundry;
	}

	public int getProcess(){
		return process.get();
	}
	public int getMaxProcess(){
		return maxProcess.get();
	}

	public double getProcessPercentage(){
		int maxProcess = getMaxProcess();
		if(maxProcess==0) return 0;
		int process = getProcess();
		return MathHelper.clamp((double)process/maxProcess, 0, 1);
	}

	@Override public void broadcastChanges(){
		if(processSupplier!=null) process.set(processSupplier.getAsInt());
		if(maxProcessSupplier!=null) maxProcess.set(maxProcessSupplier.getAsInt());
		super.broadcastChanges();
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
			if(slotIndex<foundry.getSlots()){
				if(!this.moveItemStackTo(stackAtSlot, foundry.getSlots(), this.slots.size(), true)) return ItemStack.EMPTY;
			}else{
				if(stackAtSlot.getCapability(Caps.essenceHolder).isPresent()){
					if(!this.moveItemStackTo(stackAtSlot, FoundryTile.ESSENCE_HOLDER_SLOT, FoundryTile.ESSENCE_HOLDER_SLOT+1, false)) return ItemStack.EMPTY;
				}else if(EssenceType.isEssenceItem(stackAtSlot)){
					if(!this.moveItemStackTo(stackAtSlot, FoundryTile.ESSENCE_INPUT_SLOT, FoundryTile.ESSENCE_INPUT_SLOT+1, false)) return ItemStack.EMPTY;
				}else if(!this.moveItemStackTo(stackAtSlot, FoundryTile.INPUT_SLOT_1, FoundryTile.INPUT_SLOT_2+1, false)) return ItemStack.EMPTY;
			}

			if(stackAtSlot.isEmpty()) slot.set(ItemStack.EMPTY);
			else slot.setChanged();
		}

		return returns;
	}

	private static final class ExtractOnlySlotItemHandler extends SlotItemHandler{
		public ExtractOnlySlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition){
			super(itemHandler, index, xPosition, yPosition);
		}

		@Override public boolean mayPlace(@Nonnull ItemStack stack){
			return false;
		}
	}
}
