package ttmp.infernoreborn.contents.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import ttmp.infernoreborn.util.LivingUtils;

public class DiaboloEffect extends Effect {
    public DiaboloEffect(EffectType effectType, int color) {
        super(effectType, color);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amp) {
        entity.hurt(LivingUtils.frostbiteDamage(), 1);
    }
}
