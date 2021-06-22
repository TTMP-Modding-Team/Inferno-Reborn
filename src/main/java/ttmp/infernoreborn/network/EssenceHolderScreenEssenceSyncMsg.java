package ttmp.infernoreborn.network;

import net.minecraft.network.PacketBuffer;
import ttmp.infernoreborn.util.EssenceHolder;
import ttmp.infernoreborn.util.EssenceType;

public class EssenceHolderScreenEssenceSyncMsg{
	private final int[] essences = new int[EssenceType.values().length];

	public EssenceHolderScreenEssenceSyncMsg(EssenceHolder essenceHolder){
		for(EssenceType type : EssenceType.values())
			this.essences[type.ordinal()] = essenceHolder.getEssence(type);
	}
	public EssenceHolderScreenEssenceSyncMsg(PacketBuffer buf){
		for(int i = 0; i<this.essences.length; i++)
			this.essences[i] = buf.readVarInt();
	}

	public int[] getEssences(){
		return essences;
	}

	public void write(PacketBuffer buf){
		for(int essence : this.essences)
			buf.writeVarInt(essence);
	}
}
