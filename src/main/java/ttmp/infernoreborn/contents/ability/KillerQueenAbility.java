package ttmp.infernoreborn.contents.ability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.Explosion;
import ttmp.infernoreborn.api.LivingUtils;
import ttmp.infernoreborn.api.ability.Ability;
import ttmp.infernoreborn.api.essence.EssenceType;
import ttmp.infernoreborn.contents.ModAttributes;
import ttmp.infernoreborn.contents.ModEffects;
import ttmp.infernoreborn.util.damage.Damages;
import ttmp.infernoreborn.util.damage.LivingOnlyEntityDamageSource;

public final class KillerQueenAbility{
	private KillerQueenAbility(){}

	public static Ability killerQueen(){
		return new Ability(new Ability.Properties(0xE3AADD, 0xE3AADD)
				.onUpdate((entity, holder) -> {
					LivingEntity target = LivingUtils.getTarget(entity);
					if(target==null||!target.isAlive()) return;
					EffectInstance effect = target.getEffect(ModEffects.KILLER_QUEEN.get());
					if(effect==null) return;
					int c = effect.getAmplifier()+1;
					double distance = target.distanceTo(entity);
					if(distance>6&&(distance>15||c>=target.getHealth())){
						entity.hurt(Damages.killerQueen(entity), c);
						entity.level.explode(entity, new LivingOnlyEntityDamageSource("explosion.player", null, entity).setExplosion(),
								null, target.getX(), target.getY(), target.getZ(), 1+c/2f, false, Explosion.Mode.NONE);
						target.removeEffect(ModEffects.KILLER_QUEEN.get());
					}
				}).onHit((entity, holder, event) -> {
					Entity directEntity = event.getSource().getDirectEntity();
					if(entity==directEntity){
						LivingUtils.addStackEffect(event.getEntityLiving(), ModEffects.KILLER_QUEEN.get(), 600, 0, 1, 127);
					}else if(directEntity!=null&&event.getSource().isProjectile()){
						entity.level.explode(entity, new LivingOnlyEntityDamageSource("explosion.player", directEntity, entity).setExplosion(),
								null, directEntity.getX(), directEntity.getY(), directEntity.getZ(), 1.5f, false, Explosion.Mode.NONE);
						directEntity.kill();
					}
				}).onAttacked((entity, holder, event) -> {
					if(event.getSource().isExplosion()) event.setCanceled(true);
				}).addAttribute(ModAttributes.DAMAGE_RESISTANCE.get(), "ec33a2c7-5757-413a-9a79-51d507d068aa", 0.15, Operation.MULTIPLY_BASE)
				.drops(EssenceType.FIRE, 3*9)
				.drops(EssenceType.AIR, 3*9)
				.drops(EssenceType.DOMINANCE, 3*9));
	}
}
