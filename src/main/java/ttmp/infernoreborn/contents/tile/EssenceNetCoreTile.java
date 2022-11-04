package ttmp.infernoreborn.contents.tile;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import ttmp.infernoreborn.api.essence.EssenceNetProvider;
import ttmp.infernoreborn.contents.ModTileEntities;
import ttmp.infernoreborn.contents.item.EssenceNetBlockItem;

public class EssenceNetCoreTile extends TileEntity{
	private int networkId;

	public EssenceNetCoreTile(){
		super(ModTileEntities.ESSENCE_NET_CORE.get());
	}

	public int getNetworkId(){
		return networkId;
	}
	public void setNetworkId(int networkId){
		this.networkId = networkId;
	}

	public int getOrAssignNetworkId(){
		if(networkId==0) networkId = EssenceNetProvider.getInstance().assignNew();
		return networkId;
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
