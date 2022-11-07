package ttmp.infernoreborn.contents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import ttmp.infernoreborn.contents.ModBlocks;
import ttmp.infernoreborn.contents.tile.crucible.Crucible;
import ttmp.infernoreborn.contents.tile.crucible.CrucibleAutomationUnitTile;
import ttmp.infernoreborn.contents.tile.crucible.CrucibleTile;
import ttmp.infernoreborn.util.ReplaceBlockContext;

import javax.annotation.Nullable;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;
import static net.minecraft.state.properties.BlockStateProperties.LIT;
import static ttmp.infernoreborn.contents.block.ModProperties.AUTOMATED;

@SuppressWarnings("deprecation")
public class CrucibleBlock extends Block{
	private static final VoxelShape SHAPE = VoxelShapes.or(
			box(1, 0, 1, 15, 2, 15),
			box(1, 2, 1, 3, 12, 15),
			box(13, 2, 1, 15, 12, 15),
			box(3, 2, 1, 13, 12, 3),
			box(3, 2, 13, 13, 12, 15)
	).optimize();
	private static final VoxelShape AUTOMATED_SHAPE = box(1, 0, 1, 15, 16, 15);

	public CrucibleBlock(Properties p){
		super(p);
		this.registerDefaultState(getStateDefinition().any().setValue(AUTOMATED, false));
	}

	@Override public BlockState getStateForPlacement(BlockItemUseContext ctx){
		if(ctx instanceof ReplaceBlockContext){
			BlockState prev = ctx.getLevel().getBlockState(ctx.getClickedPos());
			return ModBlocks.CRUCIBLE_CAMPFIRE.get().defaultBlockState()
					.setValue(HORIZONTAL_FACING, prev.hasProperty(HORIZONTAL_FACING) ?
							prev.getValue(HORIZONTAL_FACING) : ctx.getHorizontalDirection().getOpposite())
					.setValue(LIT, !prev.hasProperty(LIT)||prev.getValue(LIT));
		}
		return this.defaultBlockState();
	}

	@Override public void setPlacedBy(World level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack){
		updateAutomated(state, level, pos);
	}

	@Override public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit){
		if(state.getValue(AUTOMATED)){
			if(player.isCrouching()){
				Direction clickedCrucibleSide = getClickedCrucibleSide(hit);
				if(clickedCrucibleSide!=Direction.UP&&clickedCrucibleSide!=Direction.DOWN){
					if(!level.isClientSide){
						TileEntity te = level.getBlockEntity(pos.above());
						if(te instanceof CrucibleAutomationUnitTile)
							CrucibleAutomationUnitBlock.playOutputToggleSound(level, pos,
									((CrucibleAutomationUnitTile)te).toggleSetOutput(clickedCrucibleSide));
					}
					return ActionResultType.sidedSuccess(level.isClientSide);
				}
			}
			return ActionResultType.PASS;
		}
		ItemStack stack = player.getItemInHand(hand);
		if(stack.isEmpty()){
			CrucibleTile crucible = Crucible.crucible(level, pos);
			if(crucible!=null) crucible.empty(player, player.isCrouching());
			return ActionResultType.sidedSuccess(level.isClientSide);
		}else if(stack.getItem()==Items.STICK){
			CrucibleTile crucible = Crucible.crucible(level, pos);
			if(crucible!=null&&crucible.stirManually()){
				float waterLevel = (float)crucible.getMaxFluidFillRate();
				if(waterLevel>0) level.playSound(player, pos,
						SoundEvents.BOAT_PADDLE_WATER, SoundCategory.PLAYERS, 1,
						0.8f+0.4f*level.random.nextFloat()*(2-waterLevel));
				else level.playSound(player, pos,
						SoundEvents.BOAT_PADDLE_LAND, SoundCategory.PLAYERS, 1,
						0.8f+0.4f*level.random.nextFloat());
			}
			return ActionResultType.sidedSuccess(level.isClientSide);
		}else if(FluidUtil.getFluidHandler(stack).isPresent()){
			CrucibleTile crucible = Crucible.crucible(level, pos);
			if(crucible!=null) FluidUtil.interactWithFluidHandler(player, hand, crucible.getFluidHandler());
			return ActionResultType.sidedSuccess(level.isClientSide);
		}
		return ActionResultType.PASS;
	}

	public Direction getClickedCrucibleSide(BlockRayTraceResult hit){
		Vector3d location = hit.getLocation();
		double x = location.x-hit.getBlockPos().getX();
		if(gt(1/16.0, x)) return Direction.EAST;
		if(gt(x, 15/16.0)) return Direction.WEST;
		double z = location.z-hit.getBlockPos().getZ();
		if(gt(1/16.0, z)) return Direction.NORTH;
		if(gt(z, 15/16.0)) return Direction.SOUTH;
		if(hit.getDirection()!=Direction.DOWN&&
				gt(x, 1/16.0)&&gt(15/16.0, x)&&
				gt(z, 1/16.0)&&gt(15/16.0, z)) return Direction.UP;
		return hit.getDirection();
	}

	/**
	 * @return whether {@code a} is greater than {@code b} above epsilon
	 */
	protected static boolean gt(double a, double b){
		return a-b>Double.MIN_VALUE;
	}

	@Override protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> b){
		b.add(AUTOMATED);
	}

	@Override public boolean hasTileEntity(BlockState state){
		return true;
	}
	@Override public TileEntity createTileEntity(BlockState state, IBlockReader world){
		return new CrucibleTile();
	}

	@Override public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx){
		return state.getValue(AUTOMATED) ? AUTOMATED_SHAPE : SHAPE;
	}

	@Override public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld level, BlockPos currentPos, BlockPos facingPos){
		if(!level.isClientSide()){
			if(facing==Direction.DOWN){
				CrucibleTile crucible = Crucible.crucible(level, currentPos);
				if(crucible!=null) crucible.markUpdateHeat();
			}else if(facing==Direction.UP) updateAutomated(state, level, currentPos, facingPos);
		}
		return state;
	}

	private void updateAutomated(BlockState state, IWorld level, BlockPos pos){
		updateAutomated(state, level, pos, pos.above());
	}
	private void updateAutomated(BlockState state, IWorld level, BlockPos pos, BlockPos up){
		TileEntity te = level.getBlockEntity(up);
		boolean automated = te instanceof CrucibleAutomationUnitTile;
		if(state.getValue(AUTOMATED)!=automated)
			level.setBlock(pos, state.setValue(AUTOMATED, automated), 3);
	}

	@Override public void handleRain(World level, BlockPos pos){
		if(level.random.nextInt(20)==1){
			if(!(level.getBiome(pos).getTemperature(pos)<0.15f)){
				CrucibleTile te = Crucible.crucible(level, pos);
				if(te!=null) te.getFluidHandler().fill(new FluidStack(Fluids.WATER, 100), FluidAction.EXECUTE);
			}
		}
	}

	@SuppressWarnings("deprecation") @Override public void onRemove(BlockState oldState, World level, BlockPos pos, BlockState newState, boolean isMoving){
		if(!oldState.is(newState.getBlock())){
			CrucibleTile te = Crucible.crucible(level, pos);
			if(te!=null) te.dropContents();
		}
		super.onRemove(oldState, level, pos, newState, isMoving);
	}
}
