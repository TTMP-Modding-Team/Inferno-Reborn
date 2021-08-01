package ttmp.infernoreborn.contents.tile;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import ttmp.infernoreborn.capability.EssenceNetProvider;
import ttmp.infernoreborn.contents.ModTileEntities;
import ttmp.infernoreborn.contents.item.EssenceNetBlockItem;
import ttmp.infernoreborn.util.EssenceHolder;
import ttmp.infernoreborn.util.EssenceSize;
import ttmp.infernoreborn.util.EssenceType;

import javax.annotation.Nullable;

// TODO turn off with redstone, set template type with using essence item, acceleration rune maybe
public class EssenceNetExporterTile extends TileEntity implements ITickableTileEntity{
	private int networkId;
	@Nullable private EssenceSize size;
	@Nullable private EssenceType type;
	private boolean accelerated;

	@Nullable private EssenceHolder essenceHolderCache;

	public EssenceNetExporterTile(){
		super(ModTileEntities.ESSENCE_NET_EXPORTER.get());
	}

	private boolean isWorkPeriod(){
		return level!=null&&level.getGameTime()%(accelerated ? 2 : 20)==0;
	}

	public int getNetworkId(){
		return networkId;
	}
	public void setNetworkId(int networkId){
		if(this.networkId!=networkId){
			this.networkId = networkId;
			this.essenceHolderCache = null;
		}
	}

	@Nullable public EssenceSize getTemplateSize(){
		return size;
	}
	@Nullable public EssenceType getTemplateType(){
		return type;
	}
	public void setTemplate(@Nullable EssenceSize size, @Nullable EssenceType type){
		this.size = size;
		this.type = type;
	}
	public boolean isAccelerated(){
		return accelerated;
	}
	public void setAccelerated(boolean accelerated){
		this.accelerated = accelerated;
	}

	@Override public void tick(){
		if(level==null||level.isClientSide) return;
		if(size==null||type==null) return;
		if(!isWorkPeriod()) return;
		if(essenceHolderCache==null){
			essenceHolderCache = EssenceNetProvider.getInstance().get(networkId);
			if(essenceHolderCache==null) return;
		}
		int maxExtract = essenceHolderCache.getEssence(type)/size.getCompressionRate();
		if(maxExtract<=0) return;
		TileEntity be = level.getBlockEntity(this.getBlockPos().relative(this.getBlockState().getValue(BlockStateProperties.FACING)));
		if(be==null) return;
		be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
			ItemStack s = new ItemStack(type.getItem(size), maxExtract);
			for(int i = 0; i<h.getSlots(); i++){
				s = h.insertItem(i, s, false);
				if(s.isEmpty()) break;
			}
			int extracted = maxExtract-s.getCount();
			if(extracted>0) essenceHolderCache.extractEssence(type, extracted*size.getCompressionRate(), false);
		});
	}

	@Override public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		setNetworkId(nbt.getInt(EssenceNetBlockItem.DEFAULT_NETWORK_ID_KEY));
		size = nbt.contains("Size", Constants.NBT.TAG_BYTE) ? EssenceSize.of(nbt.getByte("Size")) : null;
		type = nbt.contains("Type", Constants.NBT.TAG_STRING) ? EssenceType.of(nbt.getString("Type")) : null;
		accelerated = nbt.getBoolean("Accelerated");
	}
	@Override public CompoundNBT save(CompoundNBT nbt){
		nbt.putInt(EssenceNetBlockItem.DEFAULT_NETWORK_ID_KEY, networkId);
		if(size!=null) nbt.putByte("Size", (byte)size.ordinal());
		if(type!=null) nbt.putString("Type", type.id);
		if(accelerated) nbt.putBoolean("Accelerated", true);
		return super.save(nbt);
	}
}
