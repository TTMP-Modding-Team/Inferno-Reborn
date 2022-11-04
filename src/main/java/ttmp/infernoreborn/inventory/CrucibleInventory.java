package ttmp.infernoreborn.inventory;

import net.minecraft.inventory.IInventory;
import ttmp.infernoreborn.contents.block.CrucibleHeat;
import ttmp.infernoreborn.util.EssenceHandler;

public interface CrucibleInventory extends IInventory{
	EssenceHandler essences();
	CrucibleHeat heat();
	int waterLevel();
}
