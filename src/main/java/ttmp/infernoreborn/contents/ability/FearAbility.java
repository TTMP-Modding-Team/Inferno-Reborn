package ttmp.infernoreborn.contents.ability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import ttmp.infernoreborn.contents.ModEffects;
import ttmp.infernoreborn.util.EssenceType;
import ttmp.infernoreborn.util.LivingUtils;

import java.util.List;
import java.util.stream.Collectors;

public final class FearAbility{
	private FearAbility(){}


	public static Ability diabolo(){
		return new Ability(new Ability.Properties(0x4B0000, 0x4B0000)
				.addSkill(10, 600, (entity, holder) -> {
					List<LivingEntity> entityList = LivingUtils.getLivingEntitiesInCylinder(entity, 12, 3).stream()
							.filter((e) -> !(e instanceof MobEntity))
							.collect(Collectors.toList());
					for(LivingEntity e : entityList){
						if(e instanceof PlayerEntity){
							if(((PlayerEntity)e).isCreative()) continue;
						}
						e.addEffect(new EffectInstance(ModEffects.FEAR.get(), 300, 2-(int)e.distanceToSqr(entity)/6));
					}
					return entity.addEffect(new EffectInstance(ModEffects.DIABOLO.get(), 600));
				}).drops(EssenceType.DOMINANCE, 4*9));
	}
}
