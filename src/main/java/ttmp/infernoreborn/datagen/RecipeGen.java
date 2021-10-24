package ttmp.infernoreborn.datagen;

import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
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

				compactAndUncompact(type.getItem(s2), type.getItem(s1), consumer);
			}
		}

		compactAndUncompact(ModItems.PYRITE_BLOCK.get(), ModItems.PYRITE_INGOT.get(), consumer);
		compactAndUncompact(ModItems.PYRITE_INGOT.get(), ModItems.PYRITE_NUGGET.get(), consumer);
		compactAndUncompact(ModItems.NETHER_STEEL_BLOCK.get(), ModItems.NETHER_STEEL_INGOT.get(), consumer);
		compactAndUncompact(ModItems.NETHER_STEEL_INGOT.get(), ModItems.NETHER_STEEL_NUGGET.get(), consumer);
		compactAndUncompact(ModItems.DAMASCUS_STEEL_BLOCK.get(), ModItems.DAMASCUS_STEEL_INGOT.get(), consumer);
		compactAndUncompact(ModItems.DAMASCUS_STEEL_INGOT.get(), ModItems.DAMASCUS_STEEL_NUGGET.get(), consumer);

		new ShapedSigilTableCraftingRecipeBuilder(Items.PUFFERFISH)
				.pattern("111")
				.pattern("4X4")
				.pattern("222")
				.defineAsCenter('X', Ingredient.of(Items.COBBLESTONE))
				.define('1', Ingredient.of(greaterCrystal(EssenceType.DOMINANCE)))
				.define('2', Ingredient.of(greaterCrystal(EssenceType.AIR)))
				.define('4', Ingredient.of(greaterCrystal(EssenceType.BLOOD)))
				.unlockedBy("fuck", has(greaterCrystal(EssenceType.DOMINANCE)))
				.save(consumer, new ResourceLocation(MODID, "sigilcraft/pfu"));

		new ShapedSigilTableCraftingRecipeBuilder(Items.PUFFERFISH)
				.pattern("  111  ")
				.pattern("3144413")
				.pattern("344X443")
				.pattern(" 24442 ")
				.pattern("  222  ")
				.defineAsCenter('X', Ingredient.of(Items.COBBLESTONE))
				.define('1', Ingredient.of(greaterCrystal(EssenceType.DOMINANCE)))
				.define('2', Ingredient.of(greaterCrystal(EssenceType.AIR)))
				.define('3', Ingredient.of(greaterCrystal(EssenceType.WATER)))
				.define('4', Ingredient.of(greaterCrystal(EssenceType.BLOOD)))
				.unlockedBy("fuck", has(greaterCrystal(EssenceType.DOMINANCE)))
				.save(consumer, new ResourceLocation(MODID, "sigilcraft/pf"));
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
		new ShapedSigilEngravingRecipeBuilder(Sigils.GOAT_EYES.get())
				.pattern("1X1")
				.pattern(" 2 ")
				.defineAsCenter('X')
				.define('1', Ingredient.of(shard(EssenceType.AIR)))
				.define('2', Ingredient.of(shard(EssenceType.DOMINANCE)))
				.unlockedBy("fuck", has(shard(EssenceType.DOMINANCE)))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/goat_eyes"));
		new ShapedSigilEngravingRecipeBuilder(Sigils.FAINT_AFFLICTION.get())
				.pattern("11 ")
				.pattern("1X1")
				.pattern(" 2 ")
				.defineAsCenter('X')
				.define('1', Ingredient.of(shard(EssenceType.FIRE)))
				.define('2', Ingredient.of(shard(EssenceType.DEATH)))
				.unlockedBy("fuck", has(shard(EssenceType.FIRE)))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/faint_affliction"));
		new ShapedSigilEngravingRecipeBuilder(Sigils.FAINT_ENDURANCE.get())
				.pattern("111")
				.pattern(" X ")
				.pattern("111")
				.defineAsCenter('X')
				.define('1', Ingredient.of(shard(EssenceType.METAL)))
				.unlockedBy("fuck", has(shard(EssenceType.METAL)))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/faint_endurance"));
		new ShapedSigilEngravingRecipeBuilder(Sigils.FAINT_HEART.get())
				.pattern("1 1")
				.pattern("1X1")
				.pattern(" 1 ")
				.defineAsCenter('X')
				.define('1', Ingredient.of(shard(EssenceType.BLOOD)))
				.unlockedBy("fuck", has(shard(EssenceType.BLOOD)))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/faint_heart"));
		new ShapedSigilEngravingRecipeBuilder(Sigils.MARK_OF_AFFLICTION.get())
				.pattern(" 1 1 ")
				.pattern("1 1 1")
				.pattern("11X11")
				.pattern("  2  ")
				.pattern("  2  ")
				.defineAsCenter('X')
				.define('1', Ingredient.of(crystal(EssenceType.FIRE)))
				.define('2', Ingredient.of(crystal(EssenceType.DEATH)))
				.unlockedBy("fuck", has(crystal(EssenceType.FIRE)))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/mark_of_affliction"));
		new ShapedSigilEngravingRecipeBuilder(Sigils.MARK_OF_ENDURANCE.get())
				.pattern("11111")
				.pattern(" 111 ")
				.pattern("  X  ")
				.pattern(" 1 1 ")
				.pattern("11111")
				.defineAsCenter('X')
				.define('1', Ingredient.of(crystal(EssenceType.METAL)))
				.unlockedBy("fuck", has(crystal(EssenceType.METAL)))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/mark_of_endurance"));
		new ShapedSigilEngravingRecipeBuilder(Sigils.MINI_HEART.get())
				.pattern(" 1 1 ")
				.pattern("1 1 1")
				.pattern("1 X 1")
				.pattern(" 1 1 ")
				.pattern("  1  ")
				.defineAsCenter('X')
				.define('1', Ingredient.of(crystal(EssenceType.BLOOD)))
				.unlockedBy("fuck", has(crystal(EssenceType.BLOOD)))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/mini_heart"));
		new ShapedSigilEngravingRecipeBuilder(Sigils.RUNIC_SHIELD.get())
				.pattern(" 1 1 1 ")
				.pattern("1 1 1 1")
				.pattern(" 1   1 ")
				.pattern("1  X  1")
				.pattern(" 1   1 ")
				.pattern("1 1 1 1")
				.pattern(" 1 1 1 ")
				.defineAsCenter('X')
				.define('1', Ingredient.of(crystal(EssenceType.EARTH)))
				.unlockedBy("fuck", has(crystal(EssenceType.EARTH)))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/sth"));
		new ShapedSigilEngravingRecipeBuilder(Sigils.SIGIL_OF_TRAVELER.get())
				.pattern("11111")
				.pattern(" 1 1 ")
				.pattern("  X  ")
				.pattern(" 111 ")
				.pattern("1 1 1")
				.defineAsCenter('X')
				.define('1', Ingredient.of(crystal(EssenceType.AIR)))
				.unlockedBy("fuck", has(crystal(EssenceType.AIR)))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/sigil_of_traveler"));
		new ShapedSigilEngravingRecipeBuilder(Sigils.FROSTBITE_RUNE.get())
				.pattern("1  1 ")
				.pattern("1 1  ")
				.pattern("11X1 ")
				.pattern(" 1 11")
				.pattern("1  1 ")
				.defineAsCenter('X')
				.define('1', Ingredient.of(crystal(EssenceType.FROST)))
				.unlockedBy("fuck", has(crystal(EssenceType.FROST)))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/frostbite_rune"));
		new ShapedSigilEngravingRecipeBuilder(Sigils.SCALD_RUNE.get())
				.pattern(" 1  1")
				.pattern("11 1 ")
				.pattern(" 1X11")
				.pattern("  1 1")
				.pattern(" 1  1")
				.defineAsCenter('X')
				.define('1', Ingredient.of(crystal(EssenceType.FIRE)))
				.unlockedBy("fuck", has(crystal(EssenceType.FIRE)))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/scald_rune"));

		new FoundryRecipeBuilder(new ItemStack(Items.NETHERITE_INGOT, 2))
				.ingredient(Ingredient.of(Tags.Items.INGOTS_GOLD), 1)
				.ingredient(Ingredient.of(Items.NETHERITE_SCRAP), 8)
				.unlockedBy("netherite_scrap", has(Items.NETHERITE_SCRAP))
				.save(consumer, new ResourceLocation(MODID, "foundry/netherite_ingot"));
		new FoundryRecipeBuilder(new ItemStack(ModItems.NETHER_STEEL_INGOT.get(), 2))
				.ingredient(Ingredient.of(Tags.Items.INGOTS_IRON), 1)
				.ingredient(Ingredient.of(Items.NETHERITE_SCRAP), 8)
				.unlockedBy("netherite_scrap", has(Items.NETHERITE_SCRAP))
				.save(consumer, new ResourceLocation(MODID, "foundry/nether_steel"));
		new FoundryRecipeBuilder(new ItemStack(ModItems.DAMASCUS_STEEL_INGOT.get()))
				.ingredient(Ingredient.of(Tags.Items.INGOTS_IRON), 1)
				.ingredient(Ingredient.of(ItemTags.COALS), 4)
				.essence(EssenceType.METAL, 9)
				.unlockedBy("iron", has(Tags.Items.INGOTS_IRON))
				.save(consumer, new ResourceLocation(MODID, "foundry/damascus_steel_ingot"));
	}

	private static Item shard(EssenceType type){
		return type.getEssenceItem();
	}
	private static Item crystal(EssenceType type){
		return type.getGreaterEssenceItem();
	}
	private static Item greaterCrystal(EssenceType type){
		return type.getExquisiteEssenceItem();
	}

	private static void compactAndUncompact(IItemProvider ingot, IItemProvider nugget, Consumer<IFinishedRecipe> consumer){
		compact(ingot, Ingredient.of(nugget), has(nugget),
				"compact_"+Objects.requireNonNull(ingot.asItem().getRegistryName()).getPath(),
				consumer);
		uncompact(nugget, Ingredient.of(ingot), has(ingot),
				"uncompact_"+Objects.requireNonNull(ingot.asItem().getRegistryName()).getPath(),
				consumer);
	}

	private static void compact(IItemProvider result, Ingredient ingredient, ICriterionInstance criterion, String id, Consumer<IFinishedRecipe> consumer){
		ShapedRecipeBuilder.shaped(result)
				.pattern("111")
				.pattern("111")
				.pattern("111")
				.define('1', ingredient)
				.unlockedBy("has_item", criterion)
				.save(consumer, new ResourceLocation(MODID, id));
	}
	private static void uncompact(IItemProvider result, Ingredient ingredient, ICriterionInstance criterion, String id, Consumer<IFinishedRecipe> consumer){
		ShapelessRecipeBuilder.shapeless(result, 9)
				.requires(ingredient)
				.unlockedBy("has_item", criterion)
				.save(consumer, new ResourceLocation(MODID, id));
	}
}
