package ttmp.infernoreborn.container.listener;

import net.minecraft.item.ItemStack;
import ttmp.infernoreborn.network.BulkItemSyncMsg;
import ttmp.infernoreborn.network.EssenceHolderSyncMsg;

import javax.annotation.Nullable;
import java.util.List;

public class EssenceHolderSynchronizer extends ItemSynchronizer<EssenceHolderSyncMsg>{
	@Nullable @Override protected EssenceHolderSyncMsg getUpdateMessage(int slot, ItemStack stack){
		return null;
	}
	@Override protected BulkItemSyncMsg<EssenceHolderSyncMsg> toBulkSyncMessage(List<EssenceHolderSyncMsg> messages){
		return null;
	}
}
