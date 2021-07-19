package ttmp.infernoreborn.datagen;

import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.ModRecipes;
import ttmp.infernoreborn.contents.Sigils;
import ttmp.infernoreborn.datagen.builder.FoundryRecipeBuilder;
import ttmp.infernoreborn.datagen.builder.NotSoSpecialRecipeBuilder;
import ttmp.infernoreborn.datagen.builder.ShapedSigilEngravingRecipeBuilder;
import ttmp.infernoreborn.datagen.builder.ShapedSigilTableCraftingRecipeBuilder;
import ttmp.infernoreborn.util.EssenceSize;
import ttmp.infernoreborn.util.EssenceType;

import java.util.Objects;
import java.util.function.Consumer;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public class RecipeGen extends RecipeProvider{
	public RecipeGen(DataGenerator generator){
		super(generator);
	}

	@Override protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer){
		new NotSoSpecialRecipeBuilder(ModRecipes.ESSENCE_HOLDER_BOOK.get())
				.unlockedBy("has_the_book", has(ModItems.BOOK_OF_THE_UNSPEAKABLE.get()))
				.save(consumer, new ResourceLocation(MODID, "essence_holder_book"));
		CustomRecipeBuilder.special(ModRecipes.SPARK_COMBINE.get())
				.save(consumer, MODID+":combine_spark");
		CustomRecipeBuilder.special(ModRecipes.APPLY_SIGIL.get())
				.save(consumer, MODID+":apply_sigil");

		EssenceSize[] sizes = EssenceSize.values();
		for(EssenceType type : EssenceType.values()){
			for(int i = 1; i<sizes.length; i++){
				EssenceSize s1 = sizes[i-1];
				EssenceSize s2 = sizes[i];

				ShapedRecipeBuilder.shaped(type.getItem(s2))
						.pattern("111")
						.pattern("111")
						.pattern("111")
						.define('1', Ingredient.of(type.getItem(s1)))
						.unlockedBy("has_item", has(type.getItem(s1)))
						.save(consumer, new ResourceLocation(MODID, "compress_"+Objects.requireNonNull(type.getItem(s1).getRegistryName()).getPath()));

				ShapelessRecipeBuilder.shapeless(type.getItem(s1), 9)
						.requires(Ingredient.of(type.getItem(s2)))
						.unlockedBy("has_item", has(type.getItem(s2)))
						.save(consumer, new ResourceLocation(MODID, "decompress_"+Objects.requireNonNull(type.getItem(s2).getRegistryName()).getPath()));
			}
		}
/*

		new ShapedSigilEngravingRecipeBuilder(Sigils.TEST.get())
				.pattern("1 1")
				.pattern(" X ")
				.pattern("1 1")
				.defineAsCenter('X')
				.define('1', Ingredient.of(ModItems.BLOOD_ESSENCE_SHARD.get()))
				.unlockedBy("fuck", has(ModItems.BLOOD_ESSENCE_SHARD.get()))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/test"));
*/

		new ShapedSigilTableCraftingRecipeBuilder(Items.BEDROCK)
				.pattern("2X2")
				.pattern("121")
				.defineAsCenter('X', Ingredient.of(Items.STONE))
				.define('1', Ingredient.of(ModItems.GREATER_METAL_ESSENCE_CRYSTAL.get()))
				.define('2', Ingredient.of(ModItems.GREATER_EARTH_ESSENCE_CRYSTAL.get()))
				.unlockedBy("fuck", has(Items.STONE))
				.save(consumer, new ResourceLocation(MODID, "sigilcraft/test"));
		new ShapedSigilTableCraftingRecipeBuilder(Items.ACACIA_BUTTON)
				.pattern("  1  ")
				.pattern("1 1 1")
				.pattern(" 1X1 ")
				.pattern("  1  ")
				.pattern("  1  ")
				.defineAsCenter('X', Ingredient.of(Items.ACACIA_LOG))
				.define('1', Ingredient.of(ModItems.METAL_ESSENCE_CRYSTAL.get()))
				.unlockedBy("fuck", has(ModItems.METAL_ESSENCE_CRYSTAL.get()))
				.save(consumer, new ResourceLocation(MODID, "sigilcraft/test2"));
/*

		new ShapedSigilEngravingRecipeBuilder(Sigils.TEST2.get())
				.pattern("  1  ")
				.pattern("1 1 1")
				.pattern(" 1X1 ")
				.pattern("  1  ")
				.pattern("  1  ")
				.defineAsCenter('X')
				.define('1', Ingredient.of(ModItems.METAL_ESSENCE_CRYSTAL.get()))
				.unlockedBy("fuck", has(ModItems.METAL_ESSENCE_CRYSTAL.get()))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/test2"));
		new ShapedSigilEngravingRecipeBuilder(Sigils.TEST3.get())
				.pattern("   1   ")
				.pattern("  1 1  ")
				.pattern(" 1   1 ")
				.pattern("11 X 11")
				.pattern(" 1 1   ")
				.pattern("11111  ")
				.pattern("   1   ")
				.defineAsCenter('X')
				.define('1', Ingredient.of(ModItems.BLOOD_ESSENCE_SHARD.get()))
				.unlockedBy("fuck", has(ModItems.BLOOD_ESSENCE_SHARD.get()))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/test3"));
*/

		new FoundryRecipeBuilder(new ItemStack(ModItems.DAMASCUS_STEEL_INGOT.get()))
				.ingredient(Ingredient.of(Tags.Items.INGOTS_IRON), 1)
				.ingredient(Ingredient.of(ItemTags.COALS), 4)
				.essence(EssenceType.METAL, 9)
				.unlockedBy("iron", has(Tags.Items.INGOTS_IRON))
				.save(consumer, new ResourceLocation(MODID, "foundry/damascus_steel_ingot"));
		new FoundryRecipeBuilder(new ItemStack(Items.ACACIA_BOAT), new ItemStack(Items.COBBLESTONE, 5))
				.ingredient(Ingredient.of(Items.BEDROCK), 1)
				.essence(EssenceType.DEATH, 1)
				.processingTime(100)
				.unlockedBy("bedrock", has(Items.BEDROCK))
				.save(consumer, new ResourceLocation(MODID, "foundry/test"));
	}
}
