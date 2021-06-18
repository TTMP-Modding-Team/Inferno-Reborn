package ttmp.infernoreborn.contents.container.listener;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.network.PacketDistributor;
import ttmp.infernoreborn.network.BulkItemSyncMsg;
import ttmp.infernoreborn.network.ItemSyncMsg;
import ttmp.infernoreborn.network.ModNet;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ItemSynchronizer<M extends ItemSyncMsg> implements IContainerListener{
	private final ServerPlayerEntity player;

	public ItemSynchronizer(ServerPlayerEntity player){
		this.player = Objects.requireNonNull(player);
	}

	@Override public void refreshContainer(Container container, NonNullList<ItemStack> itemStacks){
		List<M> list = new ArrayList<>();
		for(int i = 0; i<itemStacks.size(); i++){
			M m = getUpdateMessage(i, itemStacks.get(i));
			if(m!=null) list.add(m);
		}
		if(!list.isEmpty()){
			//InfernoReborn.LOGGER.debug("Sending {}", list.stream().map(ItemSyncMsg::toString).collect(Collectors.joining(", ")));
			ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), toBulkSyncMessage(list));
		}
	}
	@Override public void slotChanged(Container container, int slot, ItemStack stack){
		M m = getUpdateMessage(slot, stack);
		if(m!=null){
			//InfernoReborn.LOGGER.debug("Sending {}", m);
			ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), m);
		}
	}
	@Override public void setContainerData(Container container, int var, int val){}

	@Nullable protected abstract M getUpdateMessage(int slot, ItemStack stack);
	protected abstract BulkItemSyncMsg<M> toBulkSyncMessage(List<M> messages);
}
