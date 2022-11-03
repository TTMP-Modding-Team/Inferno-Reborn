package ttmp.infernoreborn.contents.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface CrucibleHeatSource{
	CrucibleHeat getHeat(World level, BlockPos pos);
}
