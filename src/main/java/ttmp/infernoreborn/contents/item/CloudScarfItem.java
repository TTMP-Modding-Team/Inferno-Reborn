package ttmp.infernoreborn.contents.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import ttmp.infernoreborn.contents.ModAttributes;

import java.util.UUID;

import static net.minecraft.util.math.RayTraceContext.BlockMode.COLLIDER;
import static net.minecraft.util.math.RayTraceContext.FluidMode.ANY;

public class CloudScarfItem extends Item implements ICurioItem{
	private static final UUID ATTRIBUTE_ID = UUID.fromString("0eed5b47-e0c2-4653-9f36-128db5d39ffe");

	public CloudScarfItem(Properties properties){
		super(properties);
	}

	@Override public void curioTick(String identifier, int index, LivingEntity entity, ItemStack stack){
		if(!entity.jumping) return;

		double force = getPropulsion(entity, 4);
		if(force>0){
			Vector3d movement = entity.getDeltaMovement();
			entity.setDeltaMovement(movement.x, movement.y+force/20, movement.z);
		}
		if(entity instanceof ServerPlayerEntity)
			((ServerPlayerEntity)entity).connection.aboveGroundTickCount = 0;
	}

	@Override public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack){
		return slotContext.getIdentifier().equals("necklace") ?
				ImmutableMultimap.of(ModAttributes.FALLING_DAMAGE_RESISTANCE.get(), new AttributeModifier(ATTRIBUTE_ID, "Cloud Scarf", 1, Operation.MULTIPLY_BASE)) :
				ImmutableMultimap.of();
	}

	@Override public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack){
		return true;
	}

	/**
	 * Get propulsion force that decreases based on distance between ground and entity.
	 *
	 * @param entity        Entity.
	 * @param atGroundLevel Propulsion force at ground level (=distance 0)
	 * @return Calculated propulsion force, {@code 0 ~ atGroundLevel}.
	 */
	public static double getPropulsion(LivingEntity entity, double atGroundLevel){
		if(entity.isOnGround()||entity.isInWall()) return atGroundLevel;
		BlockRayTraceResult result = entity.level.clip(new RayTraceContext(entity.position(), entity.position().subtract(0, atGroundLevel, 0), COLLIDER, ANY, entity));
		if(result.getType()!=RayTraceResult.Type.BLOCK) return 0;

		// Y'all like some math?
		double distancePercentage = (entity.getY()-result.getLocation().y)/atGroundLevel;
		return atGroundLevel*(distancePercentage*2-distancePercentage*distancePercentage);
	}
}
