package ttmp.infernoreborn.contents.entity.wind;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.ModEntities;

public class DamagingWindEntity extends AbstractWindEntity{
	private int damage;
	private boolean setFire = false;
	public DamagingWindEntity(World world){
		this(ModEntities.DAMAGING_WIND_ENTITY.get(), world);
	}
	public DamagingWindEntity(EntityType<? extends AbstractWindEntity> type, World world){
		super(type, world);
	}

	public int getDamage(){
		return this.damage;
	}
	public DamagingWindEntity setDamage(int damage){
		this.damage = damage;
		return this;
	}
	public DamagingWindEntity doSetFire(boolean setFire){
		this.setFire = setFire;
		return this;
	}

	@Override protected void onHitEntity(EntityRayTraceResult result){
		Entity entity = result.getEntity();
		if(entity instanceof LivingEntity){
			entity.hurt(DamageSource.MAGIC, this.damage);
			if(this.setFire)
				entity.setSecondsOnFire(8);
		}
		super.onHitEntity(result);
	}
}
