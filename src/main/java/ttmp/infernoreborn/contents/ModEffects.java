package ttmp.infernoreborn.contents;

import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.contents.effect.AbilityStackEffect;
import ttmp.infernoreborn.contents.effect.BloodFrenzyEffect;
import ttmp.infernoreborn.contents.effect.FrostbiteEffect;

import static net.minecraft.entity.ai.attributes.AttributeModifier.Operation.*;
import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class ModEffects {
	private ModEffects() {
	}

	public static final DeferredRegister<Effect> REGISTER = DeferredRegister.create(ForgeRegistries.POTIONS, MODID);

	public static final RegistryObject<Effect> FROSTBITE = REGISTER.register("frostbite", () -> new FrostbiteEffect(EffectType.HARMFUL, 0xFFFFFF)
			.addAttributeModifier(Attributes.MOVEMENT_SPEED, "48de2c35-5326-41a8-9125-4154bf3ea1e1", -.1, MULTIPLY_BASE));

	public static final RegistryObject<Effect> DIABOLO = REGISTER.register("diabolo", () -> new FrostbiteEffect(EffectType.BENEFICIAL, 0xFFFFFF)
			.addAttributeModifier(Attributes.ATTACK_DAMAGE, "a55443b7-b840-4d1c-bd77-c523464ce07f", 2, ADDITION)
			.addAttributeModifier(Attributes.ARMOR, "aeed175d-48ee-4fc6-a982-5dbd2547af27", 5, ADDITION)
			.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, "67c947bd-6951-48ea-983e-2f97157dd24a", 5, ADDITION)
			.addAttributeModifier(Attributes.ATTACK_DAMAGE, "01996e7e-79a6-4ef3-afce-5e8759b7f036", 2, ADDITION));
	public static final RegistryObject<Effect> FEAR = REGISTER.register("fear", () -> new FrostbiteEffect(EffectType.HARMFUL, 0xFFFFFF)
			.addAttributeModifier(Attributes.MOVEMENT_SPEED, "dc254072-0e8f-40b4-bb5b-25650f3f8aa7", -0.25, MULTIPLY_TOTAL)
			.addAttributeModifier(Attributes.ATTACK_SPEED, "1207c8d0-8a9b-4a8f-8ae7-192ede2558a4", -0.15, MULTIPLY_TOTAL)
			.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, "422903d3-1081-4545-9596-4d1f7120c46b", -0.35, MULTIPLY_TOTAL));

	public static final RegistryObject<Effect> BLOOD_FRENZY = REGISTER.register("blood_frenzy", () -> new BloodFrenzyEffect(EffectType.BENEFICIAL, 0xa00000)
			.addAttributeModifier(ModAttributes.REGENERATION.get(), "285f126c-14d4-48e7-a13d-443851d85b4c", 1.0 / 60, ADDITION));
	public static final RegistryObject<Effect> KILLER_QUEEN = REGISTER.register("killer_queen", () -> new AbilityStackEffect(EffectType.HARMFUL, 0x0));
	public static final RegistryObject<Effect> HAND_OF_MIDAS = REGISTER.register("hand_of_midas", () -> new AbilityStackEffect(EffectType.HARMFUL, 0xFFF35A));
}
