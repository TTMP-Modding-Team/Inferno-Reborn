package ttmp.infernoreborn.contents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.tile.EssenceHolderTile;

public class EssenceHolderBlock extends Block{
	public EssenceHolderBlock(Properties properties){
		super(properties);
	}

	@SuppressWarnings("deprecation") @Override public ActionResultType use(BlockState state,
	                                                                       World level,
	                                                                       BlockPos pos,
	                                                                       PlayerEntity player,
	                                                                       Hand hand,
	                                                                       BlockRayTraceResult hit){
		if(level.isClientSide) return ActionResultType.SUCCESS;
		TileEntity te = level.getBlockEntity(pos);
		if(te instanceof EssenceHolderTile) player.openMenu((INamedContainerProvider)te);
		return ActionResultType.CONSUME;
	}

	@Override public boolean hasTileEntity(BlockState state){
		return true;
	}
	@Override public TileEntity createTileEntity(BlockState state, IBlockReader world){
		return new EssenceHolderTile();
	}
}
