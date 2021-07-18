package ttmp.infernoreborn.contents;

import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.contents.loot.EssenceLootModifier;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class ModLootModifiers{
	private ModLootModifiers(){}

	public static final DeferredRegister<GlobalLootModifierSerializer<?>> REGISTER = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, MODID);

	public static final RegistryObject<GlobalLootModifierSerializer<EssenceLootModifier>> ESSENCE = REGISTER.register("essence", EssenceLootModifier.Serializer::new);
}
