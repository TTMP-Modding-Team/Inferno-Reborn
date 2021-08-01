package ttmp.infernoreborn.contents.block.essencenet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.tile.EssenceNetCoreTile;

public class EssenceNetCoreBlock extends Block{
	public EssenceNetCoreBlock(Properties properties){
		super(properties);
	}

	@SuppressWarnings("deprecation")
	@Override public ActionResultType use(BlockState pState, World pLevel, BlockPos pPos, PlayerEntity pPlayer, Hand pHand, BlockRayTraceResult pHit){
		ItemStack s = pPlayer.getItemInHand(pHand);
		if(!s.isEmpty()){
			Item i = s.getItem();
			if(i instanceof EssenceNetAcceptable){
				if(pLevel.isClientSide) return ActionResultType.SUCCESS;
				TileEntity blockEntity = pLevel.getBlockEntity(pPos);
				if(blockEntity instanceof EssenceNetCoreTile){
					((EssenceNetAcceptable)i).setNetwork(s, ((EssenceNetCoreTile)blockEntity).getOrAssignNetworkId());
					pLevel.playSound(null, pPos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, .1f, .9f);
				}
				return ActionResultType.CONSUME;
			}
		}
		return ActionResultType.PASS;
	}

	@Override public boolean hasTileEntity(BlockState state){
		return true;
	}
	@Override public TileEntity createTileEntity(BlockState state, IBlockReader world){
		return new EssenceNetCoreTile();
	}

	public interface EssenceNetAcceptable{
		void setNetwork(ItemStack stack, int network);
	}
}
