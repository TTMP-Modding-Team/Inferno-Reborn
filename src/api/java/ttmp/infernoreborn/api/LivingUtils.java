package ttmp.infernoreborn.api;

import com.google.common.collect.ListMultimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public final class LivingUtils{
	private LivingUtils(){}

	private static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	private static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{
			UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
			UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
			UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
			UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")
	};

	public static UUID getAttackDamageId(){
		return BASE_ATTACK_DAMAGE_UUID;
	}
	public static UUID getAttackSpeedId(){
		return BASE_ATTACK_SPEED_UUID;
	}
	public static UUID getArmorModifierId(EquipmentSlotType type){
		if(type.getType()!=EquipmentSlotType.Group.ARMOR) throw new IllegalArgumentException("type");
		return ARMOR_MODIFIER_UUID_PER_SLOT[type.getIndex()];
	}

	public static void addStackEffect(LivingEntity entity,
	                                  Effect effect,
	                                  int duration,
	                                  int startAmplifier,
	                                  int amplifierGrow,
	                                  int maxAmplifier){
		addStackEffect(entity, effect, duration, startAmplifier, amplifierGrow, maxAmplifier, true, true);
	}

	public static void addStackEffect(LivingEntity entity,
	                                  Effect effect,
	                                  int duration,
	                                  int startAmplifier,
	                                  int amplifierGrow,
	                                  int maxAmplifier,
	                                  boolean visible,
	                                  boolean showIcon){
		EffectInstance instance = entity.getEffect(effect);
		if(instance==null){
			entity.addEffect(new EffectInstance(effect, duration, startAmplifier, false, visible, showIcon));
		}else if(instance.getAmplifier()<=maxAmplifier&&instance.getDuration()<duration){
			entity.addEffect(new EffectInstance(effect,
					duration,
					Math.min(instance.getAmplifier()+amplifierGrow, maxAmplifier),
					false,
					visible,
					showIcon));
		}
	}

	@Nullable public static LivingEntity getTarget(LivingEntity entity){
		if(entity instanceof MobEntity) return ((MobEntity)entity).getTarget();
		else if(entity instanceof IAngerable) return ((IAngerable)entity).getTarget();
		else return null;
	}

	public static void addToModifier(ListMultimap<Attribute, AttributeModifier> attributeMap, Attribute attribute, @Nullable UUID targetUuid, double amount, AttributeModifier.Operation operation){
		addToModifier(attributeMap, attribute, targetUuid, targetUuid, amount, operation);
	}
	public static void addToModifier(ListMultimap<Attribute, AttributeModifier> attributeMap, Attribute attribute, @Nullable UUID targetUuid, @Nullable UUID fallbackUuid, double amount, AttributeModifier.Operation operation){
		List<AttributeModifier> list = attributeMap.get(attribute);
		int modifyTargetIndex = -1;
		for(int i = 0; i<list.size(); i++){
			AttributeModifier m = list.get(i);
			if(m.getOperation()!=operation) continue;
			modifyTargetIndex = i;
			if(targetUuid!=null&&m.getId().equals(targetUuid)) break;
		}
		if(modifyTargetIndex>=0){
			AttributeModifier m = list.get(modifyTargetIndex);
			list.set(modifyTargetIndex, new AttributeModifier(m.getId(), m.getName(), m.getAmount()+amount, m.getOperation()));
		}else
			attributeMap.put(attribute, new AttributeModifier(fallbackUuid!=null ? fallbackUuid : UUID.randomUUID(), "", amount, operation));
	}

	public static void forEachLivingEntitiesInCylinder(Entity entity, double radius, double height, Consumer<LivingEntity> consumer){
		forEachLivingEntitiesInCylinder(entity.level, entity.getX(), entity.getY(), entity.getZ(), radius, height, entity, consumer);
	}
	public static void forEachLivingEntitiesInCylinder(IWorldReader level, double x, double y, double z, double radius, double height, @Nullable Entity excludedEntity, Consumer<LivingEntity> consumer){
		int chunkMinX = MathHelper.floor(x-radius) >> 4;
		int chunkMaxX = MathHelper.floor(x+radius) >> 4;
		int chunkMinZ = MathHelper.floor(z-radius) >> 4;
		int chunkMaxZ = MathHelper.floor(z+radius) >> 4;
		for(int cx = chunkMinX; cx<=chunkMaxX; cx++){
			for(int cz = chunkMinZ; cz<=chunkMaxZ; cz++){
				IChunk chunk = level.getChunk(cx, cz, ChunkStatus.FULL, false);
				if(!(chunk instanceof Chunk)) continue;
				for(ClassInheritanceMultiMap<Entity> section : ((Chunk)chunk).getEntitySections()){
					for(Entity e : section){
						if(e instanceof LivingEntity&&e!=excludedEntity){
							double distanceX = e.getX()-x;
							double distanceY = Math.abs(e.getY()-y);
							double distanceZ = e.getZ()-z;
							if(radius*radius>=distanceX*distanceX+distanceZ*distanceZ&&distanceY<=height/2)
								consumer.accept((LivingEntity)e);
						}
					}
				}
			}
		}
	}
}
