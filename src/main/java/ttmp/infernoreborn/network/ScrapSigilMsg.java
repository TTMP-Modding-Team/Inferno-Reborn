package ttmp.infernoreborn.network;

import net.minecraft.network.PacketBuffer;
import ttmp.infernoreborn.api.sigil.Sigil;
import ttmp.infernoreborn.contents.Sigils;

import javax.annotation.Nullable;

public class ScrapSigilMsg{
	public static ScrapSigilMsg read(PacketBuffer buf){
		int sigilId = buf.readVarInt();
		return new ScrapSigilMsg(Sigils.getRegistry().getValue(sigilId));
	}

	@Nullable private final Sigil sigil;

	public ScrapSigilMsg(@Nullable Sigil sigil){
		this.sigil = sigil;
	}

	@Nullable public Sigil getSigil(){
		return sigil;
	}

	public void write(PacketBuffer buf){
		buf.writeVarInt(Sigils.getRegistry().getID(sigil));
	}
}
