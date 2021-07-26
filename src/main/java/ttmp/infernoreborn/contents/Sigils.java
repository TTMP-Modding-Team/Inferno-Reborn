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
import ttmp.infernoreborn.contents.sigil.BlessingOfMercurySigil;
import ttmp.infernoreborn.contents.sigil.EnduranceSigil;
import ttmp.infernoreborn.contents.sigil.FeatherFallSigil;
import ttmp.infernoreborn.contents.sigil.FrostbiteRuneSigil;
import ttmp.infernoreborn.contents.sigil.GoatEyeSigil;
import ttmp.infernoreborn.contents.sigil.MiniHeartSigil;
import ttmp.infernoreborn.contents.sigil.RunicShieldSigil;
import ttmp.infernoreborn.contents.sigil.ScaldRuneSigil;
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

	public static final RegistryObject<Sigil> MARK_OF_AFFLICTION = REGISTER.register("mark_of_affliction", () -> new AfflictionSigil(
			new Sigil.Properties(0xff431d, 0x4f0e01, 3).allowBody().allowMainhand().allowArmor().allowCurio()));
	public static final RegistryObject<Sigil> MARK_OF_ENDURANCE = REGISTER.register("mark_of_endurance", () -> new EnduranceSigil(
			new Sigil.Properties(0xffffff, 0x3e3e3e, 4).allowBody().allowArmor().allowCurio()));
	public static final RegistryObject<Sigil> MINI_HEART = REGISTER.register("mini_heart", () -> new MiniHeartSigil(
			new Sigil.Properties(0xff0202, 0x560101, 3).allowBody().allowArmor().allowCurio()));
	public static final RegistryObject<Sigil> RUNIC_SHIELD = REGISTER.register("runic_shield", () -> new RunicShieldSigil(
			new Sigil.Properties(0xaf02ff, 0x28003b, 5).allowBody().allowArmor().allowCurio()));
	public static final RegistryObject<Sigil> SIGIL_OF_TRAVELER = REGISTER.register("sigil_of_traveler", () -> new TravelerSigil(
			new Sigil.Properties(0xe7f4f4, 0x3f5655, 3).allowBody().allowArmor().allowCurio()));

	public static final RegistryObject<Sigil> GOAT_EYES = REGISTER.register("goat_eyes", () -> new GoatEyeSigil(
			new Sigil.Properties(0xFFFFFF, 0xFFFFFF, 1).allowBody()));
	public static final RegistryObject<Sigil> FEATHER_FALL_SIGIL = REGISTER.register("feather_fall_sigil", () -> new FeatherFallSigil(
			new Sigil.Properties(0xFFFFFF, 0xFFFFFF, 3).allowBody().allowArmor().allowCurio()));
	public static final RegistryObject<Sigil> BLESSING_OF_MERCURY = REGISTER.register("blessing_of_mercury", () -> new BlessingOfMercurySigil(
			new Sigil.Properties(0xFFFFFF, 0xFFFFFF, 5).allowBody().allowMainhand().allowArmor().allowCurio()));

	public static final RegistryObject<Sigil> FROSTBITE_RUNE = REGISTER.register("frostbite_rune", () -> new FrostbiteRuneSigil(
			new Sigil.Properties(0xFFFFFF, 0xFFFFFF, 4).allowMainhand()));
	public static final RegistryObject<Sigil> SCALD_RUNE = REGISTER.register("scald_rune", () -> new ScaldRuneSigil(
			new Sigil.Properties(0xFFFFFF, 0xFFFFFF, 4).allowMainhand()));

	@SubscribeEvent
	public static void newRegistry(RegistryEvent.NewRegistry e){
		registry = (ForgeRegistry<Sigil>)new RegistryBuilder<Sigil>()
				.setType(Sigil.class)
				.setName(new ResourceLocation(MODID, "sigils"))
				.create();
	}
}
