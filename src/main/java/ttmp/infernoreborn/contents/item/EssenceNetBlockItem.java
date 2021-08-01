package ttmp.infernoreborn.contents.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import ttmp.infernoreborn.contents.block.essencenet.EssenceNetCoreBlock;

public class EssenceNetBlockItem extends BlockItem implements EssenceNetCoreBlock.EssenceNetAcceptable{
	public static final String DEFAULT_NETWORK_ID_KEY = "NetworkID";

	public EssenceNetBlockItem(Block block, Properties properties){
		super(block, properties);
	}

	@Override public void setNetwork(ItemStack stack, int network){
		CompoundNBT blockEntityTag = stack.getOrCreateTagElement("BlockEntityTag");
		blockEntityTag.putInt(getNetworkIdKey(), network);
	}

	protected String getNetworkIdKey(){
		return DEFAULT_NETWORK_ID_KEY;
	}
}
