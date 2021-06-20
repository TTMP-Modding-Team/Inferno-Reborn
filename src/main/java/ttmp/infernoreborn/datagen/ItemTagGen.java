package ttmp.infernoreborn.datagen;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.ModTags;

import javax.annotation.Nullable;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public class ItemTagGen extends ItemTagsProvider{
	public ItemTagGen(DataGenerator generator, BlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper){
		super(generator, blockTagsProvider, MODID, existingFileHelper);
	}

	@Override protected void addTags(){
		this.copy(ModTags.Blocks.STORAGE_BLOCKS_DAMASCUS_STEEL, ModTags.STORAGE_BLOCKS_DAMASCUS_STEEL);
		this.tag(ModTags.INGOTS_DAMASCUS_STEEL).add(ModItems.DAMASCUS_STEEL_INGOT.get());
		this.tag(ModTags.NUGGETS_DAMASCUS_STEEL).add(ModItems.DAMASCUS_STEEL_NUGGET.get());
	}
}
