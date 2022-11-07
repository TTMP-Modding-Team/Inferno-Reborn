package ttmp.infernoreborn.contents;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.api.recipe.RecipeTypes;
import ttmp.infernoreborn.contents.recipe.ApplySigilRecipe;
import ttmp.infernoreborn.contents.recipe.CombineSparkRecipe;
import ttmp.infernoreborn.contents.recipe.EssenceHolderBookRecipe;
import ttmp.infernoreborn.contents.recipe.crucible.SimpleCrucibleRecipe;
import ttmp.infernoreborn.contents.recipe.crucible.SimpleCrucibleRecipeSerializer;
import ttmp.infernoreborn.contents.recipe.foundry.SimpleFoundryRecipe;
import ttmp.infernoreborn.contents.recipe.foundry.SimpleFoundryRecipeSerializer;
import ttmp.infernoreborn.contents.recipe.sigilcraft.ShapedSigilEngravingRecipe;
import ttmp.infernoreborn.contents.recipe.sigilcraft.ShapedSigilTableCraftingRecipe;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

public final class ModRecipes{
	private ModRecipes(){}

	public static final DeferredRegister<IRecipeSerializer<?>> REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

	public static final RegistryObject<SpecialRecipeSerializer<EssenceHolderBookRecipe>> ESSENCE_HOLDER_BOOK = REGISTER.register("essence_holder_book", () -> new SpecialRecipeSerializer<>(EssenceHolderBookRecipe::new));
	public static final RegistryObject<SpecialRecipeSerializer<CombineSparkRecipe>> SPARK_COMBINE = REGISTER.register("combine_spark", () -> new SpecialRecipeSerializer<>(CombineSparkRecipe::new));
	public static final RegistryObject<SpecialRecipeSerializer<ApplySigilRecipe>> APPLY_SIGIL = REGISTER.register("apply_sigil", () -> new SpecialRecipeSerializer<>(ApplySigilRecipe::new));

	public static final RegistryObject<IRecipeSerializer<ShapedSigilTableCraftingRecipe>> SHAPED_SIGIL_TABLE_CRAFTING = REGISTER.register("shaped_sigil_table_crafting", ShapedSigilTableCraftingRecipe.Serializer::new);
	public static final RegistryObject<IRecipeSerializer<ShapedSigilEngravingRecipe>> SHAPED_SIGIL_ENGRAVING = REGISTER.register("shaped_sigil_engraving", ShapedSigilEngravingRecipe.Serializer::new);

	public static final RegistryObject<IRecipeSerializer<SimpleFoundryRecipe>> FOUNDRY = REGISTER.register("foundry", SimpleFoundryRecipeSerializer::new);

	public static final RegistryObject<IRecipeSerializer<SimpleCrucibleRecipe>> CRUCIBLE = REGISTER.register("crucible", SimpleCrucibleRecipeSerializer::new);

	static{
		IRecipeType.register(MODID+":sigilcraft");
		IRecipeType.register(MODID+":foundry");
		IRecipeType.register(MODID+":crucible");
		RecipeTypes.setTypes();
	}
}
