package ttmp.infernoreborn.contents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.tile.crucible.AutomationModule;
import ttmp.infernoreborn.contents.tile.crucible.MockAutomationModuleTile;

public class BaseAutomationModuleBlock extends Block{
	public BaseAutomationModuleBlock(Properties p){
		super(p);
	}

	@SuppressWarnings("deprecation")
	@Override public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean moving){
		if(!state.is(newState.getBlock())){
			TileEntity te = level.getBlockEntity(pos);
			if(te instanceof AutomationModule) ((AutomationModule)te).setUnattached();
			super.onRemove(state, level, pos, newState, moving);
		}
	}

	@Override public boolean hasTileEntity(BlockState state){
		return true;
	}
	@Override public TileEntity createTileEntity(BlockState state, IBlockReader world){
		return new MockAutomationModuleTile(); // TODO
	}
}
