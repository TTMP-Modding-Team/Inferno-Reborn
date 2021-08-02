package ttmp.infernoreborn.contents.item;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.block.essencenet.EssenceNetCoreBlock;

import javax.annotation.Nullable;
import java.util.List;

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

	@Override public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> text, ITooltipFlag flag){
		CompoundNBT blockEntityTag = stack.getTagElement("BlockEntityTag");
		int networkId = blockEntityTag!=null ? blockEntityTag.getInt(getNetworkIdKey()) : 0;
		if(networkId==0) text.add(new TranslationTextComponent("tooltip.infernoreborn.essence_network.no_network")
				.withStyle(TextFormatting.DARK_RED));
		else text.add(new TranslationTextComponent("tooltip.infernoreborn.essence_network", networkId)
				.withStyle(TextFormatting.GOLD));
		super.appendHoverText(stack, level, text, flag);
	}
}
