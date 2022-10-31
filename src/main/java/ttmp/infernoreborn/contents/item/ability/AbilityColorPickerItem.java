package ttmp.infernoreborn.contents.item.ability;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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
							getPrimaryColor(stack, 0xFFFFFF),
							getSecondaryColor(stack, 0xFFFFFF),
							getHighlightColor(stack, 0xFFFFFF)));
		}
		return ActionResult.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag){
		for(int c : new int[]{
				getPrimaryColor(stack, 0xFFFFFF),
				getSecondaryColor(stack, 0xFFFFFF),
				getHighlightColor(stack, 0xFFFFFF)
		}){
			c &= 0xFFFFFF;
			if(c!=0xFFFFFF) tooltip.add(new StringTextComponent(String.format("%06x", c)));
		}
	}

	public static int getPrimaryColor(ItemStack stack, int fallback){
		CompoundNBT tag = stack.getTag();
		return tag!=null&&tag.contains("PrimaryColor", Constants.NBT.TAG_INT) ?
				tag.getInt("PrimaryColor") : fallback;
	}

	public static int getSecondaryColor(ItemStack stack, int fallback){
		CompoundNBT tag = stack.getTag();
		return tag!=null&&tag.contains("SecondaryColor", Constants.NBT.TAG_INT) ?
				tag.getInt("SecondaryColor") : fallback;
	}

	public static int getHighlightColor(ItemStack stack, int fallback){
		CompoundNBT tag = stack.getTag();
		return tag!=null&&tag.contains("HighlightColor", Constants.NBT.TAG_INT) ?
				tag.getInt("HighlightColor") : fallback;
	}

	public static void set(ItemStack stack, int primary, int secondary, int highlight){
		primary &= 0xFFFFFF;
		secondary &= 0xFFFFFF;
		highlight &= 0xFFFFFF;
		if(primary==0xFFFFFF&&secondary==0xFFFFFF&&highlight==0xFFFFFF) return;
		CompoundNBT tag = stack.getOrCreateTag();
		tag.putInt("PrimaryColor", primary);
		tag.putInt("SecondaryColor", secondary);
		tag.putInt("HighlightColor", highlight);
	}
}
