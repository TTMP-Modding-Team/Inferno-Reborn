package ttmp.infernoreborn.contents.entity.wind;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class AbstractWindEntity extends ProjectileEntity{
	private static final DataParameter<Integer> DATA_COLOR = EntityDataManager.defineId(AbstractWindEntity.class, DataSerializers.INT);
	private static final int DEFAULT_COLOR = 0xFFFFFF;

	public AbstractWindEntity(EntityType<? extends AbstractWindEntity> type, World world){
		super(type, world);
	}

	public AbstractWindEntity(EntityType<? extends AbstractWindEntity> type, double x, double y, double z, World world){
		this(type, world);
		this.setPos(x, y, z);
	}

	public AbstractWindEntity(EntityType<? extends AbstractWindEntity> type, LivingEntity entity){
		this(type, entity.level);
		this.setPos(entity.getX(), entity.getY(), entity.getZ());
		this.setOwner(entity);
	}

	public int getColor(){
		return this.entityData.get(DATA_COLOR);
	}

	public void setColor(int color){
		this.entityData.set(DATA_COLOR, color);
	}

	public void tick(){
		Entity entity = this.getOwner();
		if(this.level.isClientSide||(entity==null||!entity.removed)&&this.level.hasChunkAt(this.blockPosition())){
			super.tick();

			RayTraceResult raytraceresult = ProjectileHelper.getHitResult(this, this::canHitEntity);
			if(raytraceresult.getType()!=RayTraceResult.Type.MISS&&!net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)){
				this.onHit(raytraceresult);
			}

			this.checkInsideBlocks();
			Vector3d vector3d = this.getDeltaMovement();
			double d0 = this.getX()+vector3d.x;
			double d1 = this.getY()+vector3d.y;
			double d2 = this.getZ()+vector3d.z;
			ProjectileHelper.rotateTowardsMovement(this, 0.2F);
			this.setPos(d0, d1, d2);
		}else{
			this.remove();
		}
	}

	@Override protected void onHit(RayTraceResult result){
		super.onHit(result);
		if(!this.level.isClientSide)
			this.remove();
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
