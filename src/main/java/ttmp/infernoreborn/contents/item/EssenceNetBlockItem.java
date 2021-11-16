package ttmp.infernoreborn.contents.item;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.block.essencenet.EssenceNetCoreBlock;

import javax.annotation.Nullable;
import java.util.List;

public class EssenceNetBlockItem extends BlockItem implements EssenceNetCoreBlock.HasEssenceNet{
	public static final String DEFAULT_NETWORK_ID_KEY = "NetworkID";

	public EssenceNetBlockItem(Block block, Properties properties){
		super(block, properties);
	}

	@Override public int getNetwork(ItemStack stack){
		CompoundNBT blockEntityTag = stack.getTagElement("BlockEntityTag");
		return blockEntityTag!=null ? blockEntityTag.getInt(getNetworkIdKey()) : 0;
	}
	@Override public void setNetwork(ItemStack stack, int network){
		stack.getOrCreateTagElement("BlockEntityTag").putInt(getNetworkIdKey(), network);
	}

	protected String getNetworkIdKey(){
		return DEFAULT_NETWORK_ID_KEY;
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> text, ITooltipFlag flag){
		appendNetworkStatusText(stack, text);
		super.appendHoverText(stack, level, text, flag);
	}
}
