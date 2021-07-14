package ttmp.infernoreborn.contents.entity;

import com.google.common.collect.Lists;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import ttmp.infernoreborn.contents.ModEntities;

public class AnvilEntity extends Entity{
	protected static final DataParameter<BlockPos> DATA_START_POS = EntityDataManager.defineId(AnvilEntity.class, DataSerializers.BLOCK_POS);

	public AnvilEntity(EntityType<? extends AnvilEntity> type, World world){
		super(type, world);
	}

	public AnvilEntity(World world, double x, double y, double z){
		this(ModEntities.ANVIL.get(), world);
		this.blocksBuilding = true;
		this.setPos(x, y+(double)((1.0F-this.getBbHeight())/2.0F), z);
		this.setDeltaMovement(Vector3d.ZERO);
		this.xo = x;
		this.yo = y;
		this.zo = z;
		this.setStartPos(this.blockPosition());
	}

	@Override
	public boolean isAttackable(){
		return true;
	}

	public void setStartPos(BlockPos p_184530_1_){
		this.entityData.set(DATA_START_POS, p_184530_1_);
	}

	public BlockPos getStartPos(){
		return this.entityData.get(DATA_START_POS);
	}

	@Override
	protected boolean isMovementNoisy(){
		return false;
	}

	@Override
	protected void defineSynchedData(){
		this.entityData.define(DATA_START_POS, BlockPos.ZERO);
	}

	@Override
	public boolean isPickable(){
		return !this.removed;
	}
	@Override protected void readAdditionalSaveData(CompoundNBT nbt){}
	@Override protected void addAdditionalSaveData(CompoundNBT nbt){}

	public void tick(){
		if(!this.isNoGravity())
			this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));

		this.move(MoverType.SELF, this.getDeltaMovement());
		if(!this.level.isClientSide){
			if(!this.onGround){
				if(this.getY()<1||this.getY()>256)
					this.remove();
			}else{
				this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
				if(!this.isSilent())
					this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ANVIL_LAND, SoundCategory.BLOCKS, 0.3F, this.level.random.nextFloat()*0.1F+0.9F);
				this.remove();
			}
		}

		this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
	}

	public boolean causeFallDamage(float fallDistance, float p_225503_2_){
		int i = MathHelper.ceil(fallDistance-1.0F);
		if(i>0){
			List<Entity> list = Lists.newArrayList(this.level.getEntities(this, this.getBoundingBox()));

			for(Entity entity : list){
				entity.hurt(DamageSource.ANVIL, (float)Math.min(MathHelper.floor((float)i*2.0F), 40));
			}
		}
		return false;
	}


	@Override
	public IPacket<?> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
