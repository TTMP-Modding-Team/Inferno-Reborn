package ttmp.infernoreborn.contents.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class WindProjectileEntity extends ProjectileEntity{
	private static final DataParameter<Integer> DATA_COLOR = EntityDataManager.defineId(WindProjectileEntity.class, DataSerializers.INT);
	private static final int DEFAULT_COLOR = 0xFFFFFF;

	public WindProjectileEntity(EntityType<? extends WindProjectileEntity> type, World world){
		super(type, world);
	}

	public WindProjectileEntity(EntityType<? extends WindProjectileEntity> type, double x, double y, double z, World world){
		this(type, world);
		this.setPos(x, y, z);
	}

	public WindProjectileEntity(EntityType<? extends WindProjectileEntity> type, LivingEntity entity){
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
