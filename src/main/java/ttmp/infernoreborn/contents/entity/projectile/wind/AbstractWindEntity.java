package ttmp.infernoreborn.contents.entity.projectile.wind;

import net.minecraft.entity.EntityType;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import ttmp.infernoreborn.contents.entity.projectile.BaseProjectileEntity;

public abstract class AbstractWindEntity extends BaseProjectileEntity {
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
