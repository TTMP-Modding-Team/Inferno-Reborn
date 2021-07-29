package ttmp.infernoreborn.contents.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import ttmp.infernoreborn.capability.PlayerCapability;

public class JudgementItem extends Item{
	private static final int JUDGEMENT_COOLDOWN = 72000;

	public JudgementItem(Properties properties){
		super(properties);
	}

	@Override public void inventoryTick(ItemStack stack, World level, Entity entity, int slot, boolean selected){
		if(level.isClientSide) return;
		PlayerCapability cap = PlayerCapability.of(entity);
		boolean isJudgementOff = cap==null||cap.hasJudgementCooldown();
		if(isOff(stack)!=isJudgementOff) setOff(stack, isJudgementOff);
	}

	@Override public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand){
		ItemStack stack = player.getItemInHand(hand);
		if(level.isClientSide) return ActionResult.success(stack);
		PlayerCapability cap = PlayerCapability.of(player);
		if(cap!=null&&!cap.hasJudgementCooldown()){
			// TODO activate THE JUDGEMENT
			cap.setJudgementCooldown(JUDGEMENT_COOLDOWN);
		}
		return ActionResult.consume(stack);
	}

	public static boolean isOff(ItemStack stack){
		CompoundNBT tag = stack.getTag();
		return tag!=null&&tag.getBoolean("Off");
	}

	public static void setOff(ItemStack stack, boolean off){
		CompoundNBT tag = stack.getTag();
		if(off){
			if(tag==null) stack.setTag(tag = new CompoundNBT());
			tag.putBoolean("Off", true);
		}else if(tag!=null){
			tag.remove("Off");
			if(tag.isEmpty()) stack.setTag(null);
		}
	}
}
