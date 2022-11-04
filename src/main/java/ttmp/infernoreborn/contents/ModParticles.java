package ttmp.infernoreborn.contents;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

public final class ModParticles{
	private ModParticles(){}

	public static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);

	public static final RegistryObject<BasicParticleType> CRUCIBLE_BUBBLE = REGISTER.register("crucible_bubble", () -> new BasicParticleType(false));
}
