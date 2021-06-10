package ttmp.infernoreborn.network;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.network.PacketBuffer;

public abstract class BulkItemSyncMsg<M extends ItemSyncMsg>{
	private final Int2ObjectMap<M> messages = new Int2ObjectArrayMap<>();

	public BulkItemSyncMsg(M... messages){
		for(M message : messages){
			if(!canHandle(message))
				throw new IllegalArgumentException("Unsupported type of message '"+message+'\'');
			if(this.messages.put(message.getSlot(), message)!=null)
				throw new IllegalArgumentException("Multiple message for same target slot "+message.getSlot());
		}
	}
	public BulkItemSyncMsg(PacketBuffer buf){
		for(int i = buf.readVarInt(); i>0; i--){
			M m = read(buf);
			messages.put(m.getSlot(), m);
		}
	}

	public Int2ObjectMap<M> getMessages(){
		return messages;
	}

	public void write(PacketBuffer buf){
		buf.writeVarInt(messages.size());
		for(M m : messages.values())
			m.write(buf);
	}

	protected abstract M read(PacketBuffer buf);
	protected abstract boolean canHandle(M message);
}
