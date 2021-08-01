package ttmp.infernoreborn.contents.block.essencenet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import ttmp.infernoreborn.contents.tile.EssenceNetExporterTile;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class EssenceNetExporterBlock extends Block{
	public EssenceNetExporterBlock(Properties properties){
		super(properties);
	}

	@Override protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> b){
		b.add(BlockStateProperties.FACING);
	}

	@Override public BlockState getStateForPlacement(BlockItemUseContext ctx){
		return this.defaultBlockState().setValue(HORIZONTAL_FACING, ctx.getPlayer()==null||!ctx.getPlayer().isCrouching() ?
				ctx.getClickedFace() : ctx.getClickedFace().getOpposite());
	}

	@Override public boolean hasTileEntity(BlockState state){
		return true;
	}
	@Override public TileEntity createTileEntity(BlockState state, IBlockReader world){
		return new EssenceNetExporterTile();
	}
}
