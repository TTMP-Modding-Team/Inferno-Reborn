package ttmp.infernoreborn.contents.ability;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import ttmp.infernoreborn.contents.entity.SummonedSkeletonEntity;
import ttmp.infernoreborn.contents.entity.SummonedZombieEntity;

// TODO reimplement summon logic, also revise how summons actually work
public final class SummonAbility{
	private SummonAbility(){}

	public static Ability zombieNecromancy(){
		return new Ability(new Ability.Properties(0x466D36, 0x466D36)
				.addTargetedSkill(10, 600, (entity, holder, target) -> summon(entity, new SummonedZombieEntity(entity.level))));
	}

	public static Ability skeletonNecromancy(){
		return new Ability(new Ability.Properties(0x787878, 0x787878)
				.addTargetedSkill(10, 600, (entity, holder, target) -> summon(entity, new SummonedSkeletonEntity(entity.level))));
	}

	private static boolean summon(Entity entity, Entity minion){
		double x = entity.getRandomX(4);
		double y = entity.getY();
		double z = entity.getRandomZ(4);
		while(true){
			BlockPos p = new BlockPos(x, y, z);
			//noinspection deprecation
			if(entity.level.getBlockState(p).isAir(entity.level, p)) break;
			y++;
		}
		minion.setPos(x, y, z);
		return entity.level.addFreshEntity(minion);
	}
}
