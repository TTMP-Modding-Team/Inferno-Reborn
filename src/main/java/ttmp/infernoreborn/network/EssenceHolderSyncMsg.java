package ttmp.infernoreborn.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import ttmp.infernoreborn.capability.Caps;
import ttmp.infernoreborn.util.EssenceHolder;
import ttmp.infernoreborn.util.EssenceType;

public class EssenceHolderSyncMsg extends ItemSyncMsg{
	private final int[] essences = new int[EssenceType.values().length];

	public EssenceHolderSyncMsg(int slot, EssenceHolder essenceHolder){
		super(slot);
		for(EssenceType type : EssenceType.values())
			this.essences[type.ordinal()] = essenceHolder.getEssence(type);
	}
	public EssenceHolderSyncMsg(PacketBuffer buf){
		super(buf);
		for(int i = 0; i<this.essences.length; i++)
			this.essences[i] = buf.readVarInt();
	}
	@Override protected void doWrite(PacketBuffer buf){
		for(int essence : this.essences)
			buf.writeVarInt(essence);
	}

	@Override protected void doSync(PlayerEntity player, ItemStack stack){
		stack.getCapability(Caps.essenceHolder).ifPresent(essenceHolder -> {
			for(EssenceType type : EssenceType.values())
				essenceHolder.setEssence(type, essences[type.ordinal()]);
		});
	}

	public static class Bulk extends BulkItemSyncMsg<EssenceHolderSyncMsg>{
		public Bulk(EssenceHolderSyncMsg[] messages){
			super(messages);
		}
		public Bulk(Iterable<EssenceHolderSyncMsg> messages){
			super(messages);
		}
		public Bulk(PacketBuffer buf){
			super(buf);
		}

		@Override protected EssenceHolderSyncMsg read(PacketBuffer buf){
			return new EssenceHolderSyncMsg(buf);
		}
	}
}
