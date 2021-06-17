package ttmp.infernoreborn.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import ttmp.infernoreborn.contents.Sigils;
import ttmp.infernoreborn.sigil.holder.ItemSigilHolder;
import ttmp.infernoreborn.sigil.holder.SigilHolder;

import java.util.Arrays;

public class SigilHolderSyncMsg extends ItemSyncMsg{
	private final int[] sigils;
	private final long gibberishSeed;

	public SigilHolderSyncMsg(int slot, SigilHolder sigilHolder){
		super(slot);
		this.sigils = sigilHolder.getSigils().stream().mapToInt(Sigils.getRegistry()::getID).toArray();
		this.gibberishSeed = sigilHolder.getGibberishSeed();
	}
	public SigilHolderSyncMsg(PacketBuffer buf){
		super(buf);
		this.sigils = buf.readVarIntArray();
		this.gibberishSeed = buf.readLong();
	}
	@Override protected void doWrite(PacketBuffer buf){
		buf.writeVarIntArray(sigils);
		buf.writeLong(gibberishSeed);
	}

	@Override protected void doSync(PlayerEntity player, ItemStack stack){
		SigilHolder h = SigilHolder.of(stack);
		if(h==null) return;
		h.clear();
		Arrays.stream(sigils).mapToObj(Sigils.getRegistry()::getValue).forEach(h::forceAdd);
		if(h instanceof ItemSigilHolder) ((ItemSigilHolder)h).setGibberishSeed(gibberishSeed);
	}

	public static class Bulk extends BulkItemSyncMsg<SigilHolderSyncMsg>{
		public Bulk(SigilHolderSyncMsg[] messages){
			super(messages);
		}
		public Bulk(Iterable<SigilHolderSyncMsg> messages){
			super(messages);
		}
		public Bulk(PacketBuffer buf){
			super(buf);
		}

		@Override protected SigilHolderSyncMsg read(PacketBuffer buf){
			return new SigilHolderSyncMsg(buf);
		}
	}
}
