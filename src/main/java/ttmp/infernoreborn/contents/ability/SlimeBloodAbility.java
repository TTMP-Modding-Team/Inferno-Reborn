package ttmp.infernoreborn.contents.ability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.MagmaCubeEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.IServerWorld;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.contents.ability.holder.ServerAbilityHolder;
import ttmp.infernoreborn.util.EssenceType;
import ttmp.infernoreborn.util.SlimeEntityAccessor;

import javax.annotation.Nullable;

public final class SlimeBloodAbility{
	private SlimeBloodAbility(){}

	public static Ability slimeBlood(){
		return new Ability(new Ability.Properties(0x400000, 0x400000)
				.onAttacked((entity, holder, event) -> {
					if(cannotTriggerSlimeBlood(event.getSource())||entity.getRandom().nextInt(2)!=0) return;
					MagmaCubeEntity magmaCube = new MagmaCubeEntity(EntityType.MAGMA_CUBE, entity.level);
					summonSlime(magmaCube, entity, event.getSource().getDirectEntity());
				}).drops(EssenceType.BLOOD, 2*9)
				.drops(EssenceType.FIRE, 2*9));
	}

	public static Ability magmaBlood(){
		return new Ability(new Ability.Properties(0x400000, 0x400000)
				.onAttacked((entity, holder, event) -> {
					if(cannotTriggerSlimeBlood(event.getSource())||entity.getRandom().nextInt(2)!=0) return;
					MagmaCubeEntity magmaCube = new MagmaCubeEntity(EntityType.MAGMA_CUBE, entity.level);
					summonSlime(magmaCube, entity, event.getSource().getDirectEntity());
				}).drops(EssenceType.BLOOD, 2*9)
				.drops(EssenceType.FIRE, 2*9));
	}

	private static boolean cannotTriggerSlimeBlood(DamageSource source){
		return source.getDirectEntity()==null||
				source.isMagic()||
				source instanceof EntityDamageSource&&((EntityDamageSource)source).isThorns();
	}

	private static void summonSlime(SlimeEntity slime, LivingEntity owner, @Nullable Entity target){
		if(!(slime.level instanceof IServerWorld)){
			InfernoReborn.LOGGER.warn("Summoning slime failed because the provided world is not a server");
			return;
		}
		if(!(slime instanceof SlimeEntityAccessor)){
			InfernoReborn.LOGGER.warn("Slime entity won't be summoned because mixin didn't apply correctly");
			return;
		}
		slime.setPos(owner.getX(), owner.getY(), owner.getZ());
		if(target instanceof LivingEntity) slime.setTarget((LivingEntity)target);
		slime.finalizeSpawn((IServerWorld)slime.level, slime.level.getCurrentDifficultyAt(slime.blockPosition()), SpawnReason.MOB_SUMMONED, null, null);
		((SlimeEntityAccessor)slime).setSlimeSize(1, true);
		ServerAbilityHolder of = ServerAbilityHolder.of(slime);
		if(of!=null) of.markSpawned();
		slime.level.addFreshEntity(slime);
	}
}
