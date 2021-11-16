package ttmp.infernoreborn.contents.tile;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import ttmp.infernoreborn.capability.EssenceNetProvider;
import ttmp.infernoreborn.contents.ModTileEntities;
import ttmp.infernoreborn.contents.block.ModProperties;
import ttmp.infernoreborn.contents.item.EssenceNetBlockItem;
import ttmp.infernoreborn.util.EssenceHolder;
import ttmp.infernoreborn.util.EssenceSize;
import ttmp.infernoreborn.util.EssenceType;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

// TODO turn off with redstone
public class EssenceNetExporterTile extends TileEntity implements ITickableTileEntity{
	private static final Random RNG = new Random();

	private int networkId;
	@Nullable private Template template;
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
			updateBlock();
		}
	}
	@Nullable public Template getTemplate(){
		return template;
	}
	public void setTemplate(@Nullable Template template){
		this.template = template;
		updateBlock();
	}
	public boolean isAccelerated(){
		return accelerated;
	}
	public void setAccelerated(boolean accelerated){
		this.accelerated = accelerated;
		updateBlock();
	}

	@Override public void tick(){
		if(level==null||level.isClientSide) return;
		if(!isWorkPeriod()) return;
		if(essenceHolderCache==null){
			essenceHolderCache = EssenceNetProvider.getInstance().get(networkId);
			if(essenceHolderCache==null) return;
		}
		EssenceType type;
		EssenceSize size;
		if(template!=null){
			type = template.type;
			size = template.size;
		}else{
			type = EssenceType.values()[RNG.nextInt(EssenceType.values().length)]; // TODO improve
			size = EssenceSize.ESSENCE;
		}
		int maxExtract = Math.min(64, essenceHolderCache.getEssence(type)/size.getCompressionRate());
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

	public void updateBlock(){
		if(level==null||level.isClientSide()) return;
		BlockState state = this.level.getBlockState(getBlockPos());
		boolean stateUpdated = false;
		if(state.getValue(ModProperties.NO_NETWORK)!=(this.networkId==0)){
			state = state.setValue(ModProperties.NO_NETWORK, this.networkId==0);
			stateUpdated = true;
		}
		if(state.getValue(ModProperties.HAS_FILTER)!=(this.template!=null)){
			state = state.setValue(ModProperties.NO_NETWORK, this.template!=null);
			stateUpdated = true;
		}
		if(state.getValue(ModProperties.ACCELERATED)!=(this.accelerated)){
			state = state.setValue(ModProperties.NO_NETWORK, this.accelerated);
			stateUpdated = true;
		}
		if(stateUpdated) level.setBlock(getBlockPos(), state, 3);
	}

	@Override public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		setNetworkId(nbt.getInt(EssenceNetBlockItem.DEFAULT_NETWORK_ID_KEY));
		template = Template.load(nbt);
		accelerated = nbt.getBoolean("Accelerated");
	}
	@Override public CompoundNBT save(CompoundNBT nbt){
		nbt.putInt(EssenceNetBlockItem.DEFAULT_NETWORK_ID_KEY, networkId);
		if(template!=null) template.save(nbt);
		if(accelerated) nbt.putBoolean("Accelerated", true);
		return super.save(nbt);
	}

	public static final class Template{
		@Nullable public static Template load(CompoundNBT nbt){
			return nbt.contains("Size", Constants.NBT.TAG_BYTE)&&nbt.contains("Type", Constants.NBT.TAG_STRING) ?
					new Template(EssenceType.of(nbt.getString("Type")), EssenceSize.of(nbt.getByte("Size"))) :
					null;
		}
		@Nullable public static Template fromItem(Item item){
			for(EssenceType type : EssenceType.values())
				for(EssenceSize size : EssenceSize.values())
					if(type.getItem(size)==item)
						return new Template(type, size);
			return null;
		}

		public final EssenceType type;
		public final EssenceSize size;

		public Template(EssenceType type, EssenceSize size){
			this.type = type;
			this.size = size;
		}

		@Override public boolean equals(Object o){
			if(this==o) return true;
			if(o==null||getClass()!=o.getClass()) return false;
			Template template = (Template)o;
			return type==template.type&&size==template.size;
		}
		@Override public int hashCode(){
			return Objects.hash(type, size);
		}

		public void save(CompoundNBT nbt){
			nbt.putString("Type", type.id);
			nbt.putByte("Size", (byte)size.ordinal());
		}

		@Override public String toString(){
			return "Template{"+
					"type="+type+
					", size="+size+
					'}';
		}
	}
}
