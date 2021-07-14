package ttmp.infernoreborn.contents.entity.wind;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class AbstractWindEntity extends ProjectileEntity{
	private static final DataParameter<Integer> DATA_COLOR = EntityDataManager.defineId(AbstractWindEntity.class, DataSerializers.INT);
	private static final int DEFAULT_COLOR = 0xFFFFFF;

	public AbstractWindEntity(EntityType<? extends AbstractWindEntity> type, World world){
		super(type, world);
	}

	public int getColor(){
		return this.entityData.get(DATA_COLOR);
	}
	public void setColor(int color){
		this.entityData.set(DATA_COLOR, color);
	}

	public void tick(){
		Entity entity = this.getOwner();
		//noinspection deprecation
		if(this.level.isClientSide||(entity==null||entity.isAlive())&&this.level.hasChunkAt(this.blockPosition())){
			super.tick();

			RayTraceResult ray = ProjectileHelper.getHitResult(this, this::canHitEntity);
			if(ray.getType()!=RayTraceResult.Type.MISS&&!ForgeEventFactory.onProjectileImpact(this, ray)){
				this.onHit(ray);
			}

			this.checkInsideBlocks();
			Vector3d delta = this.getDeltaMovement();
			double x = this.getX()+delta.x;
			double y = this.getY()+delta.y;
			double z = this.getZ()+delta.z;
			ProjectileHelper.rotateTowardsMovement(this, 0.2F);
			this.setPos(x, y, z);
		}else this.remove();
	}

	@Override protected void onHit(RayTraceResult result){
		super.onHit(result);
		if(!this.level.isClientSide) this.remove();
	}
	@Override protected void defineSynchedData(){
		this.entityData.define(DATA_COLOR, DEFAULT_COLOR);
	}
	@Override public boolean hurt(DamageSource source, float amount){
		return false;
	}
	@Override public IPacket<?> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
