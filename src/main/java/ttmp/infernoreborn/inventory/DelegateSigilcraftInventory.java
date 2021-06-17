package ttmp.infernoreborn.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;

public class DelegateSigilcraftInventory implements SigilcraftInventory{
	protected final SigilcraftInventory delegate;
	protected final Container container;

	public DelegateSigilcraftInventory(SigilcraftInventory delegate, Container container){
		this.delegate = delegate;
		this.container = container;
	}

	@Override public int getContainerSize(){
		return delegate.getContainerSize();
	}
	@Override public boolean isEmpty(){
		return delegate.isEmpty();
	}
	@Override public ItemStack getItem(int slot){
		return delegate.getItem(slot);
	}
	@Override public ItemStack removeItem(int slot, int amount){
		ItemStack stack = delegate.removeItem(slot, amount);
		if(!stack.isEmpty()) container.slotsChanged(this);
		return stack;
	}
	@Override public ItemStack removeItemNoUpdate(int slot){
		return delegate.removeItemNoUpdate(slot);
	}
	@Override public void setItem(int slot, ItemStack stack){
		delegate.setItem(slot, stack);
		container.slotsChanged(this);
	}
	@Override public void setChanged(){
		delegate.setChanged();
	}
	@Override public boolean stillValid(PlayerEntity player){
		return delegate.stillValid(player);
	}
	@Override public void clearContent(){
		delegate.clearContent();
	}

	@Override public int getWidth(){
		return delegate.getWidth();
	}
	@Override public int getHeight(){
		return delegate.getHeight();
	}
}
