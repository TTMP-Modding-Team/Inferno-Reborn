package ttmp.infernoreborn.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import ttmp.infernoreborn.capability.EssenceHolder;
import ttmp.infernoreborn.container.EssenceHolderContainer;
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
		if(!world.isClientSide){
			int holderSlot;
			switch(hand){
				case MAIN_HAND:
					holderSlot = player.inventory.selected;
					break;
				case OFF_HAND:
					holderSlot = player.inventory.items.size()+player.inventory.armor.size();
					break;
				default:
					throw new IllegalStateException("Three hands PogU");
			}
			stack.getCapability(EssenceHolder.capability).ifPresent(essenceHolder -> player.openMenu(new INamedContainerProvider(){
				@Override public ITextComponent getDisplayName(){
					return new TranslationTextComponent("container.infernoreborn.essence_holder");
				}
				@Override public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player){
					EssenceHolderContainer c = new EssenceHolderContainer(id, playerInventory);
					c.setHolderSlot(holderSlot);
					return c;
				}
			}));
		}
		return ActionResult.success(stack);
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flags){
		boolean allCollapsed = true;
		if(ExpandKey.SHIFT.isKeyDown()){
			allCollapsed = false;
			stack.getCapability(EssenceHolder.capability).ifPresent(essenceHolder -> {
				for(EssenceType type : EssenceType.values()){
					int essence = essenceHolder.getEssence(type);
					if(essence>0) text.add(new TranslationTextComponent("item.infernoreborn.essence_holder.desc.essences."+type.id, essence));
				}
			});
		}
		if(ExpandKey.CTRL.isKeyDown()){
			allCollapsed = false;
			text.add(new TranslationTextComponent("item.infernoreborn.essence_holder.desc.0"));
		}
		if(allCollapsed){
			text.add(ExpandKey.SHIFT.getCollapsedText());
			text.add(ExpandKey.CTRL.getCollapsedText());
		}
	}

	@Nullable @Override public CompoundNBT getShareTag(ItemStack stack){
		CompoundNBT nbt = stack.getTag();
		@SuppressWarnings("ConstantConditions")
		EssenceHolder h = stack.getCapability(EssenceHolder.capability).orElse(null);
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
		stack.getCapability(EssenceHolder.capability).ifPresent(h -> {
			if(nbt!=null) h.deserializeNBT(nbt.getCompound("Essence"));
			else h.clear();
		});
	}
}
