package ttmp.infernoreborn.contents.tile.crucible;

import net.minecraft.util.Direction;

public interface AutomationModule{
	void setUnattached();
	void setAttached(CrucibleAutomationUnitTile tile, Direction direction);

	/**
	 * Operate on a crucible; called every 1/2 sec
	 */
	void operate(CrucibleTile crucible);
}
