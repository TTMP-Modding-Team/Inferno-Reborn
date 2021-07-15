package ttmp.infernoreborn.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;

public abstract class ItemSyncMsg{
	private final int slot;

	public ItemSyncMsg(int slot){
		this.slot = slot;
	}
	public ItemSyncMsg(PacketBuffer buf){
		this.slot = buf.readVarInt();
	}

	public int getSlot(){
		return slot;
	}

	public void write(PacketBuffer buf){
		buf.writeVarInt(slot);
		doWrite(buf);
	}

	protected abstract void doWrite(PacketBuffer buf);

	public void sync(PlayerEntity player){
		if(slot<0) return;
		NonNullList<ItemStack> items = player.containerMenu.getItems();
		if(items.size()<=slot) return;
		ItemStack stack = items.get(slot);
		if(stack.isEmpty()) return;
		doSync(player, stack);
	}
	protected abstract void doSync(PlayerEntity player, ItemStack stack);

	@Override public String toString(){
		return getClass().getSimpleName()+"{"+slot+"}";
	}
}
