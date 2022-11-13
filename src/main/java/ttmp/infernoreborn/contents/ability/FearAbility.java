package ttmp.infernoreborn.contents.ability;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import ttmp.infernoreborn.api.LivingUtils;
import ttmp.infernoreborn.api.ability.Ability;
import ttmp.infernoreborn.api.essence.EssenceType;
import ttmp.infernoreborn.contents.ModEffects;

public final class FearAbility{
	private FearAbility(){}

	public static Ability diabolo(){
		return new Ability(new Ability.Properties(0x710020, 0xae0b0b, 0xae0b0b)
				.addSkill(10, 600, (entity, holder) -> {
					LivingUtils.forEachLivingEntitiesInCylinder(entity, 12, 3, e -> {
						if(e instanceof MobEntity) return;
						if(e instanceof PlayerEntity){
							if(((PlayerEntity)e).isCreative()) return;
						}
						e.addEffect(new EffectInstance(ModEffects.FEAR.get(), 300, 2-(int)e.distanceToSqr(entity)/6));
					});
					return entity.addEffect(new EffectInstance(ModEffects.DIABOLO.get(), 600));
				}).drops(EssenceType.DOMINANCE, 4*9));
	}
}
