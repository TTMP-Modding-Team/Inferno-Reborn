package ttmp.infernoreborn.contents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class NamedContainerBlock extends Block{
	public NamedContainerBlock(Properties properties){
		super(properties);
	}

	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result){
		if(world.isClientSide) return ActionResultType.SUCCESS;
		TileEntity te = world.getBlockEntity(pos);
		if(!(te instanceof INamedContainerProvider)) return ActionResultType.CONSUME;
		player.openMenu((INamedContainerProvider)te);
		return ActionResultType.CONSUME;
	}

	@Override public abstract TileEntity createTileEntity(BlockState state, IBlockReader world);
	@Override public boolean hasTileEntity(BlockState state){
		return true;
	}

	@SuppressWarnings("deprecation") @Override public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving){
		if (!state.is(newState.getBlock())) {
			TileEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof OnRemoveListener)
				((OnRemoveListener)blockEntity).onRemove(state, level, pos, newState, isMoving);
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	public interface OnRemoveListener{
		void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving);
	}
}
