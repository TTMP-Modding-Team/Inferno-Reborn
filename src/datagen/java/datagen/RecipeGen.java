package datagen;

import datagen.builder.CrucibleRecipeBuilder;
import datagen.builder.FoundryRecipeBuilder;
import datagen.builder.NotSoSpecialRecipeBuilder;
import datagen.builder.ShapedSigilEngravingRecipeBuilder;
import datagen.builder.ShapedSigilTableCraftingRecipeBuilder;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import ttmp.infernoreborn.api.essence.EssenceSize;
import ttmp.infernoreborn.api.essence.EssenceType;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.ModRecipes;
import ttmp.infernoreborn.contents.ModTags;
import ttmp.infernoreborn.contents.Sigils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

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

		ShapelessRecipeBuilder.shapeless(ModItems.BOOK_OF_THE_UNSPEAKABLE.get())
				.requires(Items.PAPER)
				.requires(ModTags.ESSENCES)
				.unlockedBy("fuck", has(ModTags.ESSENCES))
				.save(consumer);
		ShapedRecipeBuilder.shaped(ModItems.ESSENCE_HOLDER.get())
				.pattern(" 1 ")
				.pattern("111")
				.pattern(" 1 ")
				.define('1', ModTags.ESSENCES)
				.unlockedBy("fuck", has(ModTags.ESSENCES))
				.save(consumer);
		ShapedRecipeBuilder.shaped(ModItems.ESSENCE_HOLDER_BLOCK.get())
				.pattern("1")
				.pattern("2")
				.define('1', ModItems.ESSENCE_HOLDER.get())
				.define('2', Tags.Items.INGOTS_GOLD)
				.unlockedBy("fuck", has(ModItems.ESSENCE_HOLDER.get()))
				.save(consumer);
		ShapedRecipeBuilder.shaped(ModItems.FOUNDRY.get())
				.pattern("111")
				.pattern("121")
				.pattern("111")
				.define('1', ModItems.FOUNDRY_TILE.get())
				.define('2', Items.BLAST_FURNACE)
				.unlockedBy("fuck", has(ModTags.ESSENCES))
				.save(consumer);
		ShapedRecipeBuilder.shaped(ModItems.SIGIL_ENGRAVING_TABLE_3X3.get())
				.pattern(" 1 ")
				.pattern("232")
				.pattern("444")
				.define('1', ModTags.GREATER_ESSENCES)
				.define('2', Tags.Items.INGOTS_IRON)
				.define('3', Tags.Items.INGOTS_GOLD)
				.define('4', ItemTags.PLANKS)
				.unlockedBy("fuck", has(ModTags.ESSENCES))
				.save(consumer);
		ShapedRecipeBuilder.shaped(ModItems.CRUCIBLE.get())
				.pattern("1 1")
				.pattern("212")
				.define('1', Tags.Items.INGOTS_IRON)
				.define('2', ModTags.INGOTS_PYRITE)
				.unlockedBy("fuck", has(ModTags.INGOTS_PYRITE))
				.save(consumer);

		new ShapedSigilTableCraftingRecipeBuilder(ModItems.RUNESTONE.get())
				.pattern("1")
				.pattern("X")
				.defineAsCenter('X', Ingredient.of(Tags.Items.STONE))
				.define('1', Ingredient.of(ModTags.ESSENCES))
				.save(consumer, new ResourceLocation(MODID, "sigilcraft/runestone"));
		new ShapedSigilTableCraftingRecipeBuilder(ModItems.FOUNDRY_TILE.get())
				.pattern("1")
				.pattern("X")
				.defineAsCenter('X', Ingredient.of(Items.BRICKS))
				.define('1', Ingredient.of(ModTags.ESSENCES))
				.save(consumer, new ResourceLocation(MODID, "sigilcraft/foundry_tile"));
		new ShapedSigilTableCraftingRecipeBuilder(ModItems.SIGIL_ENGRAVING_TABLE_5X5.get())
				.pattern("121")
				.pattern("2X2")
				.pattern("121")
				.defineAsCenter('X', Ingredient.of(ModItems.SIGIL_ENGRAVING_TABLE_3X3.get()))
				.define('1', Ingredient.of(ModTags.INGOTS_PYRITE))
				.define('2', Ingredient.of(ModItems.RUNESTONE.get()))
				.save(consumer, new ResourceLocation(MODID, "sigilcraft/sigil_engraving_table_5x5"));
		new ShapedSigilTableCraftingRecipeBuilder(Items.PUFFERFISH)
				.pattern("  111  ")
				.pattern("3144413")
				.pattern("344X443")
				.pattern(" 24442 ")
				.pattern("  222  ")
				.defineAsCenter('X', Ingredient.of(Items.COBBLESTONE))
				.define('1', Ingredient.of(exquisite(EssenceType.DOMINANCE)))
				.define('2', Ingredient.of(exquisite(EssenceType.AIR)))
				.define('3', Ingredient.of(exquisite(EssenceType.WATER)))
				.define('4', Ingredient.of(exquisite(EssenceType.BLOOD)))
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
				.define('1', Ingredient.of(normal(EssenceType.AIR)))
				.define('2', Ingredient.of(normal(EssenceType.DOMINANCE)))
				.unlockedBy("fuck", has(normal(EssenceType.DOMINANCE)))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/goat_eyes"));
		new ShapedSigilEngravingRecipeBuilder(Sigils.FAINT_AFFLICTION.get())
				.pattern("11 ")
				.pattern("1X1")
				.pattern(" 2 ")
				.defineAsCenter('X')
				.define('1', Ingredient.of(normal(EssenceType.FIRE)))
				.define('2', Ingredient.of(normal(EssenceType.DEATH)))
				.unlockedBy("fuck", has(normal(EssenceType.FIRE)))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/faint_affliction"));
		new ShapedSigilEngravingRecipeBuilder(Sigils.FAINT_ENDURANCE.get())
				.pattern("111")
				.pattern(" X ")
				.pattern("111")
				.defineAsCenter('X')
				.define('1', Ingredient.of(normal(EssenceType.METAL)))
				.unlockedBy("fuck", has(normal(EssenceType.METAL)))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/faint_endurance"));
		new ShapedSigilEngravingRecipeBuilder(Sigils.FAINT_HEART.get())
				.pattern("1 1")
				.pattern("1X1")
				.pattern(" 1 ")
				.defineAsCenter('X')
				.define('1', Ingredient.of(normal(EssenceType.BLOOD)))
				.unlockedBy("fuck", has(normal(EssenceType.BLOOD)))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/faint_heart"));
		new ShapedSigilEngravingRecipeBuilder(Sigils.MARK_OF_AFFLICTION.get())
				.pattern(" 1 1 ")
				.pattern("1 1 1")
				.pattern("11X11")
				.pattern("  2  ")
				.pattern("  2  ")
				.defineAsCenter('X')
				.define('1', Ingredient.of(greater(EssenceType.FIRE)))
				.define('2', Ingredient.of(greater(EssenceType.DEATH)))
				.unlockedBy("fuck", has(greater(EssenceType.FIRE)))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/mark_of_affliction"));
		new ShapedSigilEngravingRecipeBuilder(Sigils.MARK_OF_ENDURANCE.get())
				.pattern("11111")
				.pattern(" 111 ")
				.pattern("  X  ")
				.pattern(" 1 1 ")
				.pattern("11111")
				.defineAsCenter('X')
				.define('1', Ingredient.of(greater(EssenceType.METAL)))
				.unlockedBy("fuck", has(greater(EssenceType.METAL)))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/mark_of_endurance"));
		new ShapedSigilEngravingRecipeBuilder(Sigils.MINI_HEART.get())
				.pattern(" 1 1 ")
				.pattern("1 1 1")
				.pattern("1 X 1")
				.pattern(" 1 1 ")
				.pattern("  1  ")
				.defineAsCenter('X')
				.define('1', Ingredient.of(greater(EssenceType.BLOOD)))
				.unlockedBy("fuck", has(greater(EssenceType.BLOOD)))
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
				.define('1', Ingredient.of(greater(EssenceType.EARTH)))
				.unlockedBy("fuck", has(greater(EssenceType.EARTH)))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/sth"));
		new ShapedSigilEngravingRecipeBuilder(Sigils.SIGIL_OF_TRAVELER.get())
				.pattern("11111")
				.pattern(" 1 1 ")
				.pattern("  X  ")
				.pattern(" 111 ")
				.pattern("1 1 1")
				.defineAsCenter('X')
				.define('1', Ingredient.of(greater(EssenceType.AIR)))
				.unlockedBy("fuck", has(greater(EssenceType.AIR)))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/sigil_of_traveler"));
		new ShapedSigilEngravingRecipeBuilder(Sigils.FROSTBITE_RUNE.get())
				.pattern("1  1 ")
				.pattern("1 1  ")
				.pattern("11X1 ")
				.pattern(" 1 11")
				.pattern("1  1 ")
				.defineAsCenter('X')
				.define('1', Ingredient.of(greater(EssenceType.FROST)))
				.unlockedBy("fuck", has(greater(EssenceType.FROST)))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/frostbite_rune"));
		new ShapedSigilEngravingRecipeBuilder(Sigils.SCALD_RUNE.get())
				.pattern(" 1  1")
				.pattern("11 1 ")
				.pattern(" 1X11")
				.pattern("  1 1")
				.pattern(" 1  1")
				.defineAsCenter('X')
				.define('1', Ingredient.of(greater(EssenceType.FIRE)))
				.unlockedBy("fuck", has(greater(EssenceType.FIRE)))
				.save(consumer, new ResourceLocation(MODID, "sigil_engraving/scald_rune"));

		new CrucibleRecipeBuilder()
				.output(ModItems.RUNESTONE.get())
				.ingredient(Ingredient.of(Tags.Items.STONE), 1)
				.water()
				.anyEssence(1)
				.stirTime(40)
				.save(consumer, new ResourceLocation(MODID, "crucible/runestone"));

		new CrucibleRecipeBuilder()
				.output(ModItems.FOUNDRY_TILE.get())
				.ingredient(Ingredient.of(Items.BRICKS), 1)
				.ingredient(Ingredient.of(Items.COAL, Items.CHARCOAL), 1)
				.water()
				.anyEssence(4)
				.stirTime(40)
				.save(consumer, new ResourceLocation(MODID, "crucible/foundry_tile"));

		new CrucibleRecipeBuilder()
				.output(Items.DIAMOND, 64)
				.ingredient(Ingredient.of(Items.PUFFERFISH_BUCKET), 1)
				.ingredient(FluidTags.LAVA, 250)
				.essence(EssenceType.DEATH, 9)
				.stirTime(200)
				.save(consumer, new ResourceLocation(MODID, "crucible/idk"));

		new CrucibleRecipeBuilder()
				.output(Fluids.WATER, 1000)
				.ingredient(Ingredient.of(Items.PUFFERFISH), 1)
				.stirTime(20)
				.save(consumer, new ResourceLocation(MODID, "crucible/idk2"));

		new FoundryRecipeBuilder(new ItemStack(Items.NETHERITE_INGOT, 2))
				.ingredient(Ingredient.of(Tags.Items.INGOTS_GOLD), 1)
				.ingredient(Ingredient.of(Items.NETHERITE_SCRAP), 8)
				.save(consumer, new ResourceLocation(MODID, "foundry/netherite_ingot"));
		new FoundryRecipeBuilder(new ItemStack(ModItems.NETHER_STEEL_INGOT.get(), 2))
				.ingredient(Ingredient.of(Tags.Items.INGOTS_IRON), 1)
				.ingredient(Ingredient.of(Items.NETHERITE_SCRAP), 8)
				.save(consumer, new ResourceLocation(MODID, "foundry/nether_steel"));
		new FoundryRecipeBuilder(new ItemStack(ModItems.DAMASCUS_STEEL_INGOT.get()))
				.ingredient(Ingredient.of(Tags.Items.INGOTS_IRON), 1)
				.ingredient(Ingredient.of(ItemTags.COALS), 4)
				.essence(EssenceType.METAL, 9)
				.save(consumer, new ResourceLocation(MODID, "foundry/damascus_steel_ingot"));

		CookingRecipeBuilder.smelting(Ingredient.of(ModTags.ORES_PYRITE), ModItems.PYRITE_INGOT.get(), 1, 200)
				.unlockedBy("has_pyrite_ore", has(ModTags.ORES_PYRITE))
				.save(consumer, "smelting/pyrite");
		CookingRecipeBuilder.blasting(Ingredient.of(ModTags.ORES_PYRITE), ModItems.PYRITE_INGOT.get(), 1, 100)
				.unlockedBy("has_pyrite_ore", has(ModTags.ORES_PYRITE))
				.save(consumer, "blasting/pyrite");

		// buildTestSigilcraftRecipe(consumer);
	}

	private static void buildTestSigilcraftRecipe(Consumer<IFinishedRecipe> consumer){
		for(int x = 1; x<=7; x++){
			for(int y = 1; y<=7; y++){
				for(int xc = Math.max(1, x-3), xe = Math.min(x, 4); xc<=xe; xc++){
					for(int yc = Math.max(1, y-3), ye = Math.min(y, 4); yc<=ye; yc++){
						List<String> strs = new ArrayList<>();
						for(int i = 1; i<=y; i++){
							StringBuilder stb = new StringBuilder();
							for(int j = 1; j<=x; j++){
								if(xc==j&&yc==i){
									stb.append('X');
								}else stb.append('1');
							}
							strs.add(stb.toString());
						}

						ShapedSigilTableCraftingRecipeBuilder b = new ShapedSigilTableCraftingRecipeBuilder(Items.PUFFERFISH);
						for(String s : strs) b.pattern(s);

						b.defineAsCenter('X', Ingredient.of(Items.BEDROCK));
						if(x*y>1) b.define('1', Ingredient.of(Items.GOLD_INGOT));
						b.save(consumer, new ResourceLocation(MODID, "sigilcraft/test/"+x+y+"_"+xc+yc));

						if(x*y>1){
							ShapedSigilEngravingRecipeBuilder b2 = new ShapedSigilEngravingRecipeBuilder(Sigils.GOAT_EYES.get());
							for(String s : strs) b2.pattern(s);

							b2.defineAsCenter('X');
							b2.define('1', Ingredient.of(Items.BEDROCK));
							b2.save(consumer, new ResourceLocation(MODID, "sigil_engraving/test/"+x+y+"_"+xc+yc));
						}
					}
				}
			}
		}
	}

	private static Item normal(EssenceType type){
		return type.getEssenceItem();
	}
	private static Item greater(EssenceType type){
		return type.getGreaterEssenceItem();
	}
	private static Item exquisite(EssenceType type){
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
