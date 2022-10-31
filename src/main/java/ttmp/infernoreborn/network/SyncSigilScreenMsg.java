package ttmp.infernoreborn.network;

import net.minecraft.network.PacketBuffer;
import ttmp.infernoreborn.contents.Sigils;
import ttmp.infernoreborn.contents.sigil.Sigil;

import java.util.HashSet;
import java.util.Set;

public final class SyncSigilScreenMsg{
	public static SyncSigilScreenMsg read(PacketBuffer buffer){
		Set<Sigil> currentSigils = new HashSet<>(), newSigils = new HashSet<>();
		for(int i = buffer.readVarInt(); i>0; i--){
			Sigil s = Sigils.getRegistry().getValue(buffer.readVarInt());
			if(s!=null) currentSigils.add(s);
		}
		for(int i = buffer.readVarInt(); i>0; i--){
			Sigil s = Sigils.getRegistry().getValue(buffer.readVarInt());
			if(s!=null) newSigils.add(s);
		}
		return new SyncSigilScreenMsg(currentSigils, newSigils);
	}

	private final Set<Sigil> currentSigils;
	private final Set<Sigil> newSigils;

	public SyncSigilScreenMsg(Set<Sigil> currentSigils, Set<Sigil> newSigils){
		this.currentSigils = currentSigils;
		this.newSigils = newSigils;
	}

	public Set<Sigil> getCurrentSigils(){
		return currentSigils;
	}
	public Set<Sigil> getNewSigils(){
		return newSigils;
	}

	@Override public String toString(){
		return "SyncSigilScreenMsg{"+
				"currentSigils="+currentSigils+
				", newSigils="+newSigils+
				'}';
	}

	public void write(PacketBuffer buffer){
		buffer.writeVarInt(currentSigils.size());
		for(Sigil s : currentSigils) buffer.writeVarInt(Sigils.getRegistry().getID(s));
		buffer.writeVarInt(newSigils.size());
		for(Sigil s : newSigils) buffer.writeVarInt(Sigils.getRegistry().getID(s));
	}
}
