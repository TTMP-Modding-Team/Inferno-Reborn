package ttmp.infernoreborn.network;

import net.minecraft.network.PacketBuffer;
import ttmp.infernoreborn.contents.Sigils;
import ttmp.infernoreborn.contents.sigil.Sigil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SyncSigilScrapperScreenMsg{
	public static SyncSigilScrapperScreenMsg read(PacketBuffer buf){
		int maxSigils = buf.readVarInt();
		List<Sigil> sigils = new ArrayList<>();
		for(int i = buf.readVarInt(); i>0; i--){
			Sigil s = Sigils.getRegistry().getValue(buf.readVarInt());
			if(s!=null) sigils.add(s);
		}
		return new SyncSigilScrapperScreenMsg(maxSigils, sigils);
	}

	private final int maxSigils;
	private final List<Sigil> sigils;

	public SyncSigilScrapperScreenMsg(int maxSigils, Collection<Sigil> sigils){
		this(maxSigils, new ArrayList<>(sigils));
	}
	public SyncSigilScrapperScreenMsg(int maxSigils, List<Sigil> sigils){
		this.maxSigils = maxSigils;
		this.sigils = sigils;
	}

	public int getMaxSigils(){
		return maxSigils;
	}
	public List<Sigil> getSigils(){
		return sigils;
	}

	public void write(PacketBuffer buf){
		buf.writeVarInt(maxSigils);
		buf.writeVarInt(sigils.size());
		for(Sigil s : sigils) buf.writeVarInt(Sigils.getRegistry().getID(s));
	}
}
