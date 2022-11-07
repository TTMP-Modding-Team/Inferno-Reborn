package ttmp.infernoreborn.contents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.tile.crucible.Crucible;
import ttmp.infernoreborn.contents.tile.crucible.CrucibleAutomationUnitTile;
import ttmp.infernoreborn.contents.tile.crucible.CrucibleTile;

import static ttmp.infernoreborn.contents.block.ModProperties.*;

@SuppressWarnings("deprecation")
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

	@Override public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit){
		if(player.isCrouching()&&hit.getDirection()!=Direction.UP&&hit.getDirection()!=Direction.DOWN){
			BlockState belowState = level.getBlockState(pos.below());
			if(belowState.hasProperty(AUTOMATED)&&belowState.getValue(AUTOMATED)){
				if(!level.isClientSide){
					TileEntity te = level.getBlockEntity(pos);
					if(te instanceof CrucibleAutomationUnitTile)
						playOutputToggleSound(level, pos,
								((CrucibleAutomationUnitTile)te).toggleSetOutput(hit.getDirection()));
				}
				return ActionResultType.sidedSuccess(level.isClientSide);
			}
		}
		return ActionResultType.PASS;
	}

	@Override public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld level, BlockPos currentPos, BlockPos facingPos){
		if(!level.isClientSide()&&facing==Direction.DOWN){
			CrucibleTile crucible = Crucible.crucible(level, facingPos);
			if(crucible==null) for(Direction d : Direction.Plane.HORIZONTAL)
				state = state.setValue(ModProperties.outputProperty(d), false);
		}
		return state;
	}

	@Override public boolean hasTileEntity(BlockState state){
		return true;
	}
	@Override public TileEntity createTileEntity(BlockState state, IBlockReader world){
		return new CrucibleAutomationUnitTile();
	}

	@Override public VoxelShape getShape(BlockState pState, IBlockReader pLevel, BlockPos pPos, ISelectionContext pContext){
		return SHAPE;
	}

	@Override protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> b){
		b.add(MODULE_U, MODULE_N, MODULE_S, MODULE_W, MODULE_E, OUT_N, OUT_S, OUT_W, OUT_E);
	}

	public static void playOutputToggleSound(IWorld level, BlockPos pos, boolean open){
		level.playSound(null, pos, open ? SoundEvents.IRON_DOOR_OPEN : SoundEvents.IRON_DOOR_CLOSE,
				SoundCategory.PLAYERS, 1, level.getRandom().nextFloat()*0.1f+1.4f);
	}
}
