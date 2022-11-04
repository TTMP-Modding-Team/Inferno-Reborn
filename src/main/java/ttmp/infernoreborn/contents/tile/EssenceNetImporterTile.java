package ttmp.infernoreborn.contents.tile;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import ttmp.infernoreborn.api.essence.EssenceHolder;
import ttmp.infernoreborn.api.essence.EssenceNetProvider;
import ttmp.infernoreborn.contents.ModTileEntities;
import ttmp.infernoreborn.contents.block.ModProperties;
import ttmp.infernoreborn.contents.item.EssenceNetBlockItem;
import ttmp.infernoreborn.inventory.EssenceHolderItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EssenceNetImporterTile extends TileEntity{
	private int networkId;

	public EssenceNetImporterTile(){
		super(ModTileEntities.ESSENCE_NET_IMPORTER.get());
	}

	public int getNetworkId(){
		return networkId;
	}
	public void setNetworkId(int networkId){
		if(this.networkId!=networkId){
			this.networkId = networkId;
			updateBlock();
			if(itemHandlerCache!=null){
				this.itemHandlerCache.invalidate();
				this.itemHandlerCache = null;
			}
		}
	}

	public void updateBlock(){
		if(level==null||level.isClientSide()) return;
		BlockState state = this.level.getBlockState(getBlockPos());
		boolean noNetwork = state.getValue(ModProperties.NO_NETWORK);
		if(noNetwork!=(this.networkId==0))
			level.setBlock(getBlockPos(), state.setValue(ModProperties.NO_NETWORK, !noNetwork), 3);
	}

	@Nullable private LazyOptional<IItemHandler> itemHandlerCache;

	@Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		if(cap==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			if(itemHandlerCache==null){
				EssenceHolder h = EssenceNetProvider.getInstance().get(networkId);
				itemHandlerCache = h!=null ? LazyOptional.of(() -> new EssenceHolderItemHandler(){
					@Override public EssenceHolder getEssenceHolder(){
						return h;
					}

					@Nonnull @Override public ItemStack extractItem(int slot, int amount, boolean simulate){
						return ItemStack.EMPTY;
					}
				}) : LazyOptional.empty();
			}
			return itemHandlerCache.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override protected void invalidateCaps(){
		super.invalidateCaps();
		if(itemHandlerCache!=null){
			this.itemHandlerCache.invalidate();
			this.itemHandlerCache = null;
		}
	}

	@Override public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		setNetworkId(nbt.getInt(EssenceNetBlockItem.DEFAULT_NETWORK_ID_KEY));
	}
	@Override public CompoundNBT save(CompoundNBT nbt){
		nbt.putInt(EssenceNetBlockItem.DEFAULT_NETWORK_ID_KEY, networkId);
		return super.save(nbt);
	}
}
