package ttmp.infernoreborn.contents.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import ttmp.infernoreborn.util.damage.Damages;

public class FrostbiteEffect extends Effect{
	public FrostbiteEffect(EffectType effectType, int color){
		super(effectType, color);
	}

	@Override public void applyEffectTick(LivingEntity entity, int amp){
		entity.hurt(Damages.frostbite(), 1);
	}

	@Override public boolean isDurationEffectTick(int ticks, int amp){
		return ticks%40==0;
	}
}
