package ttmp.infernoreborn.contents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.tile.FoundryProxyTile;
import ttmp.infernoreborn.contents.tile.FoundryTile;

import javax.annotation.Nullable;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class FoundryBlock extends Block{
	public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);

	public FoundryBlock(Properties properties){
		super(properties);
	}

	@Override protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> b){
		b.add(PART, HORIZONTAL_FACING);
	}

	@Override public boolean hasTileEntity(BlockState state){
		return true;
	}
	@Nullable @Override public TileEntity createTileEntity(BlockState state, IBlockReader world){
		return state.getValue(PART)==Part.B000_FIREBOX ? new FoundryTile() : new FoundryProxyTile();
	}

	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result){
		if(world.isClientSide) return ActionResultType.SUCCESS;
		TileEntity te = world.getBlockEntity(pos);
		if(state.getValue(PART)==Part.B000_FIREBOX){
			if(!(te instanceof FoundryTile)) return ActionResultType.CONSUME;
			player.openMenu((FoundryTile)te);
		}else{
			if(!(te instanceof FoundryProxyTile)) return ActionResultType.CONSUME;
			player.openMenu((FoundryProxyTile)te);
		}
		return ActionResultType.CONSUME;
	}

	public enum Part implements IStringSerializable{
		B000_FIREBOX(0, 0, 0),
		B001_FIREBOX(0, 0, 1),
		B010_GRATE(0, 1, 0),
		B011_GRATE(0, 1, 1),
		B100_MOLD(1, 0, 0),
		B101_MOLD(1, 0, 1);

		public final int x;
		public final int y;
		public final int z;

		Part(int x, int y, int z){
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override public String getSerializedName(){
			return Integer.toString(ordinal());
		}
	}
}
