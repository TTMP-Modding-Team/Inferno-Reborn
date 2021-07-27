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
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.ModBlocks;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.tile.FoundryProxyTile;
import ttmp.infernoreborn.contents.tile.FoundryTile;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;
import static net.minecraft.state.properties.BlockStateProperties.LIT;

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
		this.registerDefaultState(defaultBlockState().setValue(LIT, false));
	}

	@Override protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> b){
		b.add(HORIZONTAL_FACING, LIT);
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
		public final int proxyX;
		public final int proxyY;
		public final int proxyZ;

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
			if(blockEntity instanceof FoundryTile) level.destroyBlock(blockEntity.getBlockPos(), !player.isCreative(), player);
		}

		@SuppressWarnings("deprecation") @Override public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean pIsMoving){
			if(!state.is(newState.getBlock())){
				TileEntity blockEntity = level.getBlockEntity(moveToOrigin(new BlockPos.Mutable().set(pos), state));
				if(blockEntity instanceof FoundryTile) level.destroyBlock(blockEntity.getBlockPos(), true, null);
			}
			super.onRemove(state, level, pos, newState, pIsMoving);
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
			this.registerDefaultState(defaultBlockState().setValue(LIT, false));
		}

		@Override protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> b){
			b.add(HORIZONTAL_FACING, LIT);
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

	private static abstract class MoldProxyBlockBase extends ProxyBlock{
		private final Map<Direction, VoxelShape> shapes;

		public MoldProxyBlockBase(int proxyX, int proxyY, int proxyZ, Properties properties){
			super(proxyX, proxyY, proxyZ, properties);
			this.shapes = new HashMap<>();
			for(Direction d : HORIZONTAL_FACING.getPossibleValues())
				this.shapes.put(d, createVoxelShape(d));
		}

		protected abstract VoxelShape createVoxelShape(Direction dir);

		@Override public FoundryProxyTile createTileEntity(BlockState state, IBlockReader world){
			return FoundryProxyTile.moldProxy();
		}

		@SuppressWarnings("deprecation") @Override public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context){
			return shapes.get(state.getValue(HORIZONTAL_FACING));
		}

		protected static VoxelShape box(double x1, double y1, double z1, double x2, double y2, double z2, Direction dir){
			switch(dir){
				case NORTH:
					return box(x1, y1, z1, x2, y2, z2);
				case SOUTH:
					return box(16-x1, y1, 16-z1, 16-x2, y2, 16-z2);
				case WEST:
					return box(z1, y1, 16-x1, z2, y2, 16-x2);
				case EAST:
					return box(16-z1, y1, x1, 16-z2, y2, x2);
				default:
					throw new IllegalArgumentException("dir");
			}
		}
	}

	public static final class MoldProxyBlock extends MoldProxyBlockBase{
		public MoldProxyBlock(int proxyX, int proxyY, int proxyZ, Properties properties){
			super(proxyX, proxyY, proxyZ, properties);
		}

		@Override protected VoxelShape createVoxelShape(Direction dir){
			dir = dir.getClockWise();
			return VoxelShapes.or(box(0, 10, 0, 4, 12, 16, dir),
					box(4, 10, 11, 16, 12, 16, dir),
					box(4, 10, 0, 14, 12, 5, dir),
					box(14, 10, 0, 15, 12, 4, dir),
					box(15, 10, 0, 16, 12, 3, dir),
					box(0, 6, 0, 16, 10, 16, dir),
					box(0, 0, 0, 16, 12, 0, dir),
					box(0, 0, 16, 16, 12, 16, dir),
					box(0, 0, 0, 0, 12, 16, dir))
					.optimize();
		}
	}

	public static final class MoldProxyBlock2 extends MoldProxyBlockBase{
		public MoldProxyBlock2(int proxyX, int proxyY, int proxyZ, Properties properties){
			super(proxyX, proxyY, proxyZ, properties);
		}

		@Override protected VoxelShape createVoxelShape(Direction dir){
			dir = dir.getCounterClockWise();
			return VoxelShapes.or(box(0, 10, 0, 4, 12, 16, dir),
					box(4, 10, 11, 14, 12, 16, dir),
					box(4, 10, 0, 16, 12, 5, dir),
					box(14, 10, 12, 15, 12, 16, dir),
					box(15, 10, 13, 16, 12, 16, dir),
					box(0, 6, 0, 16, 10, 16, dir),
					box(0, 0, 0, 16, 12, 0, dir),
					box(0, 0, 16, 16, 12, 16, dir),
					box(0, 0, 0, 0, 12, 16, dir))
					.optimize();
		}
	}
}
