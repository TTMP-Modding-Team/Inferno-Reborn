package datagen.builder;

import com.google.gson.JsonObject;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.fluids.FluidStack;

import java.util.Objects;

public final class BuilderUtils{
	private BuilderUtils(){}

	public static JsonObject result(ItemStack stack){
		return result(stack.getItem(), stack.getCount());
	}
	public static JsonObject result(IItemProvider item, int count){
		JsonObject result = new JsonObject();
		result.addProperty("item", Objects.requireNonNull(item.asItem().getRegistryName()).toString());
		if(count>1) result.addProperty("count", count);
		return result;
	}
	public static JsonObject result(FluidStack fluid){
		return result(fluid.getFluid(), fluid.getAmount());
	}
	public static JsonObject result(Fluid fluid, int amount){
		JsonObject result = new JsonObject();
		result.addProperty("fluid", Objects.requireNonNull(fluid.getRegistryName()).toString());
		result.addProperty("amount", amount);
		return result;
	}
}
