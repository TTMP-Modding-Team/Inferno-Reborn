package ttmp.infernoreborn.api.crucible;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.fluids.capability.IFluidHandler;
import ttmp.infernoreborn.api.essence.EssenceHandler;
import ttmp.infernoreborn.api.recipe.RecipeHelper;

public interface CrucibleInventory extends IInventory{
	EssenceHandler essences();
	CrucibleHeat heat();
	RecipeHelper.FluidTankAccessor fluidInput();
}
