package ttmp.infernoreborn.contents;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.item.EssenceHolderItem;
import ttmp.infernoreborn.item.ExplosiveSwordItem;
import ttmp.infernoreborn.item.FixedAbilityItem;
import ttmp.infernoreborn.item.GeneratorAbilityItem;
import ttmp.infernoreborn.item.RandomAbilityItem;
import ttmp.infernoreborn.item.SigilItem;
import ttmp.infernoreborn.item.TheBookItem;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class ModItems{
	private ModItems(){}

	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

	public static final ItemGroup SPARKS = new ItemGroup("infernoreborn.sparks"){
		@Override public ItemStack makeIcon(){
			return new ItemStack(PRIMAL_INFERNO_SPARK.get());
		}
	};
	public static final ItemGroup ARTIFACTS = new ItemGroup("infernoreborn.artifacts"){
		@Override public ItemStack makeIcon(){
			ItemStack stack = new ItemStack(BOOK_OF_THE_UNSPEAKABLE.get());
			TheBookItem.setHasEssenceHolder(stack, true);
			return stack;
		}
	};
	public static final ItemGroup SIGILS = new ItemGroup("infernoreborn.sigils"){
		@Override public ItemStack makeIcon(){
			return new ItemStack(TEST_SIGIL.get());
		}
	};

	public static final RegistryObject<Item> PRIMAL_INFERNO_SPARK = REGISTER.register("primal_inferno_spark", () -> new RandomAbilityItem(new Item.Properties().tab(SPARKS).rarity(Rarity.EPIC)));
	public static final RegistryObject<Item> GENERATOR_INFERNO_SPARK = REGISTER.register("generator_inferno_spark", () -> new GeneratorAbilityItem(new Item.Properties().tab(SPARKS).rarity(Rarity.RARE)));
	public static final RegistryObject<Item> INFERNO_SPARK = REGISTER.register("inferno_spark", () -> new FixedAbilityItem(new Item.Properties().tab(SPARKS).rarity(Rarity.RARE)));

	public static final RegistryObject<Item> BOOK_OF_THE_UNSPEAKABLE = REGISTER.register("book_of_the_unspeakable", () -> new TheBookItem(new Item.Properties().stacksTo(1).tab(ARTIFACTS)));

	public static final RegistryObject<Item> BLOOD_ESSENCE_SHARD = REGISTER.register("blood_essence_shard", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> BLOOD_ESSENCE_CRYSTAL = REGISTER.register("blood_essence_crystal", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> GREATER_BLOOD_ESSENCE_CRYSTAL = REGISTER.register("greater_blood_essence_crystal", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));

	public static final RegistryObject<Item> METAL_ESSENCE_SHARD = REGISTER.register("metal_essence_shard", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> METAL_ESSENCE_CRYSTAL = REGISTER.register("metal_essence_crystal", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> GREATER_METAL_ESSENCE_CRYSTAL = REGISTER.register("greater_metal_essence_crystal", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));

	public static final RegistryObject<Item> FROST_ESSENCE_SHARD = REGISTER.register("frost_essence_shard", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> FROST_ESSENCE_CRYSTAL = REGISTER.register("frost_essence_crystal", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> GREATER_FROST_ESSENCE_CRYSTAL = REGISTER.register("greater_frost_essence_crystal", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));

	public static final RegistryObject<Item> EARTH_ESSENCE_SHARD = REGISTER.register("earth_essence_shard", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> EARTH_ESSENCE_CRYSTAL = REGISTER.register("earth_essence_crystal", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> GREATER_EARTH_ESSENCE_CRYSTAL = REGISTER.register("greater_earth_essence_crystal", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));

	public static final RegistryObject<Item> ESSENCE_HOLDER = REGISTER.register("essence_holder", () -> new EssenceHolderItem(new Item.Properties().stacksTo(1).tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));

	public static final RegistryObject<Item> EXPLOSIVE_SWORD = REGISTER.register("explosive_sword", () -> new ExplosiveSwordItem(new Item.Properties().stacksTo(1).tab(ARTIFACTS).rarity(Rarity.RARE)));

	public static final RegistryObject<Item> TEST_SIGIL = REGISTER.register("test_sigil", () -> new SigilItem(Sigils.TEST, new Item.Properties().tab(SIGILS)));
	public static final RegistryObject<Item> TEST_SIGIL_2 = REGISTER.register("test_sigil_2", () -> new SigilItem(Sigils.TEST2, new Item.Properties().tab(SIGILS)));
	public static final RegistryObject<Item> TEST_SIGIL_3 = REGISTER.register("test_sigil_3", () -> new SigilItem(Sigils.TEST2, new Item.Properties().tab(SIGILS)));

	public static final RegistryObject<BlockItem> SIGIL_ENGRAVING_TABLE_3X3 = REGISTER.register("sigil_engraving_table_3x3", () -> new BlockItem(ModBlocks.SIGIL_ENGRAVING_TABLE_3X3.get(), new Item.Properties().tab(ARTIFACTS)));
	public static final RegistryObject<BlockItem> SIGIL_ENGRAVING_TABLE_5X5 = REGISTER.register("sigil_engraving_table_5x5", () -> new BlockItem(ModBlocks.SIGIL_ENGRAVING_TABLE_5X5.get(), new Item.Properties().tab(ARTIFACTS)));
	public static final RegistryObject<BlockItem> SIGIL_ENGRAVING_TABLE_7X7 = REGISTER.register("sigil_engraving_table_7x7", () -> new BlockItem(ModBlocks.SIGIL_ENGRAVING_TABLE_7X7.get(), new Item.Properties().tab(ARTIFACTS)));
}
