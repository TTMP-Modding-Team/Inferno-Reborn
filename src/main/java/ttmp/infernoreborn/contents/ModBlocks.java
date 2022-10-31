package ttmp.infernoreborn.contents;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.contents.block.EssenceHolderBlock;
import ttmp.infernoreborn.contents.block.FoundryBlock;
import ttmp.infernoreborn.contents.block.GoldenSkullBlock;
import ttmp.infernoreborn.contents.block.GoldenWallSkullBlock;
import ttmp.infernoreborn.contents.block.NamedContainerBlock;
import ttmp.infernoreborn.contents.block.PyriteOreBlock;
import ttmp.infernoreborn.contents.block.SigilScrapperBlock;
import ttmp.infernoreborn.contents.block.essencenet.EssenceNetCoreBlock;
import ttmp.infernoreborn.contents.block.essencenet.EssenceNetExporterBlock;
import ttmp.infernoreborn.contents.block.essencenet.EssenceNetImporterBlock;
import ttmp.infernoreborn.contents.tile.SigilEngravingTableTile;
import ttmp.infernoreborn.contents.tile.StigmaTableTile;

import static net.minecraft.state.properties.BlockStateProperties.LIT;
import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class ModBlocks{
	private ModBlocks(){}

	public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

	public static final RegistryObject<Block> HEART_CRYSTAL_ORE = REGISTER.register("heart_crystal_ore", () -> new Block(
			Properties.of(Material.STONE)
					.requiresCorrectToolForDrops()
					.strength(3, 3)
					.lightLevel(s -> 10)));
	public static final RegistryObject<Block> PYRITE_ORE = REGISTER.register("pyrite_ore", () -> new PyriteOreBlock(
			Properties.of(Material.STONE, MaterialColor.NETHER)
					.requiresCorrectToolForDrops()
					.strength(3, 3)
					.sound(SoundType.NETHER_GOLD_ORE)));
	public static final RegistryObject<Block> PYRITE_BLOCK = REGISTER.register("pyrite_block", () -> new Block(
			Properties.of(Material.METAL, MaterialColor.METAL)
					.requiresCorrectToolForDrops()
					.strength(5, 6)
					.sound(SoundType.METAL)));
	public static final RegistryObject<Block> NETHER_STEEL_BLOCK = REGISTER.register("nether_steel_block", () -> new Block(
			Properties.of(Material.METAL, MaterialColor.COLOR_BLACK)
					.requiresCorrectToolForDrops()
					.strength(50, 1200)
					.sound(SoundType.NETHERITE_BLOCK)));
	public static final RegistryObject<Block> DAMASCUS_STEEL_BLOCK = REGISTER.register("damascus_steel_block", () -> new Block(
			Properties.of(Material.METAL, MaterialColor.COLOR_BLACK)
					.requiresCorrectToolForDrops()
					.strength(5, 6)
					.sound(SoundType.METAL)));

	public static final RegistryObject<Block> RUNESTONE = REGISTER.register("runestone", () -> new Block(
			Properties.of(Material.STONE, MaterialColor.STONE).requiresCorrectToolForDrops().strength(1.5f, 6)));

	public static final RegistryObject<Block> SIGIL_ENGRAVING_TABLE_3X3 = REGISTER.register("sigil_engraving_table_3x3", () -> new NamedContainerBlock(
			Properties.of(Material.WOOD).strength(2.5f).sound(SoundType.WOOD)){
		@Override public TileEntity createTileEntity(BlockState state, IBlockReader world){
			return SigilEngravingTableTile.new3x3();
		}
	});
	public static final RegistryObject<Block> SIGIL_ENGRAVING_TABLE_5X5 = REGISTER.register("sigil_engraving_table_5x5", () -> new NamedContainerBlock(
			Properties.of(Material.WOOD).strength(2.5f).sound(SoundType.WOOD)){
		@Override public TileEntity createTileEntity(BlockState state, IBlockReader world){
			return SigilEngravingTableTile.new5x5();
		}
	});
	public static final RegistryObject<Block> SIGIL_ENGRAVING_TABLE_7X7 = REGISTER.register("sigil_engraving_table_7x7", () -> new NamedContainerBlock(
			Properties.of(Material.WOOD).strength(2.5f).sound(SoundType.WOOD)){
		@Override public TileEntity createTileEntity(BlockState state, IBlockReader world){
			return SigilEngravingTableTile.new7x7();
		}
	});

	public static final RegistryObject<Block> STIGMA_TABLE_5X5 = REGISTER.register("stigma_table_5x5", () -> new NamedContainerBlock(
			Properties.of(Material.WOOD).strength(2.5f).sound(SoundType.WOOD)){
		@Override public TileEntity createTileEntity(BlockState state, IBlockReader world){
			return StigmaTableTile.new5x5();
		}
	});
	public static final RegistryObject<Block> STIGMA_TABLE_7X7 = REGISTER.register("stigma_table_7x7", () -> new NamedContainerBlock(
			Properties.of(Material.WOOD).strength(2.5f).sound(SoundType.WOOD)){
		@Override public TileEntity createTileEntity(BlockState state, IBlockReader world){
			return StigmaTableTile.new7x7();
		}
	});

	public static final RegistryObject<Block> SIGIL_SCRAPPER = REGISTER.register("sigil_scrapper", () -> new SigilScrapperBlock(
			Properties.of(Material.WOOD).strength(2.5f).sound(SoundType.WOOD)));

	public static final RegistryObject<Block> FOUNDRY_TILE = REGISTER.register("foundry_tile", () -> new Block(
			Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.5f)));
	public static final RegistryObject<Block> FOUNDRY = REGISTER.register("foundry", () -> new FoundryBlock(
			Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.5f).lightLevel(s -> s.getValue(LIT) ? 13 : 0)));

	public static final RegistryObject<FoundryBlock.ProxyBlock> FOUNDRY_FIREBOX = REGISTER.register("foundry_firebox", () -> new FoundryBlock.FireboxProxyBlock(0, 0, 1,
			Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.5f).lightLevel(s -> s.getValue(LIT) ? 13 : 0).noDrops()));
	public static final RegistryObject<FoundryBlock.ProxyBlock> FOUNDRY_GRATE_1 = REGISTER.register("foundry_grate_1", () -> new FoundryBlock.GrateProxyBlock(0, 1, 0,
			Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.5f).noDrops()));
	public static final RegistryObject<FoundryBlock.ProxyBlock> FOUNDRY_GRATE_2 = REGISTER.register("foundry_grate_2", () -> new FoundryBlock.GrateProxyBlock(0, 1, 1,
			Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.5f).noDrops()));
	public static final RegistryObject<FoundryBlock.ProxyBlock> FOUNDRY_MOLD_1 = REGISTER.register("foundry_mold_1", () -> new FoundryBlock.MoldProxyBlock(1, 0, 0,
			Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.5f).noDrops().dynamicShape()));
	public static final RegistryObject<FoundryBlock.ProxyBlock> FOUNDRY_MOLD_2 = REGISTER.register("foundry_mold_2", () -> new FoundryBlock.MoldProxyBlock2(1, 0, 1,
			Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.5f).noDrops().dynamicShape()));

	public static final RegistryObject<Block> ESSENCE_HOLDER_BLOCK = REGISTER.register("essence_holder_block", () -> new EssenceHolderBlock(
			Properties.of(Material.GLASS).strength(1.5f).dynamicShape().lightLevel(value -> 15)));
	public static final RegistryObject<Block> ESSENCE_NET_CORE = REGISTER.register("essence_net_core", () -> new EssenceNetCoreBlock(
			Properties.of(Material.GLASS).strength(1.5f, 3600000).dynamicShape()));
	public static final RegistryObject<Block> ESSENCE_NET_IMPORTER = REGISTER.register("essence_net_importer", () -> new EssenceNetImporterBlock(
			Properties.of(Material.GLASS).strength(1.5f).dynamicShape()));
	public static final RegistryObject<Block> ESSENCE_NET_EXPORTER = REGISTER.register("essence_net_exporter", () -> new EssenceNetExporterBlock(
			Properties.of(Material.GLASS).strength(1.5f).dynamicShape()));

	public static final RegistryObject<Block> GOLDEN_SKULL = REGISTER.register("golden_skull", () -> new GoldenSkullBlock(
			Properties.of(Material.DECORATION).strength(1)));
	public static final RegistryObject<Block> GOLDEN_WALL_SKULL = REGISTER.register("golden_wall_skull", () -> new GoldenWallSkullBlock(
			Properties.of(Material.DECORATION).strength(1).lootFrom(GOLDEN_SKULL)));
}
