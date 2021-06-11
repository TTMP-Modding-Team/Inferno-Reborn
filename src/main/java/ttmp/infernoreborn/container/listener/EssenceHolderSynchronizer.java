package ttmp.infernoreborn.container.listener;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import ttmp.infernoreborn.capability.EssenceHolder;
import ttmp.infernoreborn.network.BulkItemSyncMsg;
import ttmp.infernoreborn.network.EssenceHolderSyncMsg;

import javax.annotation.Nullable;
import java.util.List;

public class EssenceHolderSynchronizer extends ItemSynchronizer<EssenceHolderSyncMsg>{
	public EssenceHolderSynchronizer(ServerPlayerEntity player){
		super(player);
	}

	@SuppressWarnings("ConstantConditions")
	@Nullable @Override protected EssenceHolderSyncMsg getUpdateMessage(int slot, ItemStack stack){
		EssenceHolder h = stack.getCapability(EssenceHolder.capability).orElse(null);
		if(h==null) return null;
		return new EssenceHolderSyncMsg(slot, h);
	}

	@Override protected BulkItemSyncMsg<EssenceHolderSyncMsg> toBulkSyncMessage(List<EssenceHolderSyncMsg> messages){
		return new EssenceHolderSyncMsg.Bulk(messages);
	}
}
