package ttmp.infernoreborn.contents.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import ttmp.infernoreborn.capability.PlayerCapability;
import ttmp.infernoreborn.config.ModCfg;

public class HeartCrystalItem extends Item{
	public HeartCrystalItem(Properties properties){
		super(properties);
	}

	@Override public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
		ItemStack itemInHand = player.getItemInHand(hand);
		if(!world.isClientSide){
			PlayerCapability c = PlayerCapability.of(player);
			if(c==null||c.getHeartCrystal()>=ModCfg.maxHeartCrystals()) return ActionResult.fail(itemInHand);
			c.setHeartCrystal(c.getHeartCrystal()+1);
			itemInHand.shrink(1);
		}
		return ActionResult.sidedSuccess(itemInHand, world.isClientSide);
	}
}
