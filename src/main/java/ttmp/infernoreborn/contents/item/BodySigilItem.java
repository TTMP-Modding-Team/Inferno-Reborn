package ttmp.infernoreborn.contents.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import ttmp.infernoreborn.api.sigil.Sigil;
import ttmp.infernoreborn.api.sigil.SigilHolder;

public class BodySigilItem extends SigilItem{
	public BodySigilItem(Properties properties){
		super(properties);
	}

	@Override public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
		ItemStack itemInHand = player.getItemInHand(hand);
		Sigil sigil = getSigil(itemInHand);
		if(sigil==null) return ActionResult.fail(itemInHand);
		if(!world.isClientSide){
			SigilHolder h = SigilHolder.of(player);
			if(h==null||!h.add(sigil)) return ActionResult.fail(itemInHand);
		}
		return ActionResult.sidedSuccess(itemInHand, world.isClientSide);
	}
}
