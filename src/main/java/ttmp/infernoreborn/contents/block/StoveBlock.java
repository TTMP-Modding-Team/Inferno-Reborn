package ttmp.infernoreborn.contents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import ttmp.infernoreborn.api.crucible.CrucibleHeat;
import ttmp.infernoreborn.api.crucible.CrucibleHeatSource;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.tile.crucible.EssenceStoveTile;
import ttmp.infernoreborn.contents.tile.crucible.FuelBasedStoveTile;
import ttmp.infernoreborn.contents.tile.crucible.StoveTile;

import static net.minecraft.state.properties.BlockStateProperties.LIT;

public abstract class StoveBlock extends Block implements CrucibleHeatSource{
	public StoveBlock(Properties p){
		super(p);
		this.registerDefaultState(getStateDefinition().any().setValue(LIT, false));
	}

	@SuppressWarnings("deprecation")
	@Override public ActionResultType use(
			BlockState state,
			World level,
			BlockPos pos,
			PlayerEntity player,
			Hand hand,
			BlockRayTraceResult hit){
		if(hit.getDirection()==Direction.UP&&player.getItemInHand(hand).getItem()==ModItems.CRUCIBLE.get())
			return ActionResultType.PASS;
		if(level.isClientSide) return ActionResultType.SUCCESS;
		TileEntity te = level.getBlockEntity(pos);
		if(te instanceof INamedContainerProvider) player.openMenu((INamedContainerProvider)te);
		return ActionResultType.CONSUME;
	}

	@Override protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> b){
		b.add(LIT);
	}

	@Override public boolean hasTileEntity(BlockState state){
		return true;
	}
	public abstract StoveTile createTileEntity(BlockState state, IBlockReader world);

	public static final class Furnace extends StoveBlock{
		public Furnace(Properties p){
			super(p);
		}
		@Override public CrucibleHeat getHeat(BlockState state, World level, BlockPos pos){
			return state.getValue(LIT) ? CrucibleHeat.FURNACE : CrucibleHeat.NONE;
		}
		@Override public StoveTile createTileEntity(BlockState state, IBlockReader world){
			return new FuelBasedStoveTile.Furnace();
		}
	}

	public static final class Foundry extends StoveBlock{
		public Foundry(Properties p){
			super(p);
		}
		@Override public CrucibleHeat getHeat(BlockState state, World level, BlockPos pos){
			return state.getValue(LIT) ? CrucibleHeat.FOUNDRY : CrucibleHeat.NONE;
		}
		@Override public StoveTile createTileEntity(BlockState state, IBlockReader world){
			return new FuelBasedStoveTile.Foundry();
		}
	}

	public static final class Nether extends StoveBlock{
		public Nether(Properties p){
			super(p);
		}
		@Override public CrucibleHeat getHeat(BlockState state, World level, BlockPos pos){
			return state.getValue(LIT) ? CrucibleHeat.NETHER : CrucibleHeat.FURNACE;
		}
		@Override public StoveTile createTileEntity(BlockState state, IBlockReader world){
			return new FuelBasedStoveTile.Nether();
		}
	}

	public static final class Essence extends StoveBlock{
		public Essence(Properties p){
			super(p);
		}
		@Override public CrucibleHeat getHeat(BlockState state, World level, BlockPos pos){
			return state.getValue(LIT) ? CrucibleHeat.ESSENCE : CrucibleHeat.NONE;
		}
		@Override public StoveTile createTileEntity(BlockState state, IBlockReader world){
			return new EssenceStoveTile();
		}
	}
}
