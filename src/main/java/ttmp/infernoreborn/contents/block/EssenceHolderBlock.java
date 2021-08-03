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
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.tile.EssenceHolderTile;

public class EssenceHolderBlock extends Block{
	private static final VoxelShape SHAPE = VoxelShapes.or(box(4, 2, 4, 12, 14, 12),
			box(3, 0, 3, 13, 2, 13),
			box(3, 2, 3, 6, 3, 6),
			box(10, 2, 3, 13, 3, 6),
			box(10, 2, 10, 13, 3, 13),
			box(3, 2, 10, 6, 3, 13)).optimize();

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

	@SuppressWarnings("deprecation")
	@Override public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_){
		return SHAPE;
	}
}
