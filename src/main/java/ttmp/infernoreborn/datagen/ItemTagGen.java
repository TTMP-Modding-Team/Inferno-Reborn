package ttmp.infernoreborn.datagen;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.ModTags;
import ttmp.infernoreborn.util.EssenceType;

import javax.annotation.Nullable;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public class ItemTagGen extends ItemTagsProvider{
	public ItemTagGen(DataGenerator generator, BlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper){
		super(generator, blockTagsProvider, MODID, existingFileHelper);
	}

	@Override protected void addTags(){
		this.copy(ModTags.Blocks.STORAGE_BLOCKS_DAMASCUS_STEEL, ModTags.STORAGE_BLOCKS_DAMASCUS_STEEL);

		Builder<Item> shards = this.tag(ModTags.ESSENCE_SHARDS);
		Builder<Item> crystals = this.tag(ModTags.ESSENCE_CRYSTALS);
		Builder<Item> greaterCrystals = this.tag(ModTags.GREATER_ESSENCE_CRYSTALS);

		for(EssenceType type : EssenceType.values()){
			shards.add(type.getShardItem());
			crystals.add(type.getCrystalItem());
			greaterCrystals.add(type.getGreaterCrystalItem());
		}

		this.tag(ModTags.INGOTS_DAMASCUS_STEEL).add(ModItems.DAMASCUS_STEEL_INGOT.get());
		this.tag(ModTags.NUGGETS_DAMASCUS_STEEL).add(ModItems.DAMASCUS_STEEL_NUGGET.get());

		this.tag(ModTags.CURIOS_CURIO).add(ModItems.CURIO_TEST.get());
		this.tag(ModTags.CURIOS_BELT).add(ModItems.THANATOS_BELT.get());
		this.tag(ModTags.CURIOS_NECKLACE).add(ModItems.CLOUD_SCARF.get());
	}
}
