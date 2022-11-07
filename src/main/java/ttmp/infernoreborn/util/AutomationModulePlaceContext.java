package ttmp.infernoreborn.util;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class AutomationModulePlaceContext extends BlockItemUseContext{
	public AutomationModulePlaceContext(BlockItemUseContext ctx){
		super(ctx);
	}

	@Override public BlockPos getClickedPos(){
		return replaceClicked||getClickedFace()==Direction.UP||getClickedFace()==Direction.DOWN ?
				super.getClickedPos() : super.getClickedPos().above();
	}
}
