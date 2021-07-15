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
import ttmp.infernoreborn.contents.sigil.AfflictionSigil;
import ttmp.infernoreborn.contents.sigil.EnduranceSigil;
import ttmp.infernoreborn.contents.sigil.MiniHeartSigil;
import ttmp.infernoreborn.contents.sigil.RunicShieldSigil;
import ttmp.infernoreborn.contents.sigil.Sigil;
import ttmp.infernoreborn.contents.sigil.TravelerSigil;

import static ttmp.infernoreborn.InfernoReborn.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Bus.MOD)
public final class Sigils{
	private Sigils(){}

	private static ForgeRegistry<Sigil> registry;

	public static ForgeRegistry<Sigil> getRegistry(){
		return registry;
	}

	public static final DeferredRegister<Sigil> REGISTER = DeferredRegister.create(Sigil.class, MODID);

	public static final RegistryObject<Sigil> MARK_OF_AFFLICTION = REGISTER.register("mark_of_affliction", () -> new AfflictionSigil(new Sigil.Properties(0xff431d, 0x4f0e01, 3)));
	public static final RegistryObject<Sigil> MARK_OF_ENDURANCE = REGISTER.register("mark_of_endurance", () -> new EnduranceSigil(new Sigil.Properties(0xffffff, 0x3e3e3e, 4)));
	public static final RegistryObject<Sigil> MINI_HEART = REGISTER.register("mini_heart", () -> new MiniHeartSigil(new Sigil.Properties(0xff0202, 0x560101, 3)));
	public static final RegistryObject<Sigil> RUNIC_SHIELD = REGISTER.register("runic_shield", () -> new RunicShieldSigil(new Sigil.Properties(0xaf02ff, 0x28003b, 5)));
	public static final RegistryObject<Sigil> SIGIL_OF_TRAVELER = REGISTER.register("sigil_of_traveler", () -> new TravelerSigil(new Sigil.Properties(0xe7f4f4, 0x3f5655, 3)));

	@SubscribeEvent
	public static void newRegistry(RegistryEvent.NewRegistry e){
		registry = (ForgeRegistry<Sigil>)new RegistryBuilder<Sigil>()
				.setType(Sigil.class)
				.setName(new ResourceLocation(MODID, "sigils"))
				.create();
	}
}
