package ttmp.infernoreborn.contents.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import ttmp.infernoreborn.api.LivingUtils;

public class CylinderTestItem extends Item{
	public CylinderTestItem(Properties p){
		super(p);
	}

	@Override public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand){
		if(!level.isClientSide){
			if(player.isCrouching()) LivingUtils.forEachLivingEntitiesInCylinder(player, 10, 10,
					e -> e.removeEffect(Effects.GLOWING));
			else LivingUtils.forEachLivingEntitiesInCylinder(player, 5, 10,
					e -> e.addEffect(new EffectInstance(Effects.GLOWING, 1000)));
		}
		return ActionResult.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
	}
}
