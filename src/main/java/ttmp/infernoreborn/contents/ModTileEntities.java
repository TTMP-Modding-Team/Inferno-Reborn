package ttmp.infernoreborn.contents;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.contents.tile.SigilEngravingTableTile;

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
}
