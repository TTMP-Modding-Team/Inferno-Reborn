package ttmp.infernoreborn.contents;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.contents.recipe.ApplySigilRecipe;
import ttmp.infernoreborn.contents.recipe.CombineSparkRecipe;
import ttmp.infernoreborn.contents.recipe.EssenceHolderBookRecipe;
import ttmp.infernoreborn.contents.recipe.sigilcraft.ShapedSigilEngravingRecipe;
import ttmp.infernoreborn.contents.recipe.sigilcraft.ShapedSigilTableCraftingRecipe;
import ttmp.infernoreborn.contents.recipe.sigilcraft.SigilcraftRecipe;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class ModRecipes{
	private ModRecipes(){}

	public static final IRecipeType<SigilcraftRecipe> SIGILCRAFT_RECIPE_TYPE = IRecipeType.register(MODID+":sigilcraft");

	public static final DeferredRegister<IRecipeSerializer<?>> REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

	public static final RegistryObject<SpecialRecipeSerializer<EssenceHolderBookRecipe>> ESSENCE_HOLDER_BOOK = REGISTER.register("essence_holder_book", () -> new SpecialRecipeSerializer<>(EssenceHolderBookRecipe::new));
	public static final RegistryObject<SpecialRecipeSerializer<CombineSparkRecipe>> SPARK_COMBINE = REGISTER.register("combine_spark", () -> new SpecialRecipeSerializer<>(CombineSparkRecipe::new));
	public static final RegistryObject<SpecialRecipeSerializer<ApplySigilRecipe>> APPLY_SIGIL = REGISTER.register("apply_sigil", () -> new SpecialRecipeSerializer<>(ApplySigilRecipe::new));

	public static final RegistryObject<IRecipeSerializer<ShapedSigilTableCraftingRecipe>> SHAPED_SIGIL_TABLE_CRAFTING = REGISTER.register("shaped_sigil_table_crafting", ShapedSigilTableCraftingRecipe.Serializer::new);
	public static final RegistryObject<IRecipeSerializer<ShapedSigilEngravingRecipe>> SHAPED_SIGIL_ENGRAVING = REGISTER.register("shaped_sigil_engraving", ShapedSigilEngravingRecipe.Serializer::new);
}
