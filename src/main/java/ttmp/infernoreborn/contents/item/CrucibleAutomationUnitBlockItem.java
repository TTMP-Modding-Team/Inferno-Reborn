package ttmp.infernoreborn.contents.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import static ttmp.infernoreborn.contents.tile.crucible.Crucible.crucible;
import static ttmp.infernoreborn.util.ReplaceBlockContext.realClickedPos;

public class CrucibleAutomationUnitBlockItem extends BlockItem{
	public CrucibleAutomationUnitBlockItem(Block block, Properties p){
		super(block, p);
	}

	@Override public ActionResultType place(BlockItemUseContext ctx){
		return placeOnCrucible(ctx) ? super.place(new UseContext(ctx)) : super.place(ctx);
	}

	protected boolean placeOnCrucible(BlockItemUseContext ctx){
		if(ctx.getClickedFace()==Direction.DOWN||ctx.getClickedFace()==Direction.UP) return false;
		BlockPos pos = realClickedPos(ctx);
		double x = ctx.getClickLocation().x-pos.getX();
		if(gt(2/16.0, x)||gt(x, 14/16.0)) return false;
		double z = ctx.getClickLocation().z-pos.getZ();
		if(gt(2/16.0, z)||gt(z, 14/16.0)) return false;
		return crucible(ctx.getLevel(), pos)!=null;
	}

	/**
	 * @return whether {@code a} is greater than {@code b} above epsilon
	 */
	protected static boolean gt(double a, double b){
		return a-b>Double.MIN_VALUE;
	}

	public static final class UseContext extends BlockItemUseContext{
		public UseContext(BlockItemUseContext ctx){
			super(ctx);
		}

		@Override public BlockPos getClickedPos(){
			return this.replaceClicked ? super.getClickedPos() : getHitResult().getBlockPos().above();
		}
		@Override public Direction getClickedFace(){
			return Direction.UP;
		}
	}
}
