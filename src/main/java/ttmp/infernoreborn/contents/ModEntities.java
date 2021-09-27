package ttmp.infernoreborn.contents;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.contents.entity.AnvilEntity;
import ttmp.infernoreborn.contents.entity.CreeperMissileEntity;
import ttmp.infernoreborn.contents.entity.WindEntity;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class ModEntities{
	private ModEntities(){}

	public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

	public static final RegistryObject<EntityType<WindEntity>> WIND = REGISTER.register("wind", () ->
			EntityType.Builder.<WindEntity>of(WindEntity::new, EntityClassification.MISC)
					.sized(0.3125F, 0.3125F).clientTrackingRange(64).updateInterval(1)
					.build("wind"));
	public static final RegistryObject<EntityType<AnvilEntity>> ANVIL = REGISTER.register("anvil", () ->
			EntityType.Builder.<AnvilEntity>of(AnvilEntity::new, EntityClassification.MISC)
					.sized(0.3125F, 0.3125F).clientTrackingRange(64).updateInterval(1)
					.build("anvil"));
	public static final RegistryObject<EntityType<CreeperMissileEntity>> CREEPER_MISSILE = REGISTER.register("creeper_missile", () ->
			EntityType.Builder.<CreeperMissileEntity>of(CreeperMissileEntity::new, EntityClassification.MISC)
					.sized(0.3125f, 0.3125f).clientTrackingRange(64).updateInterval(1)
					.build("creeper_missile"));
}
