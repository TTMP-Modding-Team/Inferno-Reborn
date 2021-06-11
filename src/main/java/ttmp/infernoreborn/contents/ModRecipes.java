package ttmp.infernoreborn.contents;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.recipe.CombineSparkRecipe;
import ttmp.infernoreborn.recipe.EssenceHolderBookRecipe;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class ModRecipes{
	private ModRecipes(){}

	public static final DeferredRegister<IRecipeSerializer<?>> REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

	public static final RegistryObject<SpecialRecipeSerializer<EssenceHolderBookRecipe>> ESSENCE_HOLDER_BOOK = REGISTER.register("essence_holder_book", () -> new SpecialRecipeSerializer<>(EssenceHolderBookRecipe::new));
	public static final RegistryObject<SpecialRecipeSerializer<CombineSparkRecipe>> SPARK_COMBINE = REGISTER.register("combine_spark", () -> new SpecialRecipeSerializer<>(CombineSparkRecipe::new));
}
