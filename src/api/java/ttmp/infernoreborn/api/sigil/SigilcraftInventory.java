package ttmp.infernoreborn.api.sigil;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface SigilcraftInventory extends IInventory{
	int getWidth();
	int getHeight();

	default ItemStack getItem(int x, int y){
		return x>=0&&x<getWidth()&&y>=0&&y<getHeight() ? getItem(x+y*getWidth()) : ItemStack.EMPTY;
	}
	default ItemStack getCenterItem(){
		return getItem(getWidth()/2, getHeight()/2);
	}
}
