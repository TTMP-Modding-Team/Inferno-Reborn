package ttmp.infernoreborn.api;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

/**
 * Nontraditional recipes which uses {@link Simulation} for simulating and processing recipes. Various forms of
 * ingredient consumptions and such are abstracted away in simulation instance; by applying result of {@link
 * WackyRecipe#consume(IInventory)}, you apply deduction or whatever state-modifying action this recipe does, and gets
 * an object indicating the result of this recipe.<br>
 * The result can be either simple item, or more complex record containing multiple items, fluids or such. One caveat,
 * however, is that shared instances such as {@link ItemStack} or {@link FluidStack} must be carried out as copy. This
 * helps code using this recipe to not accidentally modify part of recipe and prevents possibly redundant copy action.
 *
 * @param <INV> Input inventory required for this recipe
 * @param <R>   Recipe result
 */
public interface WackyRecipe<INV extends IInventory, R> extends IRecipe<INV>{
	Simulation<R> consume(INV inv);

	@Override default boolean matches(INV inv, World level){
		return consume(inv).isSuccess();
	}

	/**
	 * @deprecated Use {@link WackyRecipe#consume(IInventory)} instead
	 */
	@Deprecated @Override default ItemStack assemble(INV inv){
		return ItemStack.EMPTY;
	}
	@Deprecated @Override default boolean canCraftInDimensions(int width, int height){
		return true;
	}
}
