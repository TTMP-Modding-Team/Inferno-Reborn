package ttmp.infernoreborn.network;

import net.minecraft.network.PacketBuffer;
import ttmp.infernoreborn.infernaltype.InfernalType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SyncInfernalTypeMsg{
	public static SyncInfernalTypeMsg read(PacketBuffer buf){
		Set<InfernalType> list = new HashSet<>();
		for(int i = buf.readVarInt(); i>0; i--)
			list.add(InfernalType.read(buf));
		return new SyncInfernalTypeMsg(list);
	}

	private final Collection<InfernalType> infernalTypes;

	public SyncInfernalTypeMsg(Collection<InfernalType> infernalTypes){
		this.infernalTypes = infernalTypes;
	}

	public Collection<InfernalType> getInfernalTypes(){
		return infernalTypes;
	}

	public void write(PacketBuffer buf){
		buf.writeVarInt(infernalTypes.size());
		for(InfernalType s : infernalTypes)
			s.write(buf);
	}
}
