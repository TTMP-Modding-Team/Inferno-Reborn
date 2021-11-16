package ttmp.infernoreborn.contents.block.essencenet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.tile.EssenceNetImporterTile;

import javax.annotation.Nullable;

import static ttmp.infernoreborn.contents.block.ModProperties.NO_NETWORK;

public class EssenceNetImporterBlock extends Block{
	public EssenceNetImporterBlock(Properties properties){
		super(properties);
	}

	@Override protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> b){
		b.add(NO_NETWORK);
	}

	@Override public void setPlacedBy(World level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		TileEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof EssenceNetImporterTile)
			((EssenceNetImporterTile)blockEntity).updateBlock();
	}

	@Override public boolean hasTileEntity(BlockState state){
		return true;
	}
	@Override public TileEntity createTileEntity(BlockState state, IBlockReader world){
		return new EssenceNetImporterTile();
	}
}
