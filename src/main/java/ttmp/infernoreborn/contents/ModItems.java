package ttmp.infernoreborn.contents;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.item.DebugStickAbilityItem;
import ttmp.infernoreborn.item.DebugStickAttributeItem;
import ttmp.infernoreborn.item.EssenceHolderItem;
import ttmp.infernoreborn.item.FixedAbilityItem;
import ttmp.infernoreborn.item.GeneratorAbilityItem;
import ttmp.infernoreborn.item.RandomAbilityItem;
import ttmp.infernoreborn.item.TheBookItem;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class ModItems{
	private ModItems(){}

	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

	public static final ItemGroup SPARKS = new ItemGroup("infernoreborn.sparks"){
		@Override public ItemStack makeIcon(){
			return new ItemStack(PRIMAL_INFERNO_SPARK.get());
		}
	};
	public static final ItemGroup ARTIFACTS = new ItemGroup("infernoreborn.artifacts"){
		@Override public ItemStack makeIcon(){
			ItemStack stack = new ItemStack(BOOK_OF_THE_UNSPEAKABLE.get());
			TheBookItem.setHasEssenceHolder(stack, true);
			return stack;
		}
	};

	public static final RegistryObject<Item> PRIMAL_INFERNO_SPARK = REGISTER.register("primal_inferno_spark", () -> new RandomAbilityItem(new Item.Properties().tab(SPARKS).rarity(Rarity.EPIC)));
	public static final RegistryObject<Item> GENERATOR_INFERNO_SPARK = REGISTER.register("generator_inferno_spark", () -> new GeneratorAbilityItem(new Item.Properties().tab(SPARKS).rarity(Rarity.RARE)));
	public static final RegistryObject<Item> INFERNO_SPARK = REGISTER.register("inferno_spark", () -> new FixedAbilityItem(new Item.Properties().tab(SPARKS).rarity(Rarity.RARE)));

	public static final RegistryObject<Item> BOOK_OF_THE_UNSPEAKABLE = REGISTER.register("book_of_the_unspeakable", () -> new TheBookItem(new Item.Properties().stacksTo(1).tab(ARTIFACTS)));

	public static final RegistryObject<Item> BLOOD_ESSENCE_SHARD = REGISTER.register("blood_essence_shard", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> BLOOD_ESSENCE_CRYSTAL = REGISTER.register("blood_essence_crystal", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> GREATER_BLOOD_ESSENCE_CRYSTAL = REGISTER.register("greater_blood_essence_crystal", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));

	public static final RegistryObject<Item> METAL_ESSENCE_SHARD = REGISTER.register("metal_essence_shard", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> METAL_ESSENCE_CRYSTAL = REGISTER.register("metal_essence_crystal", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> GREATER_METAL_ESSENCE_CRYSTAL = REGISTER.register("greater_metal_essence_crystal", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));

	public static final RegistryObject<Item> FROST_ESSENCE_SHARD = REGISTER.register("frost_essence_shard", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> FROST_ESSENCE_CRYSTAL = REGISTER.register("frost_essence_crystal", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> GREATER_FROST_ESSENCE_CRYSTAL = REGISTER.register("greater_frost_essence_crystal", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));

	public static final RegistryObject<Item> EARTH_ESSENCE_SHARD = REGISTER.register("earth_essence_shard", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> EARTH_ESSENCE_CRYSTAL = REGISTER.register("earth_essence_crystal", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> GREATER_EARTH_ESSENCE_CRYSTAL = REGISTER.register("greater_earth_essence_crystal", () -> new Item(new Item.Properties().tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));

	public static final RegistryObject<Item> ESSENCE_HOLDER = REGISTER.register("essence_holder", () -> new EssenceHolderItem(new Item.Properties().stacksTo(1).tab(ARTIFACTS).rarity(Rarity.UNCOMMON)));

	public static final RegistryObject<Item> DEBUG_STICK_ATTRIBUTE = REGISTER.register("debug_stick_attribute", () -> new DebugStickAttributeItem(new Item.Properties().tab(ARTIFACTS)));
	public static final RegistryObject<Item> DEBUG_STICK_ABILITY = REGISTER.register("debug_stick_ability", () -> new DebugStickAbilityItem(new Item.Properties().tab(ARTIFACTS)));
}