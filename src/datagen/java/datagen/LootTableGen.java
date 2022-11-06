package datagen;

import com.mojang.datafixers.util.Pair;
import datagen.builder.FuckingLootEntryBuilder;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.MatchTool;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import ttmp.infernoreborn.contents.ModBlocks;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.item.EssenceNetBlockItem;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.minecraft.loot.ItemLootEntry.lootTableItem;
import static net.minecraft.loot.LootPool.lootPool;
import static net.minecraft.loot.LootTable.lootTable;
import static net.minecraft.loot.RandomValueRange.between;

public class LootTableGen extends LootTableProvider{
	private static final ILootCondition.IBuilder HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item()
			.hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))));
	public LootTableGen(DataGenerator generator){
		super(generator);
	}

	@Override protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables(){
		return Collections.singletonList(Pair.of(BlockTables::new, LootParameterSets.BLOCK));
	}

	@Override protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker){}


	public static class BlockTables extends BlockLootTables{
		@Override protected void addTables(){
			add(ModBlocks.HEART_CRYSTAL_ORE.get(), createSingleItemTableWithSilkTouch(ModBlocks.HEART_CRYSTAL_ORE.get(), ModItems.HEART_CRYSTAL.get()));
			add(ModBlocks.PYRITE_ORE.get(), createSingleItemTableWithSilkTouch(ModBlocks.PYRITE_ORE.get(), ModItems.PYRITE_NUGGET.get(), between(2, 6)));

			dropSelf(ModBlocks.PYRITE_BLOCK.get());
			dropSelf(ModBlocks.NETHER_STEEL_BLOCK.get());
			dropSelf(ModBlocks.DAMASCUS_STEEL_BLOCK.get());

			dropSelf(ModBlocks.RUNESTONE.get());

			dropSelf(ModBlocks.SIGIL_ENGRAVING_TABLE_3X3.get());
			dropSelf(ModBlocks.SIGIL_ENGRAVING_TABLE_5X5.get());
			dropSelf(ModBlocks.SIGIL_ENGRAVING_TABLE_7X7.get());
			dropSelf(ModBlocks.SIGIL_SCRAPPER.get());
			dropSelf(ModBlocks.STIGMA_SCRAPPER.get());

			dropSelf(ModBlocks.STIGMA_TABLE_5X5.get());
			dropSelf(ModBlocks.STIGMA_TABLE_7X7.get());

			dropSelf(ModBlocks.FOUNDRY_TILE.get());
			add(ModBlocks.FOUNDRY.get(), b -> createNameableBlockEntityTable(ModBlocks.FOUNDRY.get()));

			dropSelf(ModBlocks.CRUCIBLE.get());
			add(ModBlocks.CRUCIBLE_CAMPFIRE.get(), b -> lootTable().withPool(applyExplosionCondition(b, lootPool()
					.setRolls(ConstantRange.exactly(1))
					.add(new FuckingLootEntryBuilder(
							lootTableItem(Items.CAMPFIRE)
									.when(HAS_SILK_TOUCH)
									.otherwise(lootTableItem(Items.CHARCOAL)
											.apply(SetCount.setCount(ConstantRange.exactly(2)))),
							lootTableItem(ModBlocks.CRUCIBLE.get())
					)))
			));

			dropSelf(ModBlocks.CRUCIBLE_AUTOMATION_UNIT.get());

			dropSelf(ModBlocks.FURNACE_STOVE.get());
			dropSelf(ModBlocks.FOUNDRY_STOVE.get());
			dropSelf(ModBlocks.NETHER_STOVE.get());
			dropSelf(ModBlocks.ESSENCE_STOVE.get());

			addNbtCopiedDrop(ModBlocks.ESSENCE_HOLDER_BLOCK.get(), "Essence");

			addEssenceNetworkBlockDrop(ModBlocks.ESSENCE_NET_CORE.get());
			addEssenceNetworkBlockDrop(ModBlocks.ESSENCE_NET_IMPORTER.get());
			addEssenceNetworkBlockDrop(ModBlocks.ESSENCE_NET_EXPORTER.get());

			add(ModBlocks.GOLDEN_SKULL.get(), b -> createNameableBlockEntityTable(ModBlocks.GOLDEN_SKULL.get()));
		}

		private void addEssenceNetworkBlockDrop(Block block){
			addNbtCopiedDrop(block, EssenceNetBlockItem.DEFAULT_NETWORK_ID_KEY);
		}

		private void addNbtCopiedDrop(Block block, String... keys){
			CopyNbt.Builder copyData = CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY);
			for(String key : keys) copyData.copy(key, "BlockEntityTag."+key);
			add(block, lootTable().withPool(
					applyExplosionCondition(block, lootPool()
							.setRolls(ConstantRange.exactly(1))
							.add(lootTableItem(block)
									.apply(copyData)))));
		}

		@Override protected Iterable<Block> getKnownBlocks(){
			return ModBlocks.REGISTER.getEntries().stream().map(RegistryObject::get).collect(Collectors.toList());
		}
	}
}
