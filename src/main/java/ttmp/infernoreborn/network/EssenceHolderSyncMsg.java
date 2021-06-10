package ttmp.infernoreborn.network;

import net.minecraft.network.PacketBuffer;
import ttmp.infernoreborn.capability.EssenceHolder;
import ttmp.infernoreborn.util.EssenceType;

public class EssenceHolderSyncMsg extends ItemSyncMsg{
	private final int[] essences = new int[EssenceType.values().length];

	public EssenceHolderSyncMsg(int slot, EssenceHolder essenceHolder){
		super(slot);
		this.essences = essenceHolder.getEssences();
	}
	public EssenceHolderSyncMsg(PacketBuffer buf){
		super(buf);
		this.essenceHolder = new EssenceHolder();
	}
	@Override protected void doWrite(PacketBuffer buf){

	}
}
