package ttmp.infernoreborn.contents.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import ttmp.infernoreborn.capability.Caps;
import ttmp.infernoreborn.capability.EssenceHolder;
import ttmp.infernoreborn.contents.container.EssenceHolderContainerProvider;
import ttmp.infernoreborn.util.EssenceType;
import ttmp.infernoreborn.util.ExpandKey;

import javax.annotation.Nullable;
import java.util.List;

public class EssenceHolderItem extends Item{
	public EssenceHolderItem(Properties properties){
		super(properties);
	}

	@Nullable @Override public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt){
		return new EssenceHolder();
	}

	@Override public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
		ItemStack stack = player.getItemInHand(hand);
		if(!world.isClientSide) openGui(player, hand);
		return ActionResult.success(stack);
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flags){
		text.add(new TranslationTextComponent("item.infernoreborn.essence_holder.desc.0"));
		if(ExpandKey.SHIFT.isKeyDown()) listEssences(stack, text);
		else text.add(ExpandKey.SHIFT.getCollapsedText());
	}

	protected void openGui(PlayerEntity player, Hand hand){
		ItemStack stack = player.getItemInHand(hand);
		player.openMenu(new EssenceHolderContainerProvider(stack.getHoverName(), player, hand));
	}

	protected void listEssences(ItemStack stack, List<ITextComponent> text){
		stack.getCapability(Caps.essenceHolder).ifPresent(essenceHolder -> {
			for(EssenceType type : EssenceType.values()){
				int essence = essenceHolder.getEssence(type);
				if(essence>0) text.add(new TranslationTextComponent("item.infernoreborn.essence_holder.desc.essences."+type.id, essence));
			}
		});
	}

	@Nullable @Override public CompoundNBT getShareTag(ItemStack stack){
		CompoundNBT nbt = stack.getTag();
		@SuppressWarnings("ConstantConditions")
		EssenceHolder h = stack.getCapability(Caps.essenceHolder).orElse(null);
		//noinspection ConstantConditions
		if(h!=null){
			CompoundNBT capNbt = h.serializeNBT();
			if(!capNbt.isEmpty()){
				if(nbt==null) nbt = new CompoundNBT();
				nbt.put("Essence", capNbt);
			}
		}
		return nbt;
	}

	@Override public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt){
		stack.setTag(nbt);
		stack.getCapability(Caps.essenceHolder).ifPresent(h -> {
			if(nbt!=null) h.deserializeNBT(nbt.getCompound("Essence"));
			else h.clear();
		});
	}
}
