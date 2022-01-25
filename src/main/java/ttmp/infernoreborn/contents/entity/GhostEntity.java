package ttmp.infernoreborn.contents.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import ttmp.infernoreborn.contents.ModEntities;

import javax.annotation.Nullable;
import java.util.UUID;

public class GhostEntity extends Entity{

	private UUID targetUUID;
	private int targetNetworkId;
	private boolean isAngry;
	private long livingTime = 0;

	public GhostEntity(World world){
		this(ModEntities.GHOST.get(), world);
	}
	public GhostEntity(EntityType<? extends Entity> entityType, World world){
		super(entityType, world);
		isAngry = false;
	}


	@Override protected void defineSynchedData(){}

	public void setTarget(@Nullable Entity target){
		if(target!=null){
			this.targetUUID = target.getUUID();
			this.targetNetworkId = target.getId();
		}
	}

	@Nullable
	public Entity getTarget(){
		if(this.targetUUID!=null&&this.level instanceof ServerWorld){
			return ((ServerWorld)this.level).getEntity(this.targetUUID);
		}else{
			return this.targetNetworkId!=0 ? this.level.getEntity(this.targetNetworkId) : null;
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT nbt){
		if(this.targetUUID!=null) nbt.putUUID("Target", this.targetUUID);
		nbt.putBoolean("Angry", this.isAngry);
		nbt.putLong("LivingTime", this.livingTime);

	}
	@Override public IPacket<?> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT nbt){
		if(nbt.hasUUID("Target")) this.targetUUID = nbt.getUUID("Target");
		this.isAngry = nbt.getBoolean("Angry");
		this.livingTime = nbt.getLong("LivingTime");
	}

	@Override
	public void tick(){
		super.tick();
		livingTime++;
		if(livingTime>200){
			this.remove();
			return;
		}
		Entity target = this.getTarget();
		if(target==null){
			isAngry = false;
			return;
		}
		if(target instanceof PlayerEntity){
			if(!isAngry) isAngry = this.distanceToSqr(target)>100;
			else{
				double xDistance = target.getX()-this.getX();
				double yDistance = target.getY()-this.getY();
				double zDistance = target.getZ()-this.getZ();
				this.setDeltaMovement(new Vector3d(xDistance, yDistance, zDistance).normalize().scale(0.05D));
				this.move(MoverType.SELF, this.getDeltaMovement());
				if(this.getBoundingBox().intersects(target.getBoundingBox())){
					target.hurt(DamageSource.MAGIC, 10);
					this.remove();
				}
			}
		}

	}
}
