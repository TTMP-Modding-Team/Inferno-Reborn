package ttmp.infernoreborn.network;

import net.minecraft.network.PacketBuffer;
import ttmp.infernoreborn.api.sigil.Sigil;
import ttmp.infernoreborn.api.sigil.SigilHolder;
import ttmp.infernoreborn.contents.Sigils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SyncBodySigilMsg{
	public static SyncBodySigilMsg read(PacketBuffer buf){
		List<Sigil> list = new ArrayList<>();
		for(int i = 0, j = buf.readVarInt(); i<j; i++){
			Sigil sigil = Sigils.getRegistry().getValue(buf.readVarInt());
			if(sigil!=null) list.add(sigil);
		}
		return new SyncBodySigilMsg(list);
	}

	private final Collection<Sigil> sigils;

	public SyncBodySigilMsg(SigilHolder sigils){
		this(sigils.getSigils());
	}
	public SyncBodySigilMsg(Collection<Sigil> sigils){
		this.sigils = sigils;
	}

	public Collection<Sigil> getSigils(){
		return sigils;
	}

	public void write(PacketBuffer buf){
		buf.writeVarInt(sigils.size());
		for(Sigil sigil : sigils)
			buf.writeVarInt(Sigils.getRegistry().getID(sigil));
	}
}
