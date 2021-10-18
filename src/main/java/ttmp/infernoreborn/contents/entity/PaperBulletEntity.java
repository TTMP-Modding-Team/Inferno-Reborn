package ttmp.infernoreborn.contents.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.CompoundNBT;
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
import ttmp.infernoreborn.contents.ModEntities;

public class PaperBulletEntity extends ProjectileEntity{
	private static final DataParameter<Integer> DATA_COLOR = EntityDataManager.defineId(WindEntity.class, DataSerializers.INT);

	private int ticks;

	public PaperBulletEntity(EntityType<? extends PaperBulletEntity> type, World world){
		super(type, world);
	}
	public PaperBulletEntity(World level){
		super(ModEntities.PAPER_BULLET.get(), level);
	}

	public int getColor(){
		return this.entityData.get(DATA_COLOR);
	}
	public void setColor(int color){
		this.entityData.set(DATA_COLOR, color);
	}

	@Override
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
			Vector3d delta = this.getDeltaMovement(); // TODO
			double x = this.getX()+delta.x;
			double y = this.getY()+delta.y;
			double z = this.getZ()+delta.z;
			ProjectileHelper.rotateTowardsMovement(this, 0.2F);
			this.setPos(x, y, z);

			if(!level.isClientSide&&++ticks>=80){
				remove();
				return;
			}
			delta = getDeltaMovement().scale(isInWater() ? .7 : .99);
			if(!isNoGravity()){
				delta = delta.subtract(0, .01, 0);
			}
			this.setDeltaMovement(delta);
		}else this.remove();
	}

	@Override protected void onHit(RayTraceResult result){
		super.onHit(result);
		if(!this.level.isClientSide) this.remove();
	}
	@Override public boolean hurt(DamageSource source, float amount){
		return false;
	}
	@Override public IPacket<?> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override protected void defineSynchedData(){
		this.entityData.define(DATA_COLOR, 0xFFFFFF);
	}

	@Override public void shoot(double pX, double pY, double pZ, float pVelocity, float pInaccuracy){
		super.shoot(pX, pY, pZ, pVelocity, pInaccuracy);
		// TODO
	}
	@Override public void shootFromRotation(Entity pProjectile, float pX, float pY, float pZ, float pVelocity, float pInaccuracy){
		super.shootFromRotation(pProjectile, pX, pY, pZ, pVelocity, pInaccuracy);
		// TODO
	}

	@Override protected void addAdditionalSaveData(CompoundNBT tag){
		super.addAdditionalSaveData(tag);
		tag.putInt("Ticks", this.ticks);
	}
	@Override protected void readAdditionalSaveData(CompoundNBT tag){
		super.readAdditionalSaveData(tag);
		this.ticks = tag.getInt("Ticks");
	}
}
