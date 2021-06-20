package ttmp.infernoreborn.contents;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.contents.block.SigilEngravingTableBlock;
import ttmp.infernoreborn.contents.tile.SigilEngravingTableTile;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class ModBlocks{
	private ModBlocks(){}

	public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

	public static final RegistryObject<Block> SIGIL_ENGRAVING_TABLE_3X3 = REGISTER.register("sigil_engraving_table_3x3", () -> new SigilEngravingTableBlock(Properties.of(Material.WOOD)){
		@Override public SigilEngravingTableTile createTileEntity(BlockState state, IBlockReader world){
			return SigilEngravingTableTile.new3x3();
		}
	});
	public static final RegistryObject<Block> SIGIL_ENGRAVING_TABLE_5X5 = REGISTER.register("sigil_engraving_table_5x5", () -> new SigilEngravingTableBlock(Properties.of(Material.WOOD)){
		@Override public SigilEngravingTableTile createTileEntity(BlockState state, IBlockReader world){
			return SigilEngravingTableTile.new5x5();
		}
	});
	public static final RegistryObject<Block> SIGIL_ENGRAVING_TABLE_7X7 = REGISTER.register("sigil_engraving_table_7x7", () -> new SigilEngravingTableBlock(Properties.of(Material.WOOD)){
		@Override public SigilEngravingTableTile createTileEntity(BlockState state, IBlockReader world){
			return SigilEngravingTableTile.new7x7();
		}
	});

	public static final RegistryObject<Block> DAMASCUS_STEEL_BLOCK = REGISTER.register("damascus_steel_block", () -> new Block(Properties.of(Material.METAL)));
}
