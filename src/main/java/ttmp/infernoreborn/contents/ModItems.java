package ttmp.infernoreborn.contents;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.item.*;

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
			return new ItemStack(Items.COBBLESTONE);
		}
	};

	public static final RegistryObject<Item> PRIMAL_INFERNO_SPARK = REGISTER.register("primal_inferno_spark", () ->
			new RandomAbilityItem(new Item.Properties().tab(SPARKS).rarity(Rarity.RARE)));
	public static final RegistryObject<Item> GENERATOR_INFERNO_SPARK = REGISTER.register("generator_inferno_spark", () ->
			new GeneratorAbilityItem(new Item.Properties().tab(SPARKS).rarity(Rarity.RARE)));
	public static final RegistryObject<Item> INFERNO_SPARK = REGISTER.register("inferno_spark", () ->
			new FixedAbilityItem(new Item.Properties().tab(SPARKS).rarity(Rarity.RARE)));

	public static final RegistryObject<Item> DEBUG_STICK_ATTRIBUTE = REGISTER.register("debug_stick_attribute", () -> new DebugStickAttributeItem(new Item.Properties().tab(ARTIFACTS)));
	public static final RegistryObject<Item> DEBUG_STICK_ABILITY = REGISTER.register("debug_stick_ability", () -> new DebugStickAbilityItem(new Item.Properties().tab(ARTIFACTS)));

	public static final RegistryObject<Item> ABILDEX = REGISTER.register("abildex", () -> new AbilDexItem(new Item.Properties().tab(ARTIFACTS)));
}
