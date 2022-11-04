package ttmp.infernoreborn.contents.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import ttmp.infernoreborn.api.Caps;
import ttmp.infernoreborn.api.essence.EssenceHolder;

import javax.annotation.Nullable;
import java.util.Objects;

public class EssenceHolderItemContainerProvider implements INamedContainerProvider{
	private final ITextComponent displayName;
	private final int holderSlot;

	public EssenceHolderItemContainerProvider(ITextComponent displayName, PlayerEntity player, Hand hand){
		this.displayName = Objects.requireNonNull(displayName);
		this.holderSlot = hand==Hand.MAIN_HAND ?
				player.inventory.selected :
				player.inventory.items.size()+player.inventory.armor.size();
	}

	@Override public ITextComponent getDisplayName(){
		return displayName;
	}
	@SuppressWarnings("ConstantConditions")
	@Nullable @Override public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity entity){
		if(holderSlot>=0&&holderSlot<playerInventory.getContainerSize()){
			EssenceHolder h = playerInventory.getItem(holderSlot).getCapability(Caps.essenceHolder).orElse(null);
			if(h!=null){
				EssenceHolderContainer c = new EssenceHolderContainer(id, playerInventory, h);
				c.setHolderSlot(holderSlot);
				return c;
			}
		}
		return null;
	}
}
