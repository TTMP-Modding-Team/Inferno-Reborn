package ttmp.infernoreborn.contents.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.block.FoundryBlock;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class FoundryBlockItem extends BlockItem{
	public FoundryBlockItem(Block block, Properties properties){
		super(block, properties);
	}

	@Override protected boolean canPlace(BlockItemUseContext ctx, BlockState state){
		World level = ctx.getLevel();
		if(this.mustSurvive()&&!state.canSurvive(level, ctx.getClickedPos())) return false;
		PlayerEntity player = ctx.getPlayer();
		ISelectionContext sctx = player==null ? ISelectionContext.empty() : ISelectionContext.of(player);
		BlockPos.Mutable mpos = new BlockPos.Mutable();

		Direction facing = state.getValue(HORIZONTAL_FACING);

		for(FoundryBlock.ProxyBlock proxyBlock : FoundryBlock.proxyBlocks()){
			BlockState s2 = proxyBlock.defaultBlockState().setValue(HORIZONTAL_FACING, facing);
			if(!level.isUnobstructed(s2, FoundryBlock.moveFromOrigin(mpos.set(ctx.getClickedPos()), s2), sctx)) return false;
		}
		return true;
	}
}
