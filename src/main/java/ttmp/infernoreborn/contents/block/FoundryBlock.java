package ttmp.infernoreborn.contents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.ModBlocks;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.tile.FoundryProxyTile;
import ttmp.infernoreborn.contents.tile.FoundryTile;

import javax.annotation.Nullable;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class FoundryBlock extends Block{
	public static ProxyBlock[] proxyBlocks(){
		return new ProxyBlock[]{
				ModBlocks.FOUNDRY_FIREBOX.get(),
				ModBlocks.FOUNDRY_GRATE_1.get(),
				ModBlocks.FOUNDRY_GRATE_2.get(),
				ModBlocks.FOUNDRY_MOLD_1.get(),
				ModBlocks.FOUNDRY_MOLD_2.get()
		};
	}

	public FoundryBlock(Properties properties){
		super(properties);
	}

	@Override protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> b){
		b.add(HORIZONTAL_FACING);
	}

	@Nullable @Override public BlockState getStateForPlacement(BlockItemUseContext ctx){
		Direction facing = ctx.getHorizontalDirection().getOpposite();
		BlockPos.Mutable mpos = new BlockPos.Mutable();
		for(ProxyBlock proxyBlock : proxyBlocks()){
			moveFromOrigin(mpos.set(ctx.getClickedPos()), proxyBlock.defaultBlockState().setValue(HORIZONTAL_FACING, facing));
			if(mpos.getY()>=255||!ctx.getLevel().getBlockState(mpos).canBeReplaced(ctx)) return null;
		}
		return this.defaultBlockState().setValue(HORIZONTAL_FACING, facing);
	}

	@Override public void setPlacedBy(World level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack){
		Direction facing = state.getValue(HORIZONTAL_FACING);
		BlockPos.Mutable mpos = new BlockPos.Mutable();
		for(ProxyBlock proxyBlock : proxyBlocks()){
			BlockState s2 = proxyBlock.defaultBlockState().setValue(HORIZONTAL_FACING, facing);
			level.setBlock(moveFromOrigin(mpos.set(pos), s2), s2, 3);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result){
		if(world.isClientSide) return ActionResultType.SUCCESS;
		TileEntity te = world.getBlockEntity(pos);
		if(te instanceof FoundryTile){
			FoundryTile foundry = (FoundryTile)te;
			player.openMenu(foundry);
		}
		return ActionResultType.CONSUME;
	}

	@SuppressWarnings("deprecation") @Override public BlockState updateShape(BlockState state, Direction dir, BlockState stateAt, IWorld level, BlockPos pos, BlockPos posAt){
		Direction facing = state.getValue(HORIZONTAL_FACING);
		BlockPos.Mutable mpos = new BlockPos.Mutable();
		for(ProxyBlock proxyBlock : proxyBlocks()){
			BlockState s2 = proxyBlock.defaultBlockState().setValue(HORIZONTAL_FACING, facing);
			if(level.getBlockState(moveFromOrigin(mpos.set(pos), s2)).getBlock()!=proxyBlock) return Blocks.AIR.defaultBlockState();
		}
		return state;
	}

	@SuppressWarnings("deprecation") @Override public void onRemove(BlockState oldState, World level, BlockPos pos, BlockState newState, boolean isMoving){
		if(!oldState.is(newState.getBlock())){
			TileEntity blockEntity = level.getBlockEntity(moveToOrigin(new BlockPos.Mutable().set(pos), oldState));
			if(blockEntity instanceof FoundryTile) ((FoundryTile)blockEntity).dropAllContents();
		}
		super.onRemove(oldState, level, pos, newState, isMoving);
	}

	@Override public boolean hasTileEntity(BlockState state){
		return true;
	}
	@Override public TileEntity createTileEntity(BlockState state, IBlockReader world){
		return new FoundryTile();
	}

	public static BlockPos.Mutable moveToOrigin(BlockPos.Mutable mpos, BlockState state){
		return state.getBlock() instanceof ProxyBlock ? ((ProxyBlock)state.getBlock()).moveToOrigin(mpos, state) : mpos;
	}
	public static BlockPos.Mutable moveFromOrigin(BlockPos.Mutable mpos, BlockState state){
		return state.getBlock() instanceof ProxyBlock ? ((ProxyBlock)state.getBlock()).moveFromOrigin(mpos, state) : mpos;
	}

	public static abstract class ProxyBlock extends Block{
		private final int proxyX;
		private final int proxyY;
		private final int proxyZ;

		public ProxyBlock(int proxyX, int proxyY, int proxyZ, Properties properties){
			super(properties);
			this.proxyX = proxyX;
			this.proxyY = proxyY;
			this.proxyZ = proxyZ;
		}

		@Override protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> b){
			b.add(HORIZONTAL_FACING);
		}

		@SuppressWarnings("deprecation")
		@Override
		public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result){
			if(world.isClientSide) return ActionResultType.SUCCESS;
			TileEntity te = world.getBlockEntity(pos);
			if(te instanceof FoundryProxyTile) player.openMenu((FoundryProxyTile)te);
			return ActionResultType.CONSUME;
		}

		@SuppressWarnings("deprecation") @Override public BlockState updateShape(BlockState state, Direction dir, BlockState stateAt, IWorld level, BlockPos pos, BlockPos posAt){
			BlockPos.Mutable mpos = moveToOrigin(new BlockPos.Mutable().set(pos), state);
			if(!level.isAreaLoaded(pos, 0)) return state; // For now...
			TileEntity blockEntity = level.getBlockEntity(mpos);
			if(!(blockEntity instanceof FoundryTile)) return Blocks.AIR.defaultBlockState();
			return state;
		}

		@Override public void playerWillDestroy(World level, BlockPos pos, BlockState state, PlayerEntity player){
			TileEntity blockEntity = level.getBlockEntity(moveToOrigin(new BlockPos.Mutable().set(pos), state));
			if(blockEntity instanceof FoundryTile) level.destroyBlock(blockEntity.getBlockPos(), true, player);
		}

		@Override public boolean hasTileEntity(BlockState state){
			return true;
		}
		@Override public abstract FoundryProxyTile createTileEntity(BlockState state, IBlockReader world);

		public BlockPos.Mutable moveToOrigin(BlockPos.Mutable mpos, BlockState state){
			return moveToOrigin(mpos, state.getValue(HORIZONTAL_FACING));
		}
		public BlockPos.Mutable moveFromOrigin(BlockPos.Mutable mpos, BlockState state){
			return moveFromOrigin(mpos, state.getValue(HORIZONTAL_FACING));
		}

		public BlockPos.Mutable moveToOrigin(BlockPos.Mutable mpos, Direction dir){
			return mpos.move(dir.getClockWise(), proxyX).move(Direction.DOWN, proxyY).move(dir, proxyZ);
		}
		public BlockPos.Mutable moveFromOrigin(BlockPos.Mutable mpos, Direction dir){
			return mpos.move(dir.getCounterClockWise(), proxyX).move(Direction.UP, proxyY).move(dir.getOpposite(), proxyZ);
		}

		@Override public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player){
			return new ItemStack(ModItems.FOUNDRY.get());
		}
		@SuppressWarnings("deprecation") @Override public ItemStack getCloneItemStack(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_){
			return new ItemStack(ModItems.FOUNDRY.get());
		}
	}

	public static final class FireboxProxyBlock extends ProxyBlock{
		public FireboxProxyBlock(int proxyX, int proxyY, int proxyZ, Properties properties){
			super(proxyX, proxyY, proxyZ, properties);
		}

		@Override public FoundryProxyTile createTileEntity(BlockState state, IBlockReader world){
			return FoundryProxyTile.fireboxProxy();
		}
	}

	public static final class GrateProxyBlock extends ProxyBlock{
		public GrateProxyBlock(int proxyX, int proxyY, int proxyZ, Properties properties){
			super(proxyX, proxyY, proxyZ, properties);
		}

		@Override public FoundryProxyTile createTileEntity(BlockState state, IBlockReader world){
			return FoundryProxyTile.fireboxProxy();
		}
	}

	public static final class MoldProxyBlock extends ProxyBlock{
		private static final VoxelShape SHAPE = box(0, 6, 0, 16, 12, 16);
		private static final VoxelShape VISUAL_SHAPE = box(0, 0, 0, 16, 12, 16);

		public MoldProxyBlock(int proxyX, int proxyY, int proxyZ, Properties properties){
			super(proxyX, proxyY, proxyZ, properties);
		}

		@Override public FoundryProxyTile createTileEntity(BlockState state, IBlockReader world){
			return FoundryProxyTile.moldProxy();
		}

		@SuppressWarnings("deprecation") @Override public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_){
			return SHAPE;
		}
		@SuppressWarnings("deprecation") @Override public VoxelShape getVisualShape(BlockState p_230322_1_, IBlockReader p_230322_2_, BlockPos p_230322_3_, ISelectionContext p_230322_4_){
			return VISUAL_SHAPE;
		}
	}
}
