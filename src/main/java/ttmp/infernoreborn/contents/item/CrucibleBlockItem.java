package ttmp.infernoreborn.contents.item;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import ttmp.infernoreborn.util.ReplaceBlockContext;

public class CrucibleBlockItem extends BlockItem{
	public CrucibleBlockItem(Block block, Properties p){
		super(block, p);
	}

	@Override public ActionResultType place(BlockItemUseContext ctx){
		return super.place(ReplaceBlockContext.replaceIf(ctx,
				p -> ctx.getClickedFace()==Direction.UP&&ctx.getLevel().getBlockState(p).is(Blocks.CAMPFIRE)));
	}
}
