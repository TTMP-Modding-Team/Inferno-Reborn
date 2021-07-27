package ttmp.infernoreborn.contents.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.tile.GoldenSkullTile;

import javax.annotation.Nullable;

public class GoldenWallSkullBlock extends WallSkullBlock{
	public GoldenWallSkullBlock(Properties properties){
		super(GoldenSkullBlock.TYPE, properties);
	}

	@Override public boolean hasTileEntity(BlockState state){
		return true;
	}
	@Override public TileEntity createTileEntity(BlockState state, IBlockReader world){
		return new GoldenSkullTile();
	}

	@Override public void setPlacedBy(World pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack){
		if(pStack.hasCustomHoverName()){
			TileEntity te = pLevel.getBlockEntity(pPos);
			if(te instanceof GoldenSkullTile)
				((GoldenSkullTile)te).setCustomName(pStack.getHoverName());
		}
	}
}
