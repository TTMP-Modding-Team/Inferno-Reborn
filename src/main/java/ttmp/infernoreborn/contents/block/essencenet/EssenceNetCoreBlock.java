package ttmp.infernoreborn.contents.block.essencenet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.block.EssenceHolderBlock;
import ttmp.infernoreborn.contents.item.EssenceNetBlockItem;
import ttmp.infernoreborn.contents.tile.EssenceNetCoreTile;

import javax.annotation.Nullable;
import java.util.List;

// TODO make the item indestructible
public class EssenceNetCoreBlock extends Block{
	public EssenceNetCoreBlock(Properties properties){
		super(properties);
	}

	@SuppressWarnings("deprecation")
	@Override public ActionResultType use(BlockState pState, World pLevel, BlockPos pPos, PlayerEntity pPlayer, Hand pHand, BlockRayTraceResult pHit){
		ItemStack s = pPlayer.getItemInHand(pHand);
		if(!s.isEmpty()){
			Item i = s.getItem();
			if(i instanceof HasEssenceNet){
				if(pLevel.isClientSide) return ActionResultType.SUCCESS;
				TileEntity blockEntity = pLevel.getBlockEntity(pPos);
				if(blockEntity instanceof EssenceNetCoreTile){
					((HasEssenceNet)i).setNetwork(s, ((EssenceNetCoreTile)blockEntity).getOrAssignNetworkId());
					pLevel.playSound(null, pPos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, .1f, .9f);
				}
				return ActionResultType.CONSUME;
			}
		}
		return ActionResultType.PASS;
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable IBlockReader level, List<ITextComponent> text, ITooltipFlag flag){
		CompoundNBT blockEntityTag = stack.getTagElement("BlockEntityTag");
		int networkId = blockEntityTag!=null ? blockEntityTag.getInt(EssenceNetBlockItem.DEFAULT_NETWORK_ID_KEY) : 0;
		if(networkId!=0)
			text.add(new TranslationTextComponent("tooltip.infernoreborn.essence_network", networkId)
					.withStyle(TextFormatting.GOLD));
	}

	@Override public boolean hasTileEntity(BlockState state){
		return true;
	}
	@Override public TileEntity createTileEntity(BlockState state, IBlockReader world){
		return new EssenceNetCoreTile();
	}

	@SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context){
		return EssenceHolderBlock.SHAPE;
	}

	public interface HasEssenceNet{
		/**
		 * @param stack The item
		 * @return Network ID, 0 if item doesn't have any network attached
		 */
		int getNetwork(ItemStack stack);
		void setNetwork(ItemStack stack, int network);

		default void appendNetworkStatusText(ItemStack stack, List<ITextComponent> text){
			int networkId = getNetwork(stack);
			if(networkId==0) text.add(new TranslationTextComponent("tooltip.infernoreborn.essence_network.no_network")
					.withStyle(TextFormatting.DARK_RED));
			else text.add(new TranslationTextComponent("tooltip.infernoreborn.essence_network", networkId)
					.withStyle(TextFormatting.GOLD));
		}
	}
}
