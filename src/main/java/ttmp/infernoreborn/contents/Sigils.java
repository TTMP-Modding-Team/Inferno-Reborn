package ttmp.infernoreborn.contents;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import ttmp.infernoreborn.sigil.ShieldSigil;
import ttmp.infernoreborn.sigil.Sigil;
import ttmp.infernoreborn.sigil.AttackDamageSigil;

import static ttmp.infernoreborn.InfernoReborn.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Bus.MOD)
public final class Sigils{
	private Sigils(){}

	private static ForgeRegistry<Sigil> registry;

	public static ForgeRegistry<Sigil> getRegistry(){
		return registry;
	}

	public static final DeferredRegister<Sigil> REGISTER = DeferredRegister.create(Sigil.class, MODID);

	public static final RegistryObject<Sigil> TEST = REGISTER.register("test", () -> new AttackDamageSigil(new Sigil.Properties(0xea1f1f, 0x820909, 3).item(ModItems.TEST_SIGIL.get())));
	public static final RegistryObject<Sigil> TEST2 = REGISTER.register("test2", () -> new AttackDamageSigil(new Sigil.Properties(0x1668ec, 0x062a63, 4).item(ModItems.TEST_SIGIL_2.get())));
	public static final RegistryObject<Sigil> TEST3 = REGISTER.register("test3", () -> new ShieldSigil(new Sigil.Properties(0xc80ff6, 0x570f69, 5).item(ModItems.TEST_SIGIL_3.get())));

	@SubscribeEvent
	public static void newRegistry(RegistryEvent.NewRegistry e){
		registry = (ForgeRegistry<Sigil>)new RegistryBuilder<Sigil>()
				.setType(Sigil.class)
				.setName(new ResourceLocation(MODID, "sigils"))
				.create();
	}
}
