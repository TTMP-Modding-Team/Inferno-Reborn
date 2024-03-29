package ttmp.infernoreborn.contents;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.WallOrFloorItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.api.essence.EssenceSize;
import ttmp.infernoreborn.api.essence.EssenceType;
import ttmp.infernoreborn.api.shield.SimpleShield;
import ttmp.infernoreborn.client.render.FoundryISTER;
import ttmp.infernoreborn.client.render.GoldenSkullISTER;
import ttmp.infernoreborn.client.render.SigilIconRenderer;
import ttmp.infernoreborn.contents.item.AutomationModuleBlockItem;
import ttmp.infernoreborn.contents.item.BodySigilItem;
import ttmp.infernoreborn.contents.item.CrucibleAutomationUnitBlockItem;
import ttmp.infernoreborn.contents.item.CrucibleBlockItem;
import ttmp.infernoreborn.contents.item.CylinderTestItem;
import ttmp.infernoreborn.contents.item.EssenceHolderBookItem;
import ttmp.infernoreborn.contents.item.EssenceHolderItem;
import ttmp.infernoreborn.contents.item.EssenceNetAccessorItem;
import ttmp.infernoreborn.contents.item.EssenceNetBlockItem;
import ttmp.infernoreborn.contents.item.FoundryBlockItem;
import ttmp.infernoreborn.contents.item.HeartCrystalItem;
import ttmp.infernoreborn.contents.item.JudgementItem;
import ttmp.infernoreborn.contents.item.SigilItem;
import ttmp.infernoreborn.contents.item.TheBookItem;
import ttmp.infernoreborn.contents.item.ability.AbilityColorPickerItem;
import ttmp.infernoreborn.contents.item.ability.FixedAbilityItem;
import ttmp.infernoreborn.contents.item.ability.GeneratorAbilityItem;
import ttmp.infernoreborn.contents.item.ability.RandomAbilityItem;
import ttmp.infernoreborn.contents.item.armor.BerserkerArmorItem;
import ttmp.infernoreborn.contents.item.armor.CrimsonArmorItem;
import ttmp.infernoreborn.contents.item.armor.TerrastoneArmorItem;
import ttmp.infernoreborn.contents.item.armor.ThanatosHeavyArmorItem;
import ttmp.infernoreborn.contents.item.armor.ThanatosLightArmorItem;
import ttmp.infernoreborn.contents.item.curio.BaseCurioItem;
import ttmp.infernoreborn.contents.item.curio.CloudScarfItem;
import ttmp.infernoreborn.contents.item.curio.ShieldProviderItem;
import ttmp.infernoreborn.contents.item.weapon.CrimsonClaymoreItem;
import ttmp.infernoreborn.contents.item.weapon.DragonSlayerItem;
import ttmp.infernoreborn.contents.item.weapon.ExplosiveSwordItem;
import ttmp.infernoreborn.shield.ShieldSkins;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

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
			return new ItemStack(BOOK_OF_THE_UNSPEAKABLE_COMBINED.get());
		}
	};
	public static final ItemGroup MATERIALS = new ItemGroup("infernoreborn.materials"){
		@Override public ItemStack makeIcon(){
			return new ItemStack(GREATER_METAL_ESSENCE_CRYSTAL.get());
		}
	};
	public static final ItemGroup SIGILS = new ItemGroup("infernoreborn.sigils"){
		@Override public ItemStack makeIcon(){
			return new ItemStack(SIGIL.get());
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
	public static final RegistryObject<Item> GENERATOR_INFERNO_SPARK = REGISTER.register("generator_inferno_spark", () -> new GeneratorAbilityItem(new Item.Properties().rarity(Rarity.EPIC)));
	public static final RegistryObject<Item> INFERNO_SPARK = REGISTER.register("inferno_spark", () -> new FixedAbilityItem(sparks(Rarity.RARE)));

	public static final RegistryObject<Item> BOOK_OF_THE_UNSPEAKABLE = REGISTER.register("book_of_the_unspeakable", () -> new TheBookItem(artifacts().stacksTo(1)));
	public static final RegistryObject<Item> BOOK_OF_THE_UNSPEAKABLE_COMBINED = REGISTER.register("book_of_the_unspeakable_combined", () -> new EssenceHolderBookItem(artifacts().stacksTo(1)));

	public static final RegistryObject<Item> ESSENCE_HOLDER = REGISTER.register("essence_holder", () -> new EssenceHolderItem(artifacts(Rarity.UNCOMMON).stacksTo(1)));

	public static final RegistryObject<Item> JUDGEMENT = REGISTER.register("judgement", () -> new JudgementItem(artifacts(Rarity.EPIC).stacksTo(1)));

	public static final RegistryObject<Item> HEART_CRYSTAL = REGISTER.register("heart_crystal", () -> new HeartCrystalItem(artifacts(Rarity.RARE)));

	public static final RegistryObject<Item> EXPLOSIVE_SWORD = REGISTER.register("explosive_sword", () -> new ExplosiveSwordItem(artifacts(Rarity.RARE)));

	public static final RegistryObject<Item> TERRASTONE_HEADGEAR = REGISTER.register("terrastone_headgear", () -> new TerrastoneArmorItem(EquipmentSlotType.HEAD, artifacts(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> TERRASTONE_CHESTPLATE = REGISTER.register("terrastone_chestplate", () -> new TerrastoneArmorItem(EquipmentSlotType.CHEST, artifacts(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> TERRASTONE_LEGGINGS = REGISTER.register("terrastone_leggings", () -> new TerrastoneArmorItem(EquipmentSlotType.LEGS, artifacts(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> TERRASTONE_BOOTS = REGISTER.register("terrastone_boots", () -> new TerrastoneArmorItem(EquipmentSlotType.FEET, artifacts(Rarity.UNCOMMON)));

	public static final RegistryObject<Item> CRIMSON_CLAYMORE = REGISTER.register("crimson_claymore", () -> new CrimsonClaymoreItem(artifacts(Rarity.RARE)));
	public static final RegistryObject<Item> CRIMSON_CHESTPLATE = REGISTER.register("crimson_chestplate", () -> new CrimsonArmorItem(EquipmentSlotType.CHEST, artifacts(Rarity.RARE)));
	public static final RegistryObject<Item> CRIMSON_LEGGINGS = REGISTER.register("crimson_leggings", () -> new CrimsonArmorItem(EquipmentSlotType.LEGS, artifacts(Rarity.RARE)));
	public static final RegistryObject<Item> CRIMSON_BOOTS = REGISTER.register("crimson_boots", () -> new CrimsonArmorItem(EquipmentSlotType.FEET, artifacts(Rarity.RARE)));

	public static final RegistryObject<Item> DRAGON_SLAYER = REGISTER.register("dragon_slayer", () -> new DragonSlayerItem(artifacts().rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> BERSERKER_HELMET = REGISTER.register("berserker_helmet", () -> new BerserkerArmorItem(EquipmentSlotType.HEAD, artifacts(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> BERSERKER_CHESTPLATE = REGISTER.register("berserker_chestplate", () -> new BerserkerArmorItem(EquipmentSlotType.CHEST, artifacts(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> BERSERKER_LEGGINGS = REGISTER.register("berserker_leggings", () -> new BerserkerArmorItem(EquipmentSlotType.LEGS, artifacts(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> BERSERKER_BOOTS = REGISTER.register("berserker_boots", () -> new BerserkerArmorItem(EquipmentSlotType.FEET, artifacts(Rarity.UNCOMMON)));

	public static final RegistryObject<Item> THANATOS_LIGHT_HELMET = REGISTER.register("thanatos_light_helmet", () -> new ThanatosLightArmorItem(EquipmentSlotType.HEAD, artifacts(Rarity.RARE)));
	public static final RegistryObject<Item> THANATOS_LIGHT_CHESTPLATE = REGISTER.register("thanatos_light_chestplate", () -> new ThanatosLightArmorItem(EquipmentSlotType.CHEST, artifacts(Rarity.RARE)));
	public static final RegistryObject<Item> THANATOS_LIGHT_LEGGINGS = REGISTER.register("thanatos_light_leggings", () -> new ThanatosLightArmorItem(EquipmentSlotType.LEGS, artifacts(Rarity.RARE)));
	public static final RegistryObject<Item> THANATOS_LIGHT_BOOTS = REGISTER.register("thanatos_light_boots", () -> new ThanatosLightArmorItem(EquipmentSlotType.FEET, artifacts(Rarity.RARE)));

	public static final RegistryObject<Item> THANATOS_HEAVY_HELMET = REGISTER.register("thanatos_heavy_helmet", () -> new ThanatosHeavyArmorItem(EquipmentSlotType.HEAD, artifacts(Rarity.RARE)));
	public static final RegistryObject<Item> THANATOS_HEAVY_CHESTPLATE = REGISTER.register("thanatos_heavy_chestplate", () -> new ThanatosHeavyArmorItem(EquipmentSlotType.CHEST, artifacts(Rarity.RARE)));
	public static final RegistryObject<Item> THANATOS_HEAVY_LEGGINGS = REGISTER.register("thanatos_heavy_leggings", () -> new ThanatosHeavyArmorItem(EquipmentSlotType.LEGS, artifacts(Rarity.RARE)));
	public static final RegistryObject<Item> THANATOS_HEAVY_BOOTS = REGISTER.register("thanatos_heavy_boots", () -> new ThanatosHeavyArmorItem(EquipmentSlotType.FEET, artifacts(Rarity.RARE)));

	public static final RegistryObject<Item> THANATOS_BELT = REGISTER.register("thanatos_belt", () -> new BaseCurioItem(artifacts(Rarity.EPIC)));
	public static final RegistryObject<Item> CLOUD_SCARF = REGISTER.register("cloud_scarf", () -> new CloudScarfItem(artifacts(Rarity.EPIC).stacksTo(1)));

	public static final RegistryObject<Item> GOLDEN_SKULL = REGISTER.register("golden_skull", () -> new WallOrFloorItem(ModBlocks.GOLDEN_SKULL.get(), ModBlocks.GOLDEN_WALL_SKULL.get(),
			new Item.Properties().setISTER(() -> GoldenSkullISTER::new)));

	public static final RegistryObject<Item> NORMAL_RING = REGISTER.register("normal_ring", () -> new BaseCurioItem(artifacts().stacksTo(1)));
	public static final RegistryObject<Item> SHIELD_RING_1 = REGISTER.register("shield_ring_1", () -> new ShieldProviderItem(artifacts().stacksTo(1), new SimpleShield(ShieldSkins.SHIELD_RING, 10, 0, 0, 0, .5, .25)));

	public static final RegistryObject<Item> BATTLE_MITTS = REGISTER.register("battle_mitts", () -> new BaseCurioItem(artifacts(Rarity.UNCOMMON).stacksTo(1)));

	public static final RegistryObject<Item> BLOOD_ESSENCE_SHARD = essence(EssenceType.BLOOD, EssenceSize.ESSENCE);
	public static final RegistryObject<Item> BLOOD_ESSENCE_CRYSTAL = essence(EssenceType.BLOOD, EssenceSize.GREATER_ESSENCE);
	public static final RegistryObject<Item> GREATER_BLOOD_ESSENCE_CRYSTAL = essence(EssenceType.BLOOD, EssenceSize.EXQUISITE_ESSENCE);

	public static final RegistryObject<Item> METAL_ESSENCE_SHARD = essence(EssenceType.METAL, EssenceSize.ESSENCE);
	public static final RegistryObject<Item> METAL_ESSENCE_CRYSTAL = essence(EssenceType.METAL, EssenceSize.GREATER_ESSENCE);
	public static final RegistryObject<Item> GREATER_METAL_ESSENCE_CRYSTAL = essence(EssenceType.METAL, EssenceSize.EXQUISITE_ESSENCE);

	public static final RegistryObject<Item> MAGIC_ESSENCE_SHARD = essence(EssenceType.MAGIC, EssenceSize.ESSENCE);
	public static final RegistryObject<Item> MAGIC_ESSENCE_CRYSTAL = essence(EssenceType.MAGIC, EssenceSize.GREATER_ESSENCE);
	public static final RegistryObject<Item> GREATER_MAGIC_ESSENCE_CRYSTAL = essence(EssenceType.MAGIC, EssenceSize.EXQUISITE_ESSENCE);

	public static final RegistryObject<Item> EARTH_ESSENCE_SHARD = essence(EssenceType.EARTH, EssenceSize.ESSENCE);
	public static final RegistryObject<Item> EARTH_ESSENCE_CRYSTAL = essence(EssenceType.EARTH, EssenceSize.GREATER_ESSENCE);
	public static final RegistryObject<Item> GREATER_EARTH_ESSENCE_CRYSTAL = essence(EssenceType.EARTH, EssenceSize.EXQUISITE_ESSENCE);

	public static final RegistryObject<Item> FIRE_ESSENCE_SHARD = essence(EssenceType.FIRE, EssenceSize.ESSENCE);
	public static final RegistryObject<Item> FIRE_ESSENCE_CRYSTAL = essence(EssenceType.FIRE, EssenceSize.GREATER_ESSENCE);
	public static final RegistryObject<Item> GREATER_FIRE_ESSENCE_CRYSTAL = essence(EssenceType.FIRE, EssenceSize.EXQUISITE_ESSENCE);

	public static final RegistryObject<Item> AIR_ESSENCE_SHARD = essence(EssenceType.AIR, EssenceSize.ESSENCE);
	public static final RegistryObject<Item> AIR_ESSENCE_CRYSTAL = essence(EssenceType.AIR, EssenceSize.GREATER_ESSENCE);
	public static final RegistryObject<Item> GREATER_AIR_ESSENCE_CRYSTAL = essence(EssenceType.AIR, EssenceSize.EXQUISITE_ESSENCE);

	public static final RegistryObject<Item> WATER_ESSENCE_SHARD = essence(EssenceType.WATER, EssenceSize.ESSENCE);
	public static final RegistryObject<Item> WATER_ESSENCE_CRYSTAL = essence(EssenceType.WATER, EssenceSize.GREATER_ESSENCE);
	public static final RegistryObject<Item> GREATER_WATER_ESSENCE_CRYSTAL = essence(EssenceType.WATER, EssenceSize.EXQUISITE_ESSENCE);

	public static final RegistryObject<Item> FROST_ESSENCE_SHARD = essence(EssenceType.FROST, EssenceSize.ESSENCE);
	public static final RegistryObject<Item> FROST_ESSENCE_CRYSTAL = essence(EssenceType.FROST, EssenceSize.GREATER_ESSENCE);
	public static final RegistryObject<Item> GREATER_FROST_ESSENCE_CRYSTAL = essence(EssenceType.FROST, EssenceSize.EXQUISITE_ESSENCE);

	public static final RegistryObject<Item> DEATH_ESSENCE_SHARD = essence(EssenceType.DEATH, EssenceSize.ESSENCE);
	public static final RegistryObject<Item> DEATH_ESSENCE_CRYSTAL = essence(EssenceType.DEATH, EssenceSize.GREATER_ESSENCE);
	public static final RegistryObject<Item> GREATER_DEATH_ESSENCE_CRYSTAL = essence(EssenceType.DEATH, EssenceSize.EXQUISITE_ESSENCE);

	public static final RegistryObject<Item> DOMINANCE_ESSENCE_SHARD = essence(EssenceType.DOMINANCE, EssenceSize.ESSENCE);
	public static final RegistryObject<Item> DOMINANCE_ESSENCE_CRYSTAL = essence(EssenceType.DOMINANCE, EssenceSize.GREATER_ESSENCE);
	public static final RegistryObject<Item> GREATER_DOMINANCE_ESSENCE_CRYSTAL = essence(EssenceType.DOMINANCE, EssenceSize.EXQUISITE_ESSENCE);

	public static final RegistryObject<BlockItem> RUNESTONE = REGISTER.register("runestone", () -> new BlockItem(ModBlocks.RUNESTONE.get(), materials()));
	public static final RegistryObject<BlockItem> FOUNDRY_TILE = REGISTER.register("foundry_tile", () -> new BlockItem(ModBlocks.FOUNDRY_TILE.get(), materials()));

	public static final RegistryObject<BlockItem> HEART_CRYSTAL_ORE = REGISTER.register("heart_crystal_ore", () -> new BlockItem(
			ModBlocks.HEART_CRYSTAL_ORE.get(), materials(Rarity.RARE)));
	public static final RegistryObject<BlockItem> PYRITE_ORE = REGISTER.register("pyrite_ore", () -> new BlockItem(
			ModBlocks.PYRITE_ORE.get(), materials()));
	public static final RegistryObject<Item> PYRITE_INGOT = REGISTER.register("pyrite_ingot", () -> new Item(materials()));
	public static final RegistryObject<Item> PYRITE_NUGGET = REGISTER.register("pyrite_nugget", () -> new Item(materials()));
	public static final RegistryObject<BlockItem> PYRITE_BLOCK = REGISTER.register("pyrite_block", () -> new BlockItem(ModBlocks.PYRITE_BLOCK.get(), materials()));

	public static final RegistryObject<Item> NETHER_STEEL_INGOT = REGISTER.register("nether_steel_ingot", () -> new Item(materials(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> NETHER_STEEL_NUGGET = REGISTER.register("nether_steel_nugget", () -> new Item(materials(Rarity.UNCOMMON)));
	public static final RegistryObject<BlockItem> NETHER_STEEL_BLOCK = REGISTER.register("nether_steel_block", () -> new BlockItem(ModBlocks.NETHER_STEEL_BLOCK.get(), materials(Rarity.UNCOMMON)));

	public static final RegistryObject<Item> DAMASCUS_STEEL_INGOT = REGISTER.register("damascus_steel_ingot", () -> new Item(materials(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> DAMASCUS_STEEL_NUGGET = REGISTER.register("damascus_steel_nugget", () -> new Item(materials(Rarity.UNCOMMON)));
	public static final RegistryObject<BlockItem> DAMASCUS_STEEL_BLOCK = REGISTER.register("damascus_steel_block", () -> new BlockItem(ModBlocks.DAMASCUS_STEEL_BLOCK.get(), materials(Rarity.UNCOMMON)));

	public static final RegistryObject<Item> CRIMSON_METAL_SCRAP = REGISTER.register("crimson_metal_scrap", () -> new Item(materials(Rarity.RARE)));
	public static final RegistryObject<Item> TERRASTONE = REGISTER.register("terrastone", () -> new Item(materials(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> DEATH_INFUSED_LEATHER = REGISTER.register("death_infused_leather", () -> new Item(materials(Rarity.RARE)));
	public static final RegistryObject<Item> DEATH_INFUSED_INGOT = REGISTER.register("death_infused_ingot", () -> new Item(materials(Rarity.RARE)));

	public static final RegistryObject<Item> ACCELERATION_RUNE = REGISTER.register("acceleration_rune", () -> new Item(materials()));

	public static final RegistryObject<BlockItem> SIGIL_ENGRAVING_TABLE_3X3 = REGISTER.register("sigil_engraving_table_3x3", () -> new BlockItem(ModBlocks.SIGIL_ENGRAVING_TABLE_3X3.get(), artifacts()));
	public static final RegistryObject<BlockItem> SIGIL_ENGRAVING_TABLE_5X5 = REGISTER.register("sigil_engraving_table_5x5", () -> new BlockItem(ModBlocks.SIGIL_ENGRAVING_TABLE_5X5.get(), artifacts()));
	public static final RegistryObject<BlockItem> SIGIL_ENGRAVING_TABLE_7X7 = REGISTER.register("sigil_engraving_table_7x7", () -> new BlockItem(ModBlocks.SIGIL_ENGRAVING_TABLE_7X7.get(), artifacts()));

	public static final RegistryObject<BlockItem> STIGMA_TABLE_5X5 = REGISTER.register("stigma_table_5x5", () -> new BlockItem(ModBlocks.STIGMA_TABLE_5X5.get(), artifacts()));
	public static final RegistryObject<BlockItem> STIGMA_TABLE_7X7 = REGISTER.register("stigma_table_7x7", () -> new BlockItem(ModBlocks.STIGMA_TABLE_7X7.get(), artifacts()));

	public static final RegistryObject<BlockItem> SIGIL_SCRAPPER = REGISTER.register("sigil_scrapper", () -> new BlockItem(ModBlocks.SIGIL_SCRAPPER.get(), artifacts()));
	public static final RegistryObject<BlockItem> STIGMA_SCRAPPER = REGISTER.register("stigma_scrapper", () -> new BlockItem(ModBlocks.STIGMA_SCRAPPER.get(), artifacts()));

	public static final RegistryObject<BlockItem> CRUCIBLE = REGISTER.register("crucible", () -> new CrucibleBlockItem(ModBlocks.CRUCIBLE.get(), artifacts()));
	public static final RegistryObject<BlockItem> CRUCIBLE_CAMPFIRE = REGISTER.register("crucible_campfire", () -> new BlockItem(ModBlocks.CRUCIBLE_CAMPFIRE.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> CRUCIBLE_AUTOMATION_UNIT = REGISTER.register("crucible_automation_unit", () -> new CrucibleAutomationUnitBlockItem(ModBlocks.CRUCIBLE_AUTOMATION_UNIT.get(), artifacts()));

	public static final RegistryObject<BlockItem> MOCK_AUTOMATION_MODULE = REGISTER.register("mock_automation_module", () -> new AutomationModuleBlockItem(ModBlocks.MOCK_AUTOMATION_MODULE.get(), artifacts(), true, true));

	public static final RegistryObject<BlockItem> FURNACE_STOVE = REGISTER.register("furnace_stove", () -> new BlockItem(ModBlocks.FURNACE_STOVE.get(), artifacts()));
	public static final RegistryObject<BlockItem> FOUNDRY_STOVE = REGISTER.register("foundry_stove", () -> new BlockItem(ModBlocks.FOUNDRY_STOVE.get(), artifacts()));
	public static final RegistryObject<BlockItem> NETHER_STOVE = REGISTER.register("nether_stove", () -> new BlockItem(ModBlocks.NETHER_STOVE.get(), artifacts()));
	public static final RegistryObject<BlockItem> ESSENCE_STOVE = REGISTER.register("essence_stove", () -> new BlockItem(ModBlocks.ESSENCE_STOVE.get(), artifacts()));

	public static final RegistryObject<Item> FOUNDRY = REGISTER.register("foundry", () -> new FoundryBlockItem(ModBlocks.FOUNDRY.get(), artifacts().setISTER(() -> FoundryISTER::new)));

	public static final RegistryObject<Item> ESSENCE_HOLDER_BLOCK = REGISTER.register("essence_holder_block", () -> new BlockItem(ModBlocks.ESSENCE_HOLDER_BLOCK.get(), artifacts()));
	public static final RegistryObject<Item> ESSENCE_NET_CORE = REGISTER.register("essence_net_core", () -> new BlockItem(ModBlocks.ESSENCE_NET_CORE.get(), artifacts().stacksTo(1)));
	public static final RegistryObject<Item> ESSENCE_NET_ACCESSOR = REGISTER.register("essence_net_accessor", () -> new EssenceNetAccessorItem(artifacts().stacksTo(1)));
	public static final RegistryObject<Item> ESSENCE_NET_IMPORTER = REGISTER.register("essence_net_importer", () -> new EssenceNetBlockItem(ModBlocks.ESSENCE_NET_IMPORTER.get(), artifacts()));
	public static final RegistryObject<Item> ESSENCE_NET_EXPORTER = REGISTER.register("essence_net_exporter", () -> new EssenceNetBlockItem(ModBlocks.ESSENCE_NET_EXPORTER.get(), artifacts()));

	public static final RegistryObject<Item> SIGIL = REGISTER.register("sigil", () -> new SigilItem(sigils()));
	public static final RegistryObject<Item> BODY_SIGIL = REGISTER.register("body_sigil", () -> new BodySigilItem(sigils()));
	public static final RegistryObject<Item> SIGIL_ICON = REGISTER.register("sigil_icon", () -> new SigilItem(new Item.Properties().setISTER(() -> SigilIconRenderer::new)));

	public static final RegistryObject<Item> ABILITY_COLOR_PICKER = REGISTER.register("ability_color_picker", () -> new AbilityColorPickerItem(new Item.Properties()));
	public static final RegistryObject<Item> CYLINDER_TEST = REGISTER.register("cylinder_test", () -> new CylinderTestItem(new Item.Properties()));

	private static RegistryObject<Item> essence(EssenceType type, EssenceSize size){
		String id;
		switch(size){
			case ESSENCE:
				id = type.id+"_essence";
				break;
			case GREATER_ESSENCE:
				id = "greater_"+type.id+"_essence";
				break;
			case EXQUISITE_ESSENCE:
				id = "exquisite_"+type.id+"_essence";
				break;
			default:
				throw new IllegalStateException("Unreachable");
		}
		RegistryObject<Item> item = REGISTER.register(id, () -> new Item(materials(Rarity.UNCOMMON)));
		type.setItem(item, size);
		return item;
	}
}
