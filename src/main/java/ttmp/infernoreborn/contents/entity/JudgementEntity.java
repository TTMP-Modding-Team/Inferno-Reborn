package ttmp.infernoreborn.contents.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JudgementEntity extends Entity{

	private float radius = 0;
	private final float EXTEND_SPEED = 0.1f;
	private final int MAX_RADIUS = 30;
	private final List<MobEntry> removeEntityList;
	private int currentIndex = 0;
	public JudgementEntity(EntityType<JudgementEntity> type, World world){
		super(type, world);
		removeEntityList = getTargetEntities();
		removeEntityList.sort(Comparator.naturalOrder());
	}

	public JudgementEntity(EntityType<JudgementEntity> type, World world, Vector3d pos){
		super(type, world);
		this.setPos(pos.x(), pos.y(), pos.z());
		removeEntityList = getTargetEntities();
		removeEntityList.sort(Comparator.naturalOrder());
	}
	@Override protected void defineSynchedData(){}
	@Override protected void readAdditionalSaveData(CompoundNBT pCompound){}
	@Override protected void addAdditionalSaveData(CompoundNBT pCompound){}
	@Override public IPacket<?> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override public void tick(){
		super.tick();
		if(radius>=MAX_RADIUS) this.remove();
		radius += EXTEND_SPEED;
		if(removeEntityList.size()==currentIndex+1) return;
		if(tickCount>=removeEntityList.get(currentIndex).getTick()){
			removeEntityList.get(currentIndex).getEntity().remove();
			currentIndex += 1;
		}
	}

	public float getRadius(){
		return this.radius;
	}

	private List<MobEntry> getTargetEntities(){
		List<MobEntry> resultList = new ArrayList<>();
		int range = (MAX_RADIUS/16)+1;
		int squareLength = 2*range+1;
		World world = this.level;
		for(int i = 1; i<=Math.pow(squareLength, 2); i++){
			Chunk chunk = world.getChunk(this.xChunk-range+(i/squareLength), this.zChunk-range+(i%squareLength));
			for(int k = 0; k<chunk.getEntitySections().length; ++k){
				for(Entity e : chunk.getEntitySections()[k]){
					if(e instanceof LivingEntity){
						double distanceX = e.getX()-this.getX();
						double distanceZ = e.getZ()-this.getZ();
						double distanceSquare = distanceX*distanceX+distanceZ*distanceZ;
						if(radius*radius>=distanceSquare)
							resultList.add(new MobEntry((LivingEntity)e, Math.sqrt(distanceSquare)/EXTEND_SPEED+(double)this.tickCount));

					}
				}
			}
		}
		return resultList;
	}
}

class MobEntry implements Comparable<MobEntry>{
	private final LivingEntity e;
	private final double tick;

	public MobEntry(LivingEntity e, double tick){
		this.e = e;
		this.tick = tick;
	}

	public LivingEntity getEntity(){
		return e;
	}

	public double getTick(){
		return tick;
	}

	@Override public int compareTo(MobEntry o){
		return Double.compare(this.getTick(), o.getTick());
	}
}
