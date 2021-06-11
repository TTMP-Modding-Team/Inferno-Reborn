package ttmp.infernoreborn.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

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
		ItemStack stack = player.inventory.getItem(slot);
		if(stack.isEmpty()) return;
		doSync(player, stack);
	}
	protected abstract void doSync(PlayerEntity player, ItemStack stack);
}
