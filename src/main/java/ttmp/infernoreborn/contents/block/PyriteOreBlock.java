package ttmp.infernoreborn.contents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;

public class PyriteOreBlock extends Block{
	public PyriteOreBlock(Properties p){
		super(p);
	}

	@Override public int getExpDrop(BlockState state, IWorldReader world, BlockPos pos, int fortune, int silktouch){
		return silktouch>0 ? 0 : MathHelper.nextInt(RANDOM, 0, 1);
	}
}
