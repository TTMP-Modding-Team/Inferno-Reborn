package ttmp.infernoreborn.network;

import net.minecraft.network.PacketBuffer;

public class EssenceHolderSlotClickMsg{
	public static EssenceHolderSlotClickMsg read(PacketBuffer buf){
		return new EssenceHolderSlotClickMsg(buf.readVarInt(), buf.readVarInt(), buf.readBoolean());
	}

	private final int slot;
	private final int type;
	private final boolean shift;

	public EssenceHolderSlotClickMsg(int slot, int type, boolean shift){
		this.slot = slot;
		this.type = type;
		this.shift = shift;
	}

	public int getSlot(){
		return slot;
	}
	public int getType(){
		return type;
	}
	public boolean isShift(){
		return shift;
	}

	public void write(PacketBuffer buf){
		buf.writeVarInt(slot);
		buf.writeVarInt(type);
		buf.writeBoolean(shift);
	}
}
