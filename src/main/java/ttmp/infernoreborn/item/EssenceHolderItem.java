package ttmp.infernoreborn.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import ttmp.infernoreborn.capability.EssenceHolder;
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

	@Override public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flags){
		boolean allCollapsed = true;
		if(ExpandKey.SHIFT.isKeyDown()){
			allCollapsed = false;
			text.add(new TranslationTextComponent("item.infernoreborn.essence_holder.desc.0"));
		}
		if(ExpandKey.CTRL.isKeyDown()){
			allCollapsed = false;

		}
		if(allCollapsed){
			text.add(ExpandKey.SHIFT.getCollapsedText());
			text.add(ExpandKey.CTRL.getCollapsedText());
		}
	}
}
