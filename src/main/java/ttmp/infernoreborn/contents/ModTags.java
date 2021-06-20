package ttmp.infernoreborn.contents;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class ModTags{
	private ModTags(){}

	public static final Tags.IOptionalNamedTag<Item> ESSENCE_SHARDS = ItemTags.createOptional(new ResourceLocation(MODID, "essence_shards"));
	public static final Tags.IOptionalNamedTag<Item> ESSENCE_CRYSTALS = ItemTags.createOptional(new ResourceLocation(MODID, "essence_crystals"));
	public static final Tags.IOptionalNamedTag<Item> GREATER_ESSENCE_CRYSTALS = ItemTags.createOptional(new ResourceLocation(MODID, "greater_essence_crystals"));

	public static final Tags.IOptionalNamedTag<Item> INGOTS_DAMASCUS_STEEL = ItemTags.createOptional(new ResourceLocation("forge", "ingots/damascus_steel"));
	public static final Tags.IOptionalNamedTag<Item> NUGGETS_DAMASCUS_STEEL = ItemTags.createOptional(new ResourceLocation("forge", "nuggets/damascus_steel"));
	public static final Tags.IOptionalNamedTag<Item> STORAGE_BLOCKS_DAMASCUS_STEEL = ItemTags.createOptional(new ResourceLocation("forge", "storage_blocks/damascus_steel"));

	public static final class Blocks{
		private Blocks(){}

		public static final Tags.IOptionalNamedTag<Block> STORAGE_BLOCKS_DAMASCUS_STEEL = BlockTags.createOptional(new ResourceLocation("forge", "storage_blocks/damascus_steel"));
	}
}
