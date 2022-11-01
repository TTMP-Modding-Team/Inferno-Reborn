package ttmp.infernoreborn.contents.item.ability;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;
import ttmp.infernoreborn.network.AbilityColorPickerMsg;
import ttmp.infernoreborn.network.ModNet;

import javax.annotation.Nullable;
import java.util.List;

public class AbilityColorPickerItem extends Item{
	public AbilityColorPickerItem(Properties p){
		super(p);
	}

	@Override public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand){
		if(!level.isClientSide&&player instanceof ServerPlayerEntity){
			ItemStack stack = player.getItemInHand(hand);
			ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player),
					new AbilityColorPickerMsg(hand==Hand.MAIN_HAND ?
							player.inventory.selected : player.inventory.items.size()+player.inventory.armor.size(),
							getPrimaryColor(stack),
							getSecondaryColor(stack),
							getHighlightColor(stack)));
		}
		return ActionResult.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag){
		int primaryColor = getPrimaryColor(stack)&0xFFFFFF;
		int secondaryColor = getSecondaryColor(stack)&0xFFFFFF;
		int highlightColor = getHighlightColor(stack)&0xFFFFFF;
		if(primaryColor!=0xFFFFFF) append(tooltip, primaryColor, "Primary");
		if(secondaryColor!=0xFFFFFF) append(tooltip, secondaryColor, "Secondary");
		if(primaryColor!=highlightColor) append(tooltip, highlightColor, "Highlight");
	}

	private static void append(List<ITextComponent> tooltip, int color, String name){
		tooltip.add(new StringTextComponent(name+": ")
				.withStyle(TextFormatting.GRAY)
				.append(new StringTextComponent(String.format("#%06x", color))
						.withStyle(Style.EMPTY.withColor(Color.fromRgb(color)))));
	}

	public static int getPrimaryColor(ItemStack stack){
		CompoundNBT tag = stack.getTag();
		return tag!=null&&tag.contains("PrimaryColor", Constants.NBT.TAG_INT) ?
				tag.getInt("PrimaryColor") : 0xFFFFFF;
	}

	public static int getSecondaryColor(ItemStack stack){
		CompoundNBT tag = stack.getTag();
		return tag!=null&&tag.contains("SecondaryColor", Constants.NBT.TAG_INT) ?
				tag.getInt("SecondaryColor") : 0xFFFFFF;
	}

	public static int getHighlightColor(ItemStack stack){
		CompoundNBT tag = stack.getTag();
		return tag!=null&&tag.contains("HighlightColor", Constants.NBT.TAG_INT) ?
				tag.getInt("HighlightColor") : getPrimaryColor(stack);
	}

	public static void set(ItemStack stack, int primary, int secondary, int highlight){
		primary &= 0xFFFFFF;
		secondary &= 0xFFFFFF;
		highlight &= 0xFFFFFF;
		if(primary==0xFFFFFF&&secondary==0xFFFFFF&&highlight==0xFFFFFF) return;
		CompoundNBT tag = stack.getOrCreateTag();
		if(primary!=0xFFFFFF) tag.putInt("PrimaryColor", primary);
		else tag.remove("PrimaryColor");
		if(secondary!=0xFFFFFF) tag.putInt("SecondaryColor", secondary);
		else tag.remove("SecondaryColor");
		if(highlight!=primary) tag.putInt("HighlightColor", highlight);
		else tag.remove("HighlightColor");
	}
}
