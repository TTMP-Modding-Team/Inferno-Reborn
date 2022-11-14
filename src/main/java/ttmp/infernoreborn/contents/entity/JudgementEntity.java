package ttmp.infernoreborn.contents.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import ttmp.infernoreborn.api.LivingUtils;
import ttmp.infernoreborn.api.ability.AbilityHolder;
import ttmp.infernoreborn.contents.ModEntities;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JudgementEntity extends Entity{
	public static final double MAX_RADIUS = 6*16;
	public static final int TICKS = 40;

	@Nullable private List<MobEntry> removeEntityList;
	private int nextEntry;

	public JudgementEntity(EntityType<JudgementEntity> type, World world){
		super(type, world);
		this.noPhysics = true;
		this.setNoGravity(true);
	}
	public JudgementEntity(World world, Vector3d pos){
		this(ModEntities.JUDGEMENT.get(), world);
		this.setPos(pos.x(), pos.y(), pos.z());
	}

	@Override protected void defineSynchedData(){}
	@Override public IPacket<?> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public double getRadius(){
		return getRadius(0);
	}
	public double getRadius(float partialTicks){
		return Math.max(1, (this.tickCount+partialTicks)/TICKS)*MAX_RADIUS;
	}

	@Override public void tick(){
		super.tick();
		if(level.isClientSide) return;
		if(tickCount>=TICKS){
			this.remove();
			return;
		}
		if(removeEntityList==null){
			removeEntityList = getTargetEntities();
			nextEntry = 0;
		}
		while(nextEntry<removeEntityList.size()){
			MobEntry e = removeEntityList.get(nextEntry);
			if(tickCount<e.tick) break;
			e.entity.remove();
			nextEntry++;
		}
	}

	private List<MobEntry> getTargetEntities(){
		List<MobEntry> resultList = new ArrayList<>();
		LivingUtils.forEachLivingEntitiesInCylinder(this, MAX_RADIUS, Double.POSITIVE_INFINITY, e -> {
			if(!e.isAlive()) return;
			AbilityHolder h = AbilityHolder.of(e);
			if(h==null||h.getAbilities().isEmpty()) return;
			double dx = this.getX()-e.getX();
			double dz = this.getZ()-e.getZ();
			double dist = Math.sqrt(dx*dx+dz*dz);
			double percentage = dist/MAX_RADIUS;
			resultList.add(new MobEntry(e, (int)Math.ceil(percentage*TICKS)));
		});
		resultList.sort(Comparator.naturalOrder());
		return resultList;
	}

	@Override protected void readAdditionalSaveData(CompoundNBT tag){
		this.tickCount = tag.getInt("Age");
	}
	@Override protected void addAdditionalSaveData(CompoundNBT tag){
		tag.putInt("Age", this.tickCount);
	}

	@Override public boolean ignoreExplosion(){
		return true;
	}
	@Override public boolean isInvisible(){
		return true;
	}

	private static final class MobEntry implements Comparable<MobEntry>{
		private final LivingEntity entity;
		private final int tick;

		public MobEntry(LivingEntity entity, int tick){
			this.entity = entity;
			this.tick = tick;
		}

		@Override public int compareTo(MobEntry o){
			return Double.compare(tick, o.tick);
		}
		@Override public String toString(){
			return "MobEntry{"+
					"entity="+entity+
					", tick="+tick+
					'}';
		}
	}
}
