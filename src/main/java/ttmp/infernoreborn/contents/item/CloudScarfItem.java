package ttmp.infernoreborn.contents.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.UUID;

public class CloudScarfItem extends Item implements ICurioItem{
	private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("0eed5b47-e0c2-4653-9f36-128db5d39ffe");

	public CloudScarfItem(Properties properties){
		super(properties);
	}

	@Override public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack){
		Vector3d movement = livingEntity.getDeltaMovement();

		if(!livingEntity.isCrouching()&&movement.y<=0.75&&isBlockFlyableBelow(livingEntity, 4.0)){
			double velocity = (-movement.y+4)/20;
			if(velocity>0) livingEntity.setDeltaMovement(movement.x, velocity, movement.z);
		}
		livingEntity.fallDistance = 0;
		if(livingEntity instanceof ServerPlayerEntity)
			((ServerPlayerEntity)livingEntity).connection.aboveGroundTickCount = 0;
	}

	@Override public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack){
		Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();
		if(slotContext.getIdentifier().equals("necklace"))
			multimap.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(MOVEMENT_SPEED_UUID, "Armor Modifier", 0.35, AttributeModifier.Operation.MULTIPLY_TOTAL));
		return multimap;
	}

	@Override public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack){
		return true;
	}

	public static boolean isBlockFlyableBelow(LivingEntity livingEntity, double height){
		World world = livingEntity.level;
		if(livingEntity.isOnGround()||livingEntity.isInWall()) return true;
		double heightCache = livingEntity.getY()-((int)livingEntity.getY())-1;
		for(int i = 0; heightCache+i<height; i++){
			Material material = world.getBlockState(livingEntity.blockPosition().below(i)).getMaterial();
			if(material.blocksMotion()||material.isLiquid()) return true;
		}
		return false;
	}
}
