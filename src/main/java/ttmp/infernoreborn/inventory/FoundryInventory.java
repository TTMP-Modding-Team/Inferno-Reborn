package ttmp.infernoreborn.inventory;

import net.minecraft.inventory.IInventory;
import ttmp.infernoreborn.util.EssenceHolder;

import javax.annotation.Nullable;

public interface FoundryInventory extends IInventory{
	@Nullable EssenceHolder getEssenceHolder();
}
