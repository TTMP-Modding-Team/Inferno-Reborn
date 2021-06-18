package ttmp.infernoreborn.contents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.tile.SigilEngravingTableTile;

public abstract class SigilEngravingTableBlock extends Block{
	public SigilEngravingTableBlock(Properties properties){
		super(properties);
	}

	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result){
		if(world.isClientSide) return ActionResultType.SUCCESS;
		TileEntity te = world.getBlockEntity(pos);
		if(!(te instanceof SigilEngravingTableTile)) return ActionResultType.CONSUME;
		player.openMenu((SigilEngravingTableTile)te);
		return ActionResultType.CONSUME;
	}

	@Override public abstract SigilEngravingTableTile createTileEntity(BlockState state, IBlockReader world);
	@Override public boolean hasTileEntity(BlockState state){
		return true;
	}
}
