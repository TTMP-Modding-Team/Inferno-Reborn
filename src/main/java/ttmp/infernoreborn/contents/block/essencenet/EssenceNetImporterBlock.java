package ttmp.infernoreborn.contents.block.essencenet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import ttmp.infernoreborn.contents.tile.EssenceNetImporterTile;

public class EssenceNetImporterBlock extends Block{
	public EssenceNetImporterBlock(Properties properties){
		super(properties);
	}

	@Override public boolean hasTileEntity(BlockState state){
		return true;
	}
	@Override public TileEntity createTileEntity(BlockState state, IBlockReader world){
		return new EssenceNetImporterTile();
	}
}
