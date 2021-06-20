package ttmp.infernoreborn.contents.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import vazkii.patchouli.api.PatchouliAPI;

import static ttmp.infernoreborn.InfernoReborn.MODID;

/**
 * The book. You know what it is.
 */
public class TheBookItem extends Item{
	public static final ResourceLocation BOOK = new ResourceLocation(MODID, "book_of_the_unspeakable");

	public TheBookItem(Properties properties){
		super(properties);
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
		ItemStack stack = player.getItemInHand(hand);
		if(player instanceof ServerPlayerEntity)
			PatchouliAPI.get().openBookGUI((ServerPlayerEntity)player, BOOK);
		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}
}
