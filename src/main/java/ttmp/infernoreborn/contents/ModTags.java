package ttmp.infernoreborn.contents;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

public final class ModTags{
	private ModTags(){}

	public static final Tags.IOptionalNamedTag<Item> ESSENCES = ItemTags.createOptional(new ResourceLocation(MODID, "essence_shards"));
	public static final Tags.IOptionalNamedTag<Item> GREATER_ESSENCES = ItemTags.createOptional(new ResourceLocation(MODID, "greater_essences"));
	public static final Tags.IOptionalNamedTag<Item> EXQUISITE_ESSENCES = ItemTags.createOptional(new ResourceLocation(MODID, "exquisite_essences"));

	public static final Tags.IOptionalNamedTag<Item> ORES_HEART_CRYSTAL = ItemTags.createOptional(new ResourceLocation("forge", "ores/heart_crystal"));
	public static final Tags.IOptionalNamedTag<Item> GEMS_HEART_CRYSTAL = ItemTags.createOptional(new ResourceLocation("forge", "gems/heart_crystal"));

	public static final Tags.IOptionalNamedTag<Item> ORES_PYRITE = ItemTags.createOptional(new ResourceLocation("forge", "ores/pyrite"));
	public static final Tags.IOptionalNamedTag<Item> INGOTS_PYRITE = ItemTags.createOptional(new ResourceLocation("forge", "ingots/pyrite"));
	public static final Tags.IOptionalNamedTag<Item> NUGGETS_PYRITE = ItemTags.createOptional(new ResourceLocation("forge", "nuggets/pyrite"));
	public static final Tags.IOptionalNamedTag<Item> STORAGE_BLOCKS_PYRITE = ItemTags.createOptional(new ResourceLocation("forge", "storage_blocks/pyrite"));

	public static final Tags.IOptionalNamedTag<Item> INGOTS_NETHER_STEEL = ItemTags.createOptional(new ResourceLocation("forge", "ingots/nether_steel"));
	public static final Tags.IOptionalNamedTag<Item> NUGGETS_NETHER_STEEL = ItemTags.createOptional(new ResourceLocation("forge", "nuggets/nether_steel"));
	public static final Tags.IOptionalNamedTag<Item> STORAGE_BLOCKS_NETHER_STEEL = ItemTags.createOptional(new ResourceLocation("forge", "storage_blocks/nether_steel"));

	public static final Tags.IOptionalNamedTag<Item> INGOTS_DAMASCUS_STEEL = ItemTags.createOptional(new ResourceLocation("forge", "ingots/damascus_steel"));
	public static final Tags.IOptionalNamedTag<Item> NUGGETS_DAMASCUS_STEEL = ItemTags.createOptional(new ResourceLocation("forge", "nuggets/damascus_steel"));
	public static final Tags.IOptionalNamedTag<Item> STORAGE_BLOCKS_DAMASCUS_STEEL = ItemTags.createOptional(new ResourceLocation("forge", "storage_blocks/damascus_steel"));

	public static final Tags.IOptionalNamedTag<Item> CURIOS_BELT = ItemTags.createOptional(new ResourceLocation("curios", "belt"));
	public static final Tags.IOptionalNamedTag<Item> CURIOS_NECKLACE = ItemTags.createOptional(new ResourceLocation("curios", "necklace"));
	public static final Tags.IOptionalNamedTag<Item> CURIOS_RING = ItemTags.createOptional(new ResourceLocation("curios", "ring"));
	public static final Tags.IOptionalNamedTag<Item> CURIOS_HANDS = ItemTags.createOptional(new ResourceLocation("curios", "hands"));
	public static final Tags.IOptionalNamedTag<Item> CURIOS_ESSENCE_HOLDER = ItemTags.createOptional(new ResourceLocation("curios", "essence_holder"));

	public static final class Blocks{
		private Blocks(){}

		public static final Tags.IOptionalNamedTag<Block> ORES_HEART_CRYSTAL = BlockTags.createOptional(new ResourceLocation("forge", "ores/heart_crystal"));
		public static final Tags.IOptionalNamedTag<Block> ORES_PYRITE = BlockTags.createOptional(new ResourceLocation("forge", "ores/pyrite"));

		public static final Tags.IOptionalNamedTag<Block> STORAGE_BLOCKS_PYRITE = BlockTags.createOptional(new ResourceLocation("forge", "storage_blocks/pyrite"));
		public static final Tags.IOptionalNamedTag<Block> STORAGE_BLOCKS_NETHER_STEEL = BlockTags.createOptional(new ResourceLocation("forge", "storage_blocks/nether_steel"));
		public static final Tags.IOptionalNamedTag<Block> STORAGE_BLOCKS_DAMASCUS_STEEL = BlockTags.createOptional(new ResourceLocation("forge", "storage_blocks/damascus_steel"));
	}

	public static final class Fluids {
		private Fluids(){}

		public static final Tags.IOptionalNamedTag<Fluid> CAN_VAPORIZE_IN_CRUCIBLE = FluidTags.createOptional(new ResourceLocation(MODID, "can_vaporize_in_crucible"));
	}
}
