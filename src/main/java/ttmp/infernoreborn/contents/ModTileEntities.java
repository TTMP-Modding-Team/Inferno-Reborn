package ttmp.infernoreborn.contents;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.contents.tile.EssenceHolderTile;
import ttmp.infernoreborn.contents.tile.EssenceNetCoreTile;
import ttmp.infernoreborn.contents.tile.EssenceNetExporterTile;
import ttmp.infernoreborn.contents.tile.EssenceNetImporterTile;
import ttmp.infernoreborn.contents.tile.FoundryProxyTile;
import ttmp.infernoreborn.contents.tile.FoundryTile;
import ttmp.infernoreborn.contents.tile.GoldenSkullTile;
import ttmp.infernoreborn.contents.tile.SigilEngravingTableTile;
import ttmp.infernoreborn.contents.tile.StigmaTableTile;
import ttmp.infernoreborn.contents.tile.crucible.CrucibleAutomationUnitTile;
import ttmp.infernoreborn.contents.tile.crucible.CrucibleTile;
import ttmp.infernoreborn.contents.tile.crucible.EssenceStoveTile;
import ttmp.infernoreborn.contents.tile.crucible.FuelBasedStoveTile;
import ttmp.infernoreborn.contents.tile.crucible.MockAutomationModuleTile;

import java.util.Arrays;
import java.util.function.Supplier;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

@SuppressWarnings("ConstantConditions")
public final class ModTileEntities{
	private ModTileEntities(){}

	public static final DeferredRegister<TileEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);

	public static final RegistryObject<TileEntityType<SigilEngravingTableTile>> SIGIL_ENGRAVING_TABLE_3X3 = REGISTER.register("sigil_engraving_table_3x3",
			te(SigilEngravingTableTile::new3x3, ModBlocks.SIGIL_ENGRAVING_TABLE_3X3));
	public static final RegistryObject<TileEntityType<SigilEngravingTableTile>> SIGIL_ENGRAVING_TABLE_5X5 = REGISTER.register("sigil_engraving_table_5x5",
			te(SigilEngravingTableTile::new5x5, ModBlocks.SIGIL_ENGRAVING_TABLE_5X5));
	public static final RegistryObject<TileEntityType<SigilEngravingTableTile>> SIGIL_ENGRAVING_TABLE_7X7 = REGISTER.register("sigil_engraving_table_7x7",
			te(SigilEngravingTableTile::new7x7, ModBlocks.SIGIL_ENGRAVING_TABLE_7X7));

	public static final RegistryObject<TileEntityType<StigmaTableTile>> STIGMA_TABLE_5X5 = REGISTER.register("stigma_table_5x5",
			te(StigmaTableTile::new5x5, ModBlocks.STIGMA_TABLE_5X5));
	public static final RegistryObject<TileEntityType<StigmaTableTile>> STIGMA_TABLE_7X7 = REGISTER.register("stigma_table_7x7",
			te(StigmaTableTile::new7x7, ModBlocks.STIGMA_TABLE_7X7));

	public static final RegistryObject<TileEntityType<FoundryTile>> FOUNDRY = REGISTER.register("foundry",
			te(FoundryTile::new, ModBlocks.FOUNDRY));
	public static final RegistryObject<TileEntityType<FoundryProxyTile>> FOUNDRY_FIREBOX_PROXY = REGISTER.register("foundry_firebox_proxy",
			te(FoundryProxyTile::fireboxProxy, ModBlocks.FOUNDRY_FIREBOX));
	public static final RegistryObject<TileEntityType<FoundryProxyTile>> FOUNDRY_GRATE_PROXY = REGISTER.register("foundry_grate_proxy",
			te(FoundryProxyTile::grateProxy, ModBlocks.FOUNDRY_GRATE_1, ModBlocks.FOUNDRY_GRATE_2));
	public static final RegistryObject<TileEntityType<FoundryProxyTile>> FOUNDRY_MOLD_PROXY = REGISTER.register("foundry_mold_proxy",
			te(FoundryProxyTile::moldProxy, ModBlocks.FOUNDRY_MOLD_1, ModBlocks.FOUNDRY_MOLD_2));

	public static final RegistryObject<TileEntityType<CrucibleTile>> CRUCIBLE = REGISTER.register("crucible",
			te(CrucibleTile::new, ModBlocks.CRUCIBLE, ModBlocks.CRUCIBLE_CAMPFIRE));

	public static final RegistryObject<TileEntityType<CrucibleAutomationUnitTile>> CRUCIBLE_AUTOMATION_UNIT = REGISTER.register("crucible_automation_unit",
			te(CrucibleAutomationUnitTile::new, ModBlocks.CRUCIBLE_AUTOMATION_UNIT));

	public static final RegistryObject<TileEntityType<MockAutomationModuleTile>> MOCK_AUTOMATION_MODULE = REGISTER.register("mock_automation_module",
			te(MockAutomationModuleTile::new, ModBlocks.MOCK_AUTOMATION_MODULE));

	public static final RegistryObject<TileEntityType<FuelBasedStoveTile.Furnace>> FURNACE_STOVE = REGISTER.register("furnace_stove",
			te(FuelBasedStoveTile.Furnace::new, ModBlocks.FURNACE_STOVE));
	public static final RegistryObject<TileEntityType<FuelBasedStoveTile.Foundry>> FOUNDRY_STOVE = REGISTER.register("foundry_stove",
			te(FuelBasedStoveTile.Foundry::new, ModBlocks.FOUNDRY_STOVE));
	public static final RegistryObject<TileEntityType<FuelBasedStoveTile.Nether>> NETHER_STOVE = REGISTER.register("nether_stove",
			te(FuelBasedStoveTile.Nether::new, ModBlocks.NETHER_STOVE));
	public static final RegistryObject<TileEntityType<EssenceStoveTile>> ESSENCE_STOVE = REGISTER.register("essence_stove",
			te(EssenceStoveTile::new, ModBlocks.ESSENCE_STOVE));

	public static final RegistryObject<TileEntityType<EssenceHolderTile>> ESSENCE_HOLDER = REGISTER.register("essence_holder",
			te(EssenceHolderTile::new, ModBlocks.ESSENCE_HOLDER_BLOCK));
	public static final RegistryObject<TileEntityType<EssenceNetCoreTile>> ESSENCE_NET_CORE = REGISTER.register("essence_net_core",
			te(EssenceNetCoreTile::new, ModBlocks.ESSENCE_NET_CORE));
	public static final RegistryObject<TileEntityType<EssenceNetImporterTile>> ESSENCE_NET_IMPORTER = REGISTER.register("essence_net_importer",
			te(EssenceNetImporterTile::new, ModBlocks.ESSENCE_NET_IMPORTER));
	public static final RegistryObject<TileEntityType<EssenceNetExporterTile>> ESSENCE_NET_EXPORTER = REGISTER.register("essence_net_exporter",
			te(EssenceNetExporterTile::new, ModBlocks.ESSENCE_NET_EXPORTER));

	public static final RegistryObject<TileEntityType<GoldenSkullTile>> GOLDEN_SKULL = REGISTER.register("golden_skull",
			te(GoldenSkullTile::new, ModBlocks.GOLDEN_SKULL, ModBlocks.GOLDEN_WALL_SKULL));

	@SafeVarargs private static <T extends TileEntity> Supplier<TileEntityType<T>> te(Supplier<T> factory, Supplier<? extends Block>... blocks){
		return () -> TileEntityType.Builder.of(factory, Arrays.stream(blocks).map(b -> b.get()).toArray(Block[]::new))
				.build(null);
	}
}
