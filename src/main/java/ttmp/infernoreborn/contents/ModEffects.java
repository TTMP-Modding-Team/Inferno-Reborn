package ttmp.infernoreborn.contents;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import ttmp.infernoreborn.contents.effect.BloodFrenzyEffect;
import ttmp.infernoreborn.contents.effect.AbilityStackEffect;
import ttmp.infernoreborn.contents.effect.CooldownEffect;

import static net.minecraft.entity.ai.attributes.AttributeModifier.Operation.ADDITION;
import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class ModEffects{
	private ModEffects(){}

	public static final DeferredRegister<Effect> REGISTER = DeferredRegister.create(ForgeRegistries.POTIONS, MODID);

	public static final RegistryObject<Effect> BLOOD_FRENZY = REGISTER.register("blood_frenzy", () -> new BloodFrenzyEffect(EffectType.BENEFICIAL, 0xa00000)
			.addAttributeModifier(ModAttributes.REGENERATION.get(), "285f126c-14d4-48e7-a13d-443851d85b4c", 1.0/60, ADDITION));
	public static final RegistryObject<Effect> KILLER_QUEEN = REGISTER.register("killer_queen", () -> new AbilityStackEffect(EffectType.HARMFUL, 0x0));
	public static final RegistryObject<Effect> HAND_OF_MIDAS = REGISTER.register("hand_of_midas", () -> new AbilityStackEffect(EffectType.HARMFUL, 0xFFF35A));
	public static final RegistryObject<Effect> GHOST_TRICK_COOLDOWN = REGISTER.register("ghost_trick_cooldown", () -> new CooldownEffect(EffectType.NEUTRAL, 0x0));
}
