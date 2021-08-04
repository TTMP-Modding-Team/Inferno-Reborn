package ttmp.infernoreborn.contents.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.UUID;

public class ItemCloudScarf extends Item implements ICurioItem{
	public ItemCloudScarf(Properties properties){
		super(properties);
	}

	@Override public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack){
		ICurioItem.super.curioTick(identifier, index, livingEntity, stack);
		Vector3d movement = livingEntity.getDeltaMovement();
		if(livingEntity.isCrouching()&&movement.y<=0.75&&isBlockFlyableBelow(livingEntity, 4.0)){
			double velocity = (-movement.y+4)/20;
			;
			if(velocity>0) livingEntity.setDeltaMovement(movement.x, velocity, movement.z);
		}
		livingEntity.fallDistance = 0;
	}
	@Override public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack){
		Multimap<Attribute, AttributeModifier> multimap = ICurioItem.super.getAttributeModifiers(slotContext, uuid, stack);
		System.out.println(slotContext.getIdentifier()+"qewr"+slotContext.getIndex());
		if(slotContext.getIdentifier().equals("necklace"))
			multimap.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(UUID.fromString("0eed5b47-e0c2-4653-9f36-128db5d39ffe"), "Armor Modifier", 0.35, AttributeModifier.Operation.MULTIPLY_TOTAL));
		return multimap;
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
