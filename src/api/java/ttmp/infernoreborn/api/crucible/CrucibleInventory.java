package ttmp.infernoreborn.api.crucible;

import net.minecraft.inventory.IInventory;
import ttmp.infernoreborn.api.essence.EssenceHandler;

public interface CrucibleInventory extends IInventory{
	EssenceHandler essences();
	CrucibleHeat heat();
	int waterLevel();
}
