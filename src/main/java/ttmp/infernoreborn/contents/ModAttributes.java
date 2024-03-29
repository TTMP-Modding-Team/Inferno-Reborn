package ttmp.infernoreborn.contents;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Bus.MOD)
public final class ModAttributes{
	private ModAttributes(){}

	public static final DeferredRegister<Attribute> REGISTER = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MODID);

	/** Increases non-magic indirect damages by N */
	public static final RegistryObject<RangedAttribute> RANGED_ATTACK = attribute("ranged_attack", 0, -1024, 1024);
	/** Increases magic damages by N */
	public static final RegistryObject<RangedAttribute> MAGIC_ATTACK = attribute("magic_attack", 0, -1024, 1024);
	/** Generates HP by N every non-invulnerable ticks */
	public static final RegistryObject<RangedAttribute> REGENERATION = attribute("regeneration", 0, 0, 1024);

	/** Percentage-based damage resistance */
	public static final RegistryObject<RangedAttribute> DAMAGE_RESISTANCE = attribute("damage_resistance", 1, 1, 2);
	/** Percentage-based fall damage resistance */
	public static final RegistryObject<RangedAttribute> FALLING_DAMAGE_RESISTANCE = attribute("falling_damage_resistance", 1, 1, 2);
	/** Percentage-based non-magic direct damage resistance */
	public static final RegistryObject<RangedAttribute> MELEE_DAMAGE_RESISTANCE = attribute("melee_damage_resistance", 1, 1, 2);
	/** Percentage-based non-magic indirect damage resistance */
	public static final RegistryObject<RangedAttribute> RANGED_DAMAGE_RESISTANCE = attribute("ranged_damage_resistance", 1, 1, 2);
	/** Percentage-based magic damage resistance */
	public static final RegistryObject<RangedAttribute> MAGIC_DAMAGE_RESISTANCE = attribute("magic_damage_resistance", 1, 1, 2);

	private static RegistryObject<RangedAttribute> attribute(String name, double def, double min, double max){
		String desc = "infernoreborn."+name;
		return REGISTER.register(name, () -> new RangedAttribute(desc, def, min, max));
	}

	@SubscribeEvent
	public static void onEntityAttributeModification(EntityAttributeModificationEvent event){
		for(EntityType<? extends LivingEntity> type : event.getTypes())
			for(RegistryObject<Attribute> e : ModAttributes.REGISTER.getEntries())
				event.add(type, e.get());
	}
}