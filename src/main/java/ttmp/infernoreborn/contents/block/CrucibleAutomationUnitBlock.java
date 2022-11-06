package ttmp.infernoreborn.contents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import ttmp.infernoreborn.contents.tile.crucible.CrucibleAutomationUnitTile;

import static ttmp.infernoreborn.contents.block.ModProperties.*;

public class CrucibleAutomationUnitBlock extends Block{
	private static final VoxelShape SHAPE = VoxelShapes.or(
			box(1, 0, 1, 15, 4, 15),
			box(4, 11, 4, 12, 14, 12),
			box(3.99, 4, 1, 12.01, 11, 4),
			box(12, 4, 3.99, 15, 11, 12.01),
			box(1, 4, 3.99, 4, 11, 12.01),
			box(3.99, 4, 12, 12.01, 11, 15)
	).optimize();

	public CrucibleAutomationUnitBlock(Properties p){
		super(p);
		this.registerDefaultState(getStateDefinition().any()
				.setValue(MODULE_U, false)
				.setValue(MODULE_N, false)
				.setValue(MODULE_S, false)
				.setValue(MODULE_W, false)
				.setValue(MODULE_E, false)
				.setValue(OUT_N, false)
				.setValue(OUT_S, false)
				.setValue(OUT_W, false)
				.setValue(OUT_E, false));
	}

	@Override public boolean hasTileEntity(BlockState state){
		return true;
	}
	@Override public TileEntity createTileEntity(BlockState state, IBlockReader world){
		return new CrucibleAutomationUnitTile();
	}

	@SuppressWarnings("deprecation")
	@Override public VoxelShape getShape(BlockState pState, IBlockReader pLevel, BlockPos pPos, ISelectionContext pContext){
		return SHAPE;
	}

	@Override protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> b){
		b.add(MODULE_U, MODULE_N, MODULE_S, MODULE_W, MODULE_E, OUT_N, OUT_S, OUT_W, OUT_E);
	}
}
