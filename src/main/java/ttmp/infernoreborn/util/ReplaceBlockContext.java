package ttmp.infernoreborn.util;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;
import java.util.function.Predicate;

public final class ReplaceBlockContext extends BlockItemUseContext{
	public static BlockPos realClickedPos(BlockItemUseContext ctx){
		return ctx.replacingClickedOnBlock() ? ctx.getClickedPos() : ctx.getClickedPos().relative(ctx.getClickedFace().getOpposite());
	}

	public static BlockItemUseContext replaceIf(BlockItemUseContext ctx, Predicate<BlockPos> predicate){
		return predicate.test(realClickedPos(ctx)) ? new ReplaceBlockContext(ctx) : ctx;
	}

	public ReplaceBlockContext(BlockItemUseContext ctx){
		super(ctx);
	}

	@Override public boolean canPlace(){
		return true;
	}

	@Override public BlockPos getClickedPos(){
		return this.getHitResult().getBlockPos();
	}

	@Override public Direction[] getNearestLookingDirections(){
		return Direction.orderedByNearest(Objects.requireNonNull(this.getPlayer()));
	}
}
