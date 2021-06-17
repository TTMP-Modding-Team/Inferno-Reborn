package ttmp.infernoreborn.container.listener;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import ttmp.infernoreborn.capability.Caps;
import ttmp.infernoreborn.network.BulkItemSyncMsg;
import ttmp.infernoreborn.network.SigilHolderSyncMsg;
import ttmp.infernoreborn.sigil.holder.SigilHolder;

import javax.annotation.Nullable;
import java.util.List;

public class SigilHolderSynchronizer extends ItemSynchronizer<SigilHolderSyncMsg>{
	public SigilHolderSynchronizer(ServerPlayerEntity player){
		super(player);
	}

	@SuppressWarnings("ConstantConditions")
	@Nullable @Override protected SigilHolderSyncMsg getUpdateMessage(int slot, ItemStack stack){
		SigilHolder h = stack.getCapability(Caps.sigilHolder).orElse(null);
		if(h==null) return null;
		return new SigilHolderSyncMsg(slot, h);
	}

	@Override protected BulkItemSyncMsg<SigilHolderSyncMsg> toBulkSyncMessage(List<SigilHolderSyncMsg> messages){
		return new SigilHolderSyncMsg.Bulk(messages);
	}
}
