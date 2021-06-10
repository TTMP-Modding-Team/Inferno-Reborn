package ttmp.infernoreborn.network;

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
}
