package ttmp.infernoreborn.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

public final class AbilityUtils{
	private AbilityUtils(){}

	public static void addStackEffect(LivingEntity entity,
	                                  Effect effect,
	                                  int duration,
	                                  int startAmplifier,
	                                  int amplifierGrow,
	                                  int maxAmplifier){
		addStackEffect(entity, effect, duration, startAmplifier, amplifierGrow, maxAmplifier, true, true);
	}
	public static void addStackEffect(LivingEntity entity,
	                                  Effect effect,
	                                  int duration,
	                                  int startAmplifier,
	                                  int amplifierGrow,
	                                  int maxAmplifier,
	                                  boolean visible,
	                                  boolean showIcon){
		EffectInstance instance = entity.getEffect(effect);
		if(instance==null){
			entity.addEffect(new EffectInstance(effect, duration, startAmplifier, false, visible, showIcon));
		}else if(instance.getAmplifier()<maxAmplifier||instance.getDuration()<duration){
			entity.addEffect(new EffectInstance(effect,
					duration,
					Math.max(instance.getAmplifier()+amplifierGrow, maxAmplifier),
					false,
					visible,
					showIcon));
		}
	}
}
