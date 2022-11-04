package ttmp.infernoreborn.api.foundry;

import net.minecraft.inventory.IInventory;
import ttmp.infernoreborn.api.essence.EssenceHandler;

import javax.annotation.Nullable;

public interface FoundryInventory extends IInventory{
	@Nullable EssenceHandler getEssenceHandler();
}
