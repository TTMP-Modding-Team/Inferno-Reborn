package ttmp.infernoreborn.contents.entity.projectile.wind;

import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.ModEntities;

public class TestWindEntity extends AbstractWindEntity{
	public TestWindEntity(World world){
		this(ModEntities.TEST_WIND_ENTITY.get(), world);
	}
	public TestWindEntity(EntityType<? extends AbstractWindEntity> type, World world){
		super(type, world);
	}

	@Override protected void onHitBlock(BlockRayTraceResult result){
		System.out.println("Hit block"+result.hitInfo);
		super.onHitBlock(result);
	}
	@Override protected void onHitEntity(EntityRayTraceResult result){
		System.out.println("Hit entity"+result.hitInfo);
		super.onHitEntity(result);
	}
}
