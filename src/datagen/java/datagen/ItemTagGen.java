package datagen;

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
		this.copy(ModTags.Blocks.ORES_HEART_CRYSTAL, ModTags.ORES_HEART_CRYSTAL);
		this.copy(ModTags.Blocks.ORES_PYRITE, ModTags.ORES_PYRITE);
		this.copy(ModTags.Blocks.STORAGE_BLOCKS_PYRITE, ModTags.STORAGE_BLOCKS_PYRITE);
		this.copy(ModTags.Blocks.STORAGE_BLOCKS_NETHER_STEEL, ModTags.STORAGE_BLOCKS_NETHER_STEEL);
		this.copy(ModTags.Blocks.STORAGE_BLOCKS_DAMASCUS_STEEL, ModTags.STORAGE_BLOCKS_DAMASCUS_STEEL);

		Builder<Item> essences = this.tag(ModTags.ESSENCES);
		Builder<Item> greaterEssences = this.tag(ModTags.GREATER_ESSENCES);
		Builder<Item> exquisiteEssences = this.tag(ModTags.EXQUISITE_ESSENCES);

		for(EssenceType type : EssenceType.values()){
			essences.add(type.getEssenceItem());
			greaterEssences.add(type.getGreaterEssenceItem());
			exquisiteEssences.add(type.getExquisiteEssenceItem());
		}

		this.tag(ModTags.GEMS_HEART_CRYSTAL).add(ModItems.HEART_CRYSTAL.get());

		this.tag(ModTags.INGOTS_PYRITE).add(ModItems.PYRITE_INGOT.get());
		this.tag(ModTags.NUGGETS_PYRITE).add(ModItems.PYRITE_NUGGET.get());
		this.tag(ModTags.INGOTS_NETHER_STEEL).add(ModItems.NETHER_STEEL_INGOT.get());
		this.tag(ModTags.NUGGETS_NETHER_STEEL).add(ModItems.NETHER_STEEL_NUGGET.get());
		this.tag(ModTags.INGOTS_DAMASCUS_STEEL).add(ModItems.DAMASCUS_STEEL_INGOT.get());
		this.tag(ModTags.NUGGETS_DAMASCUS_STEEL).add(ModItems.DAMASCUS_STEEL_NUGGET.get());

		this.tag(ModTags.CURIOS_BELT).add(ModItems.THANATOS_BELT.get());
		this.tag(ModTags.CURIOS_NECKLACE).add(ModItems.CLOUD_SCARF.get());
		this.tag(ModTags.CURIOS_RING).add(ModItems.NORMAL_RING.get(), ModItems.SHIELD_RING_1.get());
		this.tag(ModTags.CURIOS_HANDS).add(ModItems.BATTLE_MITTS.get());
		this.tag(ModTags.CURIOS_ESSENCE_HOLDER).add(ModItems.ESSENCE_HOLDER.get(), ModItems.BOOK_OF_THE_UNSPEAKABLE_COMBINED.get(), ModItems.ESSENCE_NET_ACCESSOR.get());
	}
}
