package ttmp.infernoreborn.contents;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class ModAttributes{
	private ModAttributes(){}

	public static final DeferredRegister<Attribute> REGISTER = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MODID);

	/** 마법이 아닌 모든 종류의 간접 피해량이 N만큼 상승 */
	public static final RegistryObject<RangedAttribute> RANGED_ATTACK = attribute("ranged_attack", 0, -1024, 1024);
	/** 마법이 피해량이 N만큼 상승 */
	public static final RegistryObject<RangedAttribute> MAGIC_ATTACK = attribute("magic_attack", 0, -1024, 1024);
	/** 무적시간이 아닌 매 틱 당 체력 재생 */
	public static final RegistryObject<RangedAttribute> REGENERATION = attribute("regeneration", 0, 0, 1024);
	/** 피해를 흡수하는 보호막의 최대 충전량 */
	public static final RegistryObject<RangedAttribute> SHIELD = attribute("shield", 0, 0, 1024);
	/** 피해를 흡수하는 보호막의 매 틱 당 회복량. */
	public static final RegistryObject<RangedAttribute> SHIELD_REGEN = attribute("shield_regeneration", 0.0025, 0, 1024);

	/** 퍼센트 단위 피해 면역 */
	public static final RegistryObject<RangedAttribute> DAMAGE_RESISTANCE = attribute("damage_resistance", 0, 0, 1);
	/** 퍼센트 단위 낙뎀 면역 */
	public static final RegistryObject<RangedAttribute> FALLING_DAMAGE_RESISTANCE = attribute("falling_damage_resistance", 0, 0, 1);
	/** 퍼센트 단위 비마법 직접 피해 면역 */
	public static final RegistryObject<RangedAttribute> MELEE_DAMAGE_RESISTANCE = attribute("melee_damage_resistance", 0, 0, 1);
	/** 퍼센트 단위 비마법 간접 피해 면역 */
	public static final RegistryObject<RangedAttribute> RANGED_DAMAGE_RESISTANCE = attribute("ranged_damage_resistance", 0, 0, 1);
	/** 퍼센트 단위 마법 피해 면역 */
	public static final RegistryObject<RangedAttribute> MAGIC_DAMAGE_RESISTANCE = attribute("magic_damage_resistance", 0, 0, 1);

	private static RegistryObject<RangedAttribute> attribute(String name, double def, double min, double max){
		String desc = "infernoreborn."+name;
		return REGISTER.register(name, () -> new RangedAttribute(desc, def, min, max));
	}
}