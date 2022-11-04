package ttmp.infernoreborn.api.crucible;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Interface for {@link Block}s. All blocks inheriting this interface can provide heat to crucible on top.
 */
public interface CrucibleHeatSource{
	CrucibleHeat getHeat(World level, BlockPos pos);
}
