package ttmp.infernoreborn.contents;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.contents.entity.AnvilEntity;
import ttmp.infernoreborn.contents.entity.projectile.CreeperMissileEntity;
import ttmp.infernoreborn.contents.entity.projectile.wind.DamagingWindEntity;
import ttmp.infernoreborn.contents.entity.projectile.wind.EffectWindEntity;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class ModEntities{
	private ModEntities(){}

	public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

	public static final RegistryObject<EntityType<DamagingWindEntity>> DAMAGING_WIND_ENTITY = REGISTER.register("damaging_wind_entity", () ->
			EntityType.Builder.<DamagingWindEntity>of(DamagingWindEntity::new, EntityClassification.MISC)
					.sized(0.3125F, 0.3125F).clientTrackingRange(64).updateInterval(1)
					.build("damaging_wind"));
	public static final RegistryObject<EntityType<EffectWindEntity>> EFFECT_WIND_ENTITY = REGISTER.register("effect_wind_entity", () ->
			EntityType.Builder.<EffectWindEntity>of(EffectWindEntity::new, EntityClassification.MISC)
					.sized(0.3125F, 0.3125F).clientTrackingRange(64).updateInterval(1)
					.build("effect_wind"));
	public static final RegistryObject<EntityType<AnvilEntity>> ANVIL = REGISTER.register("anvil", () ->
			EntityType.Builder.<AnvilEntity>of(AnvilEntity::new, EntityClassification.MISC)
					.sized(0.3125F, 0.3125F).clientTrackingRange(64).updateInterval(1)
					.build("anvil"));
	public static final RegistryObject<EntityType<CreeperMissileEntity>> CREEPER_MISSILE_ENTITY = REGISTER.register("creeper_missile_entity", () ->
			EntityType.Builder.<CreeperMissileEntity>of(CreeperMissileEntity::new, EntityClassification.MISC)
					.sized(0.3125f, 0.3125f).clientTrackingRange(64).updateInterval(1)
					.build("creeper_missile"));

}
