package ttmp.infernoreborn.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.function.IntConsumer;

public final class BaseInventory implements IItemHandlerModifiable{
	private final ItemStackHandler delegate;

	@Nullable private ItemValidator itemValidator;
	@Nullable private IntConsumer onContentsChanged;

	public BaseInventory(int size){
		this.delegate = new ItemStackHandler(size){
			@Override public boolean isItemValid(int slot, ItemStack stack){
				return itemValidator==null||itemValidator.isItemValid(slot, stack);
			}
			@Override protected void onContentsChanged(int slot){
				if(onContentsChanged!=null) onContentsChanged.accept(slot);
			}
		};
	}

	public BaseInventory setItemValidator(@Nullable ItemValidator itemValidator){
		this.itemValidator = itemValidator;
		return this;
	}
	public BaseInventory setOnContentsChanged(@Nullable IntConsumer onContentsChanged){
		this.onContentsChanged = onContentsChanged;
		return this;
	}

	@Override public void setStackInSlot(int slot, ItemStack stack){delegate.setStackInSlot(slot, stack);}
	@Override public int getSlots(){return delegate.getSlots();}
	@Override public ItemStack getStackInSlot(int slot){return delegate.getStackInSlot(slot);}
	@Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){return delegate.insertItem(slot, stack, simulate);}
	@Override public ItemStack extractItem(int slot, int amount, boolean simulate){return delegate.extractItem(slot, amount, simulate);}
	@Override public int getSlotLimit(int slot){return delegate.getSlotLimit(slot);}
	@Override public boolean isItemValid(int slot, ItemStack stack){return itemValidator==null||itemValidator.isItemValid(slot, stack);}

	public void write(CompoundNBT tag){
		ListNBT list = new ListNBT();
		for(int i = 0; i<delegate.getSlots(); i++){
			if(!delegate.getStackInSlot(i).isEmpty()){
				CompoundNBT tag2 = new CompoundNBT();
				tag2.putInt("Slot", i);
				delegate.getStackInSlot(i).save(tag2);
				list.add(tag2);
			}
		}
		if(!list.isEmpty()) tag.put("Items", list);
	}

	public void read(CompoundNBT tag){
		if(!tag.contains("Items", Constants.NBT.TAG_LIST)) return;
		ListNBT list = tag.getList("Items", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i<list.size(); i++){
			CompoundNBT tag2 = list.getCompound(i);
			int slot = tag2.getInt("Slot");
			if(slot>=0&&slot<delegate.getSlots())
				delegate.setStackInSlot(slot, ItemStack.of(tag2));
		}
	}

	public interface ItemValidator{
		boolean isItemValid(int slot, ItemStack stack);
	}
}
