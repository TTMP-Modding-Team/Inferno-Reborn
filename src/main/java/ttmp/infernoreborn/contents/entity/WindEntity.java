package ttmp.infernoreborn.contents.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;
import ttmp.infernoreborn.contents.ModEntities;
import ttmp.infernoreborn.util.damage.Damages;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WindEntity extends BaseProjectileEntity{
	private static final DataParameter<Integer> DATA_COLOR = EntityDataManager.defineId(WindEntity.class, DataSerializers.INT);
	private static final int DEFAULT_COLOR = 0xFFFFFF;

	private float damage;
	private int fireSecondsOnHit;

	@Nullable private List<EffectInstance> effects;

	public WindEntity(World world){
		super(ModEntities.WIND.get(), world);
	}
	public WindEntity(World world, float damage){
		super(ModEntities.WIND.get(), world);
		setDamage(damage);
	}
	public WindEntity(World world, float damage, int fireSecondsOnHit){
		super(ModEntities.WIND.get(), world);
		setDamage(damage);
		setFireSecondsOnHit(fireSecondsOnHit);
	}
	public WindEntity(World world, float damage, EffectInstance... effects){
		super(ModEntities.WIND.get(), world);
		setDamage(damage);
		for(EffectInstance effect : effects){
			addEffect(effect);
		}
	}
	public WindEntity(EntityType<? extends WindEntity> type, World world){
		super(type, world);
	}

	@Override public boolean displayFireAnimation(){
		return false;
	}

	public float getDamage(){
		return this.damage;
	}
	public void setDamage(float damage){
		this.damage = damage;
	}

	public int getFireSecondsOnHit(){
		return fireSecondsOnHit;
	}
	public void setFireSecondsOnHit(int fireSecondsOnHit){
		this.fireSecondsOnHit = fireSecondsOnHit;
	}

	public int getColor(){
		return this.entityData.get(DATA_COLOR);
	}
	public void setColor(int color){
		this.entityData.set(DATA_COLOR, color);
	}

	public List<EffectInstance> getEffects(){
		return effects!=null ? Collections.unmodifiableList(effects) : Collections.emptyList();
	}
	public void addEffect(EffectInstance effect){
		if(effects==null) effects = new ArrayList<>();
		effects.add(effect);
	}

	@Override protected void onHitEntity(EntityRayTraceResult result){
		Entity entity = result.getEntity();
		if(entity instanceof LivingEntity){
			LivingEntity living = (LivingEntity)entity;
			living.hurt(Damages.wind(this), this.damage);
			if(this.fireSecondsOnHit>0) living.setSecondsOnFire(fireSecondsOnHit);
			if(effects!=null) for(EffectInstance effect : effects){
				living.addEffect(new EffectInstance(effect));
			}
		}
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

	@Override protected void addAdditionalSaveData(CompoundNBT nbt){
		super.addAdditionalSaveData(nbt);
		if(damage>0) nbt.putFloat("Damage", damage);
		if(fireSecondsOnHit>0) nbt.putInt("FireSecondsOnHit", fireSecondsOnHit);
		if(effects!=null){
			ListNBT list = new ListNBT();
			for(EffectInstance effect : effects)
				list.add(effect.save(new CompoundNBT()));
			nbt.put("Effects", list);
		}
	}
	@Override protected void readAdditionalSaveData(CompoundNBT nbt){
		super.readAdditionalSaveData(nbt);
		this.damage = nbt.getFloat("Damage");
		this.fireSecondsOnHit = nbt.getInt("FireSecondsOnHit");
		if(nbt.contains("Effects", Constants.NBT.TAG_LIST)){
			ListNBT list = nbt.getList("Effects", Constants.NBT.TAG_COMPOUND);
			this.effects = new ArrayList<>();
			for(int i = 0; i<list.size(); i++)
				this.effects.add(EffectInstance.load(list.getCompound(i)));
		}else this.effects = null;
	}
}
