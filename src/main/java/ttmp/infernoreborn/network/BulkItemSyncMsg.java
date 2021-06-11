package ttmp.infernoreborn.network;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

public abstract class BulkItemSyncMsg<M extends ItemSyncMsg>{
	private final Int2ObjectMap<M> messages = new Int2ObjectArrayMap<>();

	public BulkItemSyncMsg(M[] messages){
		for(M message : messages) add(message);
	}
	public BulkItemSyncMsg(Iterable<M> messages){
		for(M message : messages) add(message);
	}
	public BulkItemSyncMsg(PacketBuffer buf){
		for(int i = buf.readVarInt(); i>0; i--){
			M m = read(buf);
			messages.put(m.getSlot(), m);
		}
	}

	private void add(M message){
		if(!canHandle(message))
			throw new IllegalArgumentException("Unsupported type of message '"+message+'\'');
		if(this.messages.put(message.getSlot(), message)!=null)
			throw new IllegalArgumentException("Multiple message for same target slot "+message.getSlot());
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
	protected boolean canHandle(M message){
		return true;
	}

	public void sync(PlayerEntity player){
		for(M m : messages.values()) m.sync(player);
	}
}
