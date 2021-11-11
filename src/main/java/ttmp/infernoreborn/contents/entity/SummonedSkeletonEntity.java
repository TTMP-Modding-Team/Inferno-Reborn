package ttmp.infernoreborn.contents.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.ModEntities;

public class SummonedSkeletonEntity extends SkeletonEntity{
	private final long spawnTick;
	private static final long REMAININ_TICK = 200;

	public SummonedSkeletonEntity(World world){
		this(ModEntities.SUMMONED_SKELETON.get(), world);
	}
	public SummonedSkeletonEntity(EntityType<? extends SummonedSkeletonEntity> type, World world){
		super(type, world);
		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.0);
		spawnTick = world.getGameTime();
	}

	@Override public void tick(){
		super.tick();
		if(this.level.getGameTime()-spawnTick>=REMAININ_TICK)
			this.kill();
	}

	public static AttributeModifierMap.MutableAttribute registerAttributes(){
		return SkeletonEntity.createAttributes();
	}
}
