package ttmp.infernoreborn.contents.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import ttmp.infernoreborn.util.ExpandKey;
import vazkii.patchouli.api.PatchouliAPI;

import javax.annotation.Nullable;
import java.util.List;

public class EssenceHolderBookItem extends EssenceHolderItem{
	public EssenceHolderBookItem(Properties properties){
		super(properties);
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
		ItemStack stack = player.getItemInHand(hand);
		if(player instanceof ServerPlayerEntity){
			if(player.isCrouching()) openGui(player, hand);
			else PatchouliAPI.get().openBookGUI((ServerPlayerEntity)player, TheBookItem.BOOK);
		}
		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flag){
		text.add(new TranslationTextComponent("item.infernoreborn.book_of_the_unspeakable.desc.essence_holder").setStyle(Style.EMPTY.applyFormat(TextFormatting.YELLOW)));
		if(ExpandKey.SHIFT.isKeyDown()) listEssences(stack, text);
		else text.add(ExpandKey.SHIFT.getCollapsedText());
	}
}
