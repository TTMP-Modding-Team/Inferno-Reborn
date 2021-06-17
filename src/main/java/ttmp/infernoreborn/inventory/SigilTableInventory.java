package ttmp.infernoreborn.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

public class SigilTableInventory implements SigilcraftInventory{
	protected final NonNullList<ItemStack> items;
	protected final int width;
	protected final int height;

	public SigilTableInventory(int width, int height){
		if(width%2!=1) throw new IllegalArgumentException("width");
		if(height%2!=1) throw new IllegalArgumentException("height");
		this.items = NonNullList.withSize(width*height, ItemStack.EMPTY);
		this.width = width;
		this.height = height;
	}

	@Override public int getContainerSize(){
		return items.size();
	}
	@Override public boolean isEmpty(){
		for(ItemStack itemstack : this.items)
			if(!itemstack.isEmpty()) return false;
		return true;
	}
	@Override public ItemStack getItem(int slot){
		return slot>=this.getContainerSize() ? ItemStack.EMPTY : this.items.get(slot);
	}
	@Override public ItemStack removeItem(int slot, int amount){
		return ItemStackHelper.removeItem(this.items, slot, amount);
	}
	@Override public ItemStack removeItemNoUpdate(int slot){
		return ItemStackHelper.takeItem(this.items, slot);
	}
	@Override public void setItem(int slot, ItemStack stack){
		this.items.set(slot, stack);
	}
	@Override public void setChanged(){}
	@Override public boolean stillValid(PlayerEntity player){
		return true;
	}
	@Override public void clearContent(){
		this.items.clear();
	}

	@Override public int getWidth(){
		return this.width;
	}
	@Override public int getHeight(){
		return this.height;
	}

	public void saveTo(CompoundNBT nbt){
		ItemStackHelper.saveAllItems(nbt, this.items);
	}
	public void loadFrom(CompoundNBT nbt){
		ItemStackHelper.loadAllItems(nbt, this.items);
	}
}
