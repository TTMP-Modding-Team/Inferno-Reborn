package ttmp.infernoreborn.contents;

import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import ttmp.infernoreborn.ability.Ability;

import java.util.UUID;

import static ttmp.infernoreborn.InfernoReborn.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Bus.MOD)
public final class Abilities{
	private Abilities(){}

	private static IForgeRegistry<Ability> registry;

	public static IForgeRegistry<Ability> getRegistry(){
		return registry;
	}

	public static final DeferredRegister<Ability> REGISTER = DeferredRegister.create(Ability.class, MODID);

	public static final RegistryObject<Ability> HEART = REGISTER.register("heart", () ->
			new Ability(new Ability.Properties(0xFF0000)
					.addAttribute(Attributes.MAX_HEALTH, UUID.fromString("55924d9f-ac7a-4472-bfea-bc4e305a363f"), .5, Operation.ADDITION)));

	@SubscribeEvent
	public static void newRegistry(RegistryEvent.NewRegistry e){
		registry = new RegistryBuilder<Ability>()
				.setType(Ability.class)
				.setName(new ResourceLocation(MODID, "abilities"))
				.create();
	}
}
