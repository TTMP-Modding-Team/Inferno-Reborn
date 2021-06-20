package ttmp.infernoreborn.contents;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.contents.item.BerserkerArmorItem;
import ttmp.infernoreborn.contents.item.CrimsonArmorItem;
import ttmp.infernoreborn.contents.item.CrimsonClaymoreItem;
import ttmp.infernoreborn.contents.item.DragonSlayerItem;
import ttmp.infernoreborn.contents.item.EssenceHolderItem;
import ttmp.infernoreborn.contents.item.ExplosiveSwordItem;
import ttmp.infernoreborn.contents.item.FixedAbilityItem;
import ttmp.infernoreborn.contents.item.GeneratorAbilityItem;
import ttmp.infernoreborn.contents.item.RandomAbilityItem;
import ttmp.infernoreborn.contents.item.SigilItem;
import ttmp.infernoreborn.contents.item.TheBookItem;

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
	public static final ItemGroup MATERIALS = new ItemGroup("infernoreborn.materials"){
		@Override public ItemStack makeIcon(){
			return new ItemStack(GREATER_METAL_ESSENCE_CRYSTAL.get());
		}
	};
	public static final ItemGroup SIGILS = new ItemGroup("infernoreborn.sigils"){
		@Override public ItemStack makeIcon(){
			return new ItemStack(TEST_SIGIL.get());
		}
	};

	private static Item.Properties sparks(){
		return new Item.Properties().tab(SPARKS);
	}
	private static Item.Properties artifacts(){
		return new Item.Properties().tab(ARTIFACTS);
	}
	private static Item.Properties materials(){
		return new Item.Properties().tab(MATERIALS);
	}
	private static Item.Properties sigils(){
		return new Item.Properties().tab(SIGILS);
	}
	private static Item.Properties sparks(Rarity rarity){
		return sparks().rarity(rarity);
	}
	private static Item.Properties artifacts(Rarity rarity){
		return artifacts().rarity(rarity);
	}
	private static Item.Properties materials(Rarity rarity){
		return materials().rarity(rarity);
	}
	private static Item.Properties sigils(Rarity rarity){
		return sigils().rarity(rarity);
	}

	public static final RegistryObject<Item> PRIMAL_INFERNO_SPARK = REGISTER.register("primal_inferno_spark", () -> new RandomAbilityItem(sparks(Rarity.EPIC)));
	public static final RegistryObject<Item> GENERATOR_INFERNO_SPARK = REGISTER.register("generator_inferno_spark", () -> new GeneratorAbilityItem(sparks(Rarity.RARE)));
	public static final RegistryObject<Item> INFERNO_SPARK = REGISTER.register("inferno_spark", () -> new FixedAbilityItem(sparks(Rarity.RARE)));

	public static final RegistryObject<Item> BOOK_OF_THE_UNSPEAKABLE = REGISTER.register("book_of_the_unspeakable", () -> new TheBookItem(artifacts().stacksTo(1)));

	public static final RegistryObject<Item> ESSENCE_HOLDER = REGISTER.register("essence_holder", () -> new EssenceHolderItem(artifacts(Rarity.UNCOMMON).stacksTo(1)));

	public static final RegistryObject<Item> EXPLOSIVE_SWORD = REGISTER.register("explosive_sword", () -> new ExplosiveSwordItem(artifacts(Rarity.RARE)));

	public static final RegistryObject<Item> CRIMSON_CLAYMORE = REGISTER.register("crimson_claymore", () -> new CrimsonClaymoreItem(artifacts(Rarity.RARE)));
	public static final RegistryObject<Item> CRIMSON_CHESTPLATE = REGISTER.register("crimson_chestplate", () -> new CrimsonArmorItem(EquipmentSlotType.CHEST, artifacts(Rarity.RARE)));
	public static final RegistryObject<Item> CRIMSON_LEGGINGS = REGISTER.register("crimson_leggings", () -> new CrimsonArmorItem(EquipmentSlotType.LEGS, artifacts(Rarity.RARE)));
	public static final RegistryObject<Item> CRIMSON_BOOTS = REGISTER.register("crimson_boots", () -> new CrimsonArmorItem(EquipmentSlotType.FEET, artifacts(Rarity.RARE)));

	public static final RegistryObject<Item> DRAGON_SLAYER = REGISTER.register("dragon_slayer", () -> new DragonSlayerItem(artifacts().rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> BERSERKER_HELMET = REGISTER.register("berserker_helmet", () -> new BerserkerArmorItem(EquipmentSlotType.HEAD, artifacts(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> BERSERKER_CHESTPLATE = REGISTER.register("berserker_chestplate", () -> new BerserkerArmorItem(EquipmentSlotType.CHEST, artifacts(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> BERSERKER_LEGGINGS = REGISTER.register("berserker_leggings", () -> new BerserkerArmorItem(EquipmentSlotType.LEGS, artifacts(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> BERSERKER_BOOTS = REGISTER.register("berserker_boots", () -> new BerserkerArmorItem(EquipmentSlotType.FEET, artifacts(Rarity.UNCOMMON)));

	public static final RegistryObject<BlockItem> SIGIL_ENGRAVING_TABLE_3X3 = REGISTER.register("sigil_engraving_table_3x3", () -> new BlockItem(ModBlocks.SIGIL_ENGRAVING_TABLE_3X3.get(), artifacts()));
	public static final RegistryObject<BlockItem> SIGIL_ENGRAVING_TABLE_5X5 = REGISTER.register("sigil_engraving_table_5x5", () -> new BlockItem(ModBlocks.SIGIL_ENGRAVING_TABLE_5X5.get(), artifacts()));
	public static final RegistryObject<BlockItem> SIGIL_ENGRAVING_TABLE_7X7 = REGISTER.register("sigil_engraving_table_7x7", () -> new BlockItem(ModBlocks.SIGIL_ENGRAVING_TABLE_7X7.get(), artifacts()));

	public static final RegistryObject<Item> BLOOD_ESSENCE_SHARD = REGISTER.register("blood_essence_shard", () -> new Item(materials(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> BLOOD_ESSENCE_CRYSTAL = REGISTER.register("blood_essence_crystal", () -> new Item(materials(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> GREATER_BLOOD_ESSENCE_CRYSTAL = REGISTER.register("greater_blood_essence_crystal", () -> new Item(materials(Rarity.UNCOMMON)));

	public static final RegistryObject<Item> METAL_ESSENCE_SHARD = REGISTER.register("metal_essence_shard", () -> new Item(materials(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> METAL_ESSENCE_CRYSTAL = REGISTER.register("metal_essence_crystal", () -> new Item(materials(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> GREATER_METAL_ESSENCE_CRYSTAL = REGISTER.register("greater_metal_essence_crystal", () -> new Item(materials(Rarity.UNCOMMON)));

	public static final RegistryObject<Item> FROST_ESSENCE_SHARD = REGISTER.register("frost_essence_shard", () -> new Item(materials(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> FROST_ESSENCE_CRYSTAL = REGISTER.register("frost_essence_crystal", () -> new Item(materials(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> GREATER_FROST_ESSENCE_CRYSTAL = REGISTER.register("greater_frost_essence_crystal", () -> new Item(materials(Rarity.UNCOMMON)));

	public static final RegistryObject<Item> EARTH_ESSENCE_SHARD = REGISTER.register("earth_essence_shard", () -> new Item(materials(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> EARTH_ESSENCE_CRYSTAL = REGISTER.register("earth_essence_crystal", () -> new Item(materials(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> GREATER_EARTH_ESSENCE_CRYSTAL = REGISTER.register("greater_earth_essence_crystal", () -> new Item(materials(Rarity.UNCOMMON)));

	public static final RegistryObject<Item> CRIMSON_METAL_SCRAP = REGISTER.register("crimson_metal_scrap", () -> new Item(materials(Rarity.RARE)));
	public static final RegistryObject<Item> DAMASCUS_STEEL_INGOT = REGISTER.register("damascus_steel_ingot", () -> new Item(materials(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> DAMASCUS_STEEL_NUGGET = REGISTER.register("damascus_steel_nugget", () -> new Item(materials(Rarity.UNCOMMON)));
	public static final RegistryObject<BlockItem> DAMASCUS_STEEL_BLOCK = REGISTER.register("damascus_steel_block", () -> new BlockItem(ModBlocks.DAMASCUS_STEEL_BLOCK.get(), materials(Rarity.UNCOMMON)));

	public static final RegistryObject<Item> TEST_SIGIL = REGISTER.register("test_sigil", () -> new SigilItem(Sigils.TEST, sigils()));
	public static final RegistryObject<Item> TEST_SIGIL_2 = REGISTER.register("test_sigil_2", () -> new SigilItem(Sigils.TEST2, sigils()));
	public static final RegistryObject<Item> TEST_SIGIL_3 = REGISTER.register("test_sigil_3", () -> new SigilItem(Sigils.TEST2, sigils()));
}
