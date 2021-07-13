package ttmp.infernoreborn.contents;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.contents.entity.AnvilEntity;
import ttmp.infernoreborn.contents.entity.wind.DamagingWindEntity;
import ttmp.infernoreborn.contents.entity.wind.EffectWindEntity;
import ttmp.infernoreborn.contents.entity.wind.TestWindEntity;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class ModEntities{
	private ModEntities(){}

	public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

	/*public static final RegistryObject<EntityType<AbstractWindEntity>> WIND_ENTITY = REGISTER.register("wind_entity", () ->
			EntityType.Builder.<AbstractWindEntity>of(AbstractWindEntity::new, EntityClassification.MISC)
					.sized(0.3125F, 0.3125F).clientTrackingRange(64).updateInterval(1)
					.build("inferno_wind"));*/
	public static final RegistryObject<EntityType<TestWindEntity>> TEST_WIND_ENTITY = REGISTER.register("test_wind_entity", () ->
			EntityType.Builder.<TestWindEntity>of(TestWindEntity::new, EntityClassification.MISC)
					.sized(0.3125F, 0.3125F).clientTrackingRange(64).updateInterval(1)
					.build("inferno_test_wind"));
	public static final RegistryObject<EntityType<DamagingWindEntity>> DAMAGING_WIND_ENTITY = REGISTER.register("damaging_wind_entity", () ->
			EntityType.Builder.<DamagingWindEntity>of(DamagingWindEntity::new, EntityClassification.MISC)
					.sized(0.3125F, 0.3125F).clientTrackingRange(64).updateInterval(1)
					.build("inferno_damaging_wind"));
	public static final RegistryObject<EntityType<EffectWindEntity>> EFFECT_WIND_ENTITY = REGISTER.register("effect_wind_entity", () ->
			EntityType.Builder.<EffectWindEntity>of(EffectWindEntity::new, EntityClassification.MISC)
					.sized(0.3125F, 0.3125F).clientTrackingRange(64).updateInterval(1)
					.build("inferno_effect_wind"));
	public static final RegistryObject<EntityType<AnvilEntity>> ANVIL = REGISTER.register("anvil", () ->
			EntityType.Builder.<AnvilEntity>of(AnvilEntity::new, EntityClassification.MISC)
					.sized(0.3125F, 0.3125F).clientTrackingRange(64).updateInterval(1)
					.build("inferno_anvil"));

}
