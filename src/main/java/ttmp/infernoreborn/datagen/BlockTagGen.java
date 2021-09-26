package ttmp.infernoreborn.datagen;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import ttmp.infernoreborn.contents.ModBlocks;
import ttmp.infernoreborn.contents.ModTags;

import javax.annotation.Nullable;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public class BlockTagGen extends BlockTagsProvider{
	public BlockTagGen(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper){
		super(generator, MODID, existingFileHelper);
	}

	@Override protected void addTags(){
		tag(Tags.Blocks.ORES)
				.addTag(ModTags.Blocks.ORES_HEART_CRYSTAL)
				.addTag(ModTags.Blocks.ORES_PYRITE);
		tag(Tags.Blocks.STORAGE_BLOCKS)
				.addTag(ModTags.Blocks.STORAGE_BLOCKS_PYRITE)
				.addTag(ModTags.Blocks.STORAGE_BLOCKS_NETHER_STEEL)
				.addTag(ModTags.Blocks.STORAGE_BLOCKS_DAMASCUS_STEEL);

		tag(ModTags.Blocks.ORES_HEART_CRYSTAL).add(ModBlocks.HEART_CRYSTAL_ORE.get());
		tag(ModTags.Blocks.ORES_PYRITE).add(ModBlocks.PYRITE_ORE.get());

		tag(ModTags.Blocks.STORAGE_BLOCKS_PYRITE).add(ModBlocks.PYRITE_BLOCK.get());
		tag(ModTags.Blocks.STORAGE_BLOCKS_NETHER_STEEL).add(ModBlocks.NETHER_STEEL_BLOCK.get());
		tag(ModTags.Blocks.STORAGE_BLOCKS_DAMASCUS_STEEL).add(ModBlocks.DAMASCUS_STEEL_BLOCK.get());
	}
}
