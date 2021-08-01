package ttmp.infernoreborn.capability;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.util.EssenceHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface EssenceNetProvider{
	EssenceNetProvider EMPTY = new EssenceNetProvider(){
		@Override public int assignNew(){
			return 0;
		}
		@Nullable @Override public EssenceHolder get(int id){
			return null;
		}
	};

	/**
	 * @return Assign new shit. Return 0 if can't.
	 */
	int assignNew();
	@Nullable EssenceHolder get(int id);

	static EssenceNetProvider getInstance(){
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if(server==null) return EMPTY;
		return server.overworld().getCapability(Caps.essenceNetProvider).orElse(EMPTY);
	}

	final class Impl implements EssenceNetProvider, ICapabilitySerializable<ListNBT> {
		private static final int NET_SIZE_LIMIT = 100000000;

		private final Int2ObjectMap<EssenceHolder> essenceHolders = new Int2ObjectOpenHashMap<>();


		@Override public int assignNew(){
			if(essenceHolders.size()>=NET_SIZE_LIMIT) return 0;
			int size = essenceHolders.size()+1;
			essenceHolders.put(size, new EssenceHolder());
			InfernoReborn.LOGGER.info("New essence holder network assigned: #{}", size);
			return size;
		}

		@Override @Nullable public EssenceHolder get(int id){
			return id==0 ? null : essenceHolders.get(id);
		}

		@Nullable private LazyOptional<EssenceNetProvider> self;

		@Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
			if(cap==Caps.essenceNetProvider){
				if(self==null) self = LazyOptional.of(() -> this);
				return self.cast();
			}else return LazyOptional.empty();
		}

		@Override public ListNBT serializeNBT(){
			ListNBT list = new ListNBT();
			for(Int2ObjectMap.Entry<EssenceHolder> e : essenceHolders.int2ObjectEntrySet()){
				CompoundNBT n = new CompoundNBT();
				n.putInt("i", e.getIntKey());
				n.put("e", e.getValue().serializeNBT());
				list.add(n);
			}
			return list;
		}

		@Override public void deserializeNBT(ListNBT list){
			for(int i = 0; i<list.size(); i++){
				CompoundNBT n = list.getCompound(i);
				EssenceHolder h = new EssenceHolder();
				h.deserializeNBT(n.getCompound("e"));
				essenceHolders.put(n.getInt("i"), h);
			}
		}
	}
}
