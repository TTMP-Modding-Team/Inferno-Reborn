package ttmp.infernoreborn.contents;

import net.minecraft.loot.LootPoolEntryType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.contents.loot.EssenceLootModifier;
import ttmp.infernoreborn.contents.loot.FuckingLootEntry;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class ModLootModifiers{
	private ModLootModifiers(){}

	public static final DeferredRegister<GlobalLootModifierSerializer<?>> REGISTER = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, MODID);

	public static final LootPoolEntryType FUCK_TYPE = Registry.register(Registry.LOOT_POOL_ENTRY_TYPE,
			new ResourceLocation(MODID, "fuck"), new LootPoolEntryType(new FuckingLootEntry.Serializer()));

	public static final RegistryObject<GlobalLootModifierSerializer<EssenceLootModifier>> ESSENCE = REGISTER.register("essence", EssenceLootModifier.Serializer::new);
}
