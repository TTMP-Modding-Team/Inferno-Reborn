package ttmp.infernoreborn.contents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import ttmp.infernoreborn.contents.ModBlocks;
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

	@Override public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit){
		if(state.getValue(AUTOMATED)) return ActionResultType.PASS;
		ItemStack stack = player.getItemInHand(hand);
		if(stack.isEmpty()){
			CrucibleTile crucible = te(level, pos);
			if(crucible!=null) crucible.empty(player, player.isCrouching());
			return ActionResultType.sidedSuccess(level.isClientSide);
		}else if(stack.getItem()==Items.STICK){
			CrucibleTile crucible = te(level, pos);
			if(crucible!=null&&crucible.stirManually()){
				float waterLevel = crucible.getFluidTank().getFluidAmount()/(float)crucible.getFluidTank().getCapacity();
				level.playSound(player, pos,
						SoundEvents.BOAT_PADDLE_WATER, SoundCategory.PLAYERS, 1,
						0.8f+0.4f*level.random.nextFloat()*(2-waterLevel));
			}
			return ActionResultType.sidedSuccess(level.isClientSide);
		}else{
			CrucibleTile crucible = te(level, pos);
			if(crucible!=null&&FluidUtil.interactWithFluidHandler(player, hand, crucible.getFluidHandler()))
				return ActionResultType.sidedSuccess(level.isClientSide);
		}
		return ActionResultType.PASS;
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
		return SHAPE;
	}

	@Override public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld level, BlockPos currentPos, BlockPos facingPos){
		if(facing==Direction.DOWN&&!level.isClientSide()){
			CrucibleTile te = te(level, currentPos);
			if(te!=null) te.markUpdateHeat();
		}
		return state;
	}

	@Override public void handleRain(World level, BlockPos pos){
		if(level.random.nextInt(20)==1){
			float temp = level.getBiome(pos).getTemperature(pos);
			if(!(temp<0.15f)){
				CrucibleTile te = te(level, pos);
				if(te!=null&&te.getFluidTank().getCapacity()>te.getFluidTank().getFluidAmount())
					te.getFluidTank().fill(new FluidStack(Fluids.WATER, 100), FluidAction.EXECUTE);
			}
		}
	}

	@SuppressWarnings("deprecation") @Override public void onRemove(BlockState oldState, World level, BlockPos pos, BlockState newState, boolean isMoving){
		if(!oldState.is(newState.getBlock())){
			CrucibleTile te = te(level, pos);
			if(te!=null) te.dropContents();
		}
		super.onRemove(oldState, level, pos, newState, isMoving);
	}

	@Nullable protected static CrucibleTile te(IWorldReader level, BlockPos pos){
		TileEntity te = level.getBlockEntity(pos);
		return te instanceof CrucibleTile ? (CrucibleTile)te : null;
	}
}
