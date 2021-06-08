package ttmp.infernoreborn.contents;

import net.minecraft.item.*;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.item.DebugStickAbilityItem;
import ttmp.infernoreborn.item.DebugStickAttributeItem;
import ttmp.infernoreborn.item.FixedAbilityItem;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class ModItems{
	private ModItems(){}

	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

	public static final ItemGroup GROUP = new ItemGroup("infernoreborn"){
		@Override public ItemStack makeIcon(){
			return new ItemStack(Items.COBBLESTONE);
		}
	};

	public static final RegistryObject<Item> INFERNO_SPARK = REGISTER.register("inferno_spark", () ->
			new FixedAbilityItem(new Item.Properties().tab(GROUP).rarity(Rarity.RARE)));
	public static final RegistryObject<Item> DEBUG_STICK_ATTRIBUTE = REGISTER.register("debug_stick_attribute", () -> new DebugStickAttributeItem(new Item.Properties().tab(GROUP)));
	public static final RegistryObject<Item> DEBUG_STICK_ABILITY = REGISTER.register("debug_stick_ability", () -> new DebugStickAbilityItem(new Item.Properties().tab(GROUP)));
}
