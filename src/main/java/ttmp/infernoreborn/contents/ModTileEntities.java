package ttmp.infernoreborn.contents;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.contents.tile.CrucibleTile;
import ttmp.infernoreborn.contents.tile.EssenceHolderTile;
import ttmp.infernoreborn.contents.tile.EssenceNetCoreTile;
import ttmp.infernoreborn.contents.tile.EssenceNetExporterTile;
import ttmp.infernoreborn.contents.tile.EssenceNetImporterTile;
import ttmp.infernoreborn.contents.tile.FoundryProxyTile;
import ttmp.infernoreborn.contents.tile.FoundryTile;
import ttmp.infernoreborn.contents.tile.GoldenSkullTile;
import ttmp.infernoreborn.contents.tile.SigilEngravingTableTile;
import ttmp.infernoreborn.contents.tile.StigmaTableTile;

import static ttmp.infernoreborn.InfernoReborn.MODID;

@SuppressWarnings("ConstantConditions")
public final class ModTileEntities{
	private ModTileEntities(){}

	public static final DeferredRegister<TileEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);

	public static final RegistryObject<TileEntityType<SigilEngravingTableTile>> SIGIL_ENGRAVING_TABLE_3X3 = REGISTER.register("sigil_engraving_table_3x3", () ->
			TileEntityType.Builder.of(SigilEngravingTableTile::new3x3, ModBlocks.SIGIL_ENGRAVING_TABLE_3X3.get()).build(null));
	public static final RegistryObject<TileEntityType<SigilEngravingTableTile>> SIGIL_ENGRAVING_TABLE_5X5 = REGISTER.register("sigil_engraving_table_5x5", () ->
			TileEntityType.Builder.of(SigilEngravingTableTile::new5x5, ModBlocks.SIGIL_ENGRAVING_TABLE_5X5.get()).build(null));
	public static final RegistryObject<TileEntityType<SigilEngravingTableTile>> SIGIL_ENGRAVING_TABLE_7X7 = REGISTER.register("sigil_engraving_table_7x7", () ->
			TileEntityType.Builder.of(SigilEngravingTableTile::new7x7, ModBlocks.SIGIL_ENGRAVING_TABLE_7X7.get()).build(null));

	public static final RegistryObject<TileEntityType<StigmaTableTile>> STIGMA_TABLE_5X5 = REGISTER.register("stigma_table_5x5", () ->
			TileEntityType.Builder.of(StigmaTableTile::new5x5, ModBlocks.STIGMA_TABLE_5X5.get()).build(null));
	public static final RegistryObject<TileEntityType<StigmaTableTile>> STIGMA_TABLE_7X7 = REGISTER.register("stigma_table_7x7", () ->
			TileEntityType.Builder.of(StigmaTableTile::new7x7, ModBlocks.STIGMA_TABLE_7X7.get()).build(null));

	public static final RegistryObject<TileEntityType<FoundryTile>> FOUNDRY = REGISTER.register("foundry", () ->
			TileEntityType.Builder.of(FoundryTile::new, ModBlocks.FOUNDRY.get()).build(null));
	public static final RegistryObject<TileEntityType<FoundryProxyTile>> FOUNDRY_FIREBOX_PROXY = REGISTER.register("foundry_firebox_proxy", () ->
			TileEntityType.Builder.of(FoundryProxyTile::fireboxProxy, ModBlocks.FOUNDRY_FIREBOX.get()).build(null));
	public static final RegistryObject<TileEntityType<FoundryProxyTile>> FOUNDRY_GRATE_PROXY = REGISTER.register("foundry_grate_proxy", () ->
			TileEntityType.Builder.of(FoundryProxyTile::grateProxy, ModBlocks.FOUNDRY_GRATE_1.get(), ModBlocks.FOUNDRY_GRATE_2.get()).build(null));
	public static final RegistryObject<TileEntityType<FoundryProxyTile>> FOUNDRY_MOLD_PROXY = REGISTER.register("foundry_mold_proxy", () ->
			TileEntityType.Builder.of(FoundryProxyTile::moldProxy, ModBlocks.FOUNDRY_MOLD_1.get(), ModBlocks.FOUNDRY_MOLD_2.get()).build(null));

	public static final RegistryObject<TileEntityType<CrucibleTile>> CRUCIBLE = REGISTER.register("crucible", () ->
			TileEntityType.Builder.of(CrucibleTile::new, ModBlocks.CRUCIBLE.get(), ModBlocks.CRUCIBLE_CAMPFIRE.get()).build(null));

	public static final RegistryObject<TileEntityType<EssenceHolderTile>> ESSENCE_HOLDER = REGISTER.register("essence_holder", () ->
			TileEntityType.Builder.of(EssenceHolderTile::new, ModBlocks.ESSENCE_HOLDER_BLOCK.get()).build(null));
	public static final RegistryObject<TileEntityType<EssenceNetCoreTile>> ESSENCE_NET_CORE = REGISTER.register("essence_net_core", () ->
			TileEntityType.Builder.of(EssenceNetCoreTile::new, ModBlocks.ESSENCE_NET_CORE.get()).build(null));
	public static final RegistryObject<TileEntityType<EssenceNetImporterTile>> ESSENCE_NET_IMPORTER = REGISTER.register("essence_net_importer", () ->
			TileEntityType.Builder.of(EssenceNetImporterTile::new, ModBlocks.ESSENCE_NET_IMPORTER.get()).build(null));
	public static final RegistryObject<TileEntityType<EssenceNetExporterTile>> ESSENCE_NET_EXPORTER = REGISTER.register("essence_net_exporter", () ->
			TileEntityType.Builder.of(EssenceNetExporterTile::new, ModBlocks.ESSENCE_NET_EXPORTER.get()).build(null));

	public static final RegistryObject<TileEntityType<GoldenSkullTile>> GOLDEN_SKULL = REGISTER.register("golden_skull", () ->
			TileEntityType.Builder.of(GoldenSkullTile::new, ModBlocks.GOLDEN_SKULL.get(), ModBlocks.GOLDEN_WALL_SKULL.get()).build(null));
}
