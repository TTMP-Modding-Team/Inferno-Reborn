package ttmp.infernoreborn.contents.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.Objects;

public class EssenceHolderContainerProvider implements INamedContainerProvider{
	private final ITextComponent displayName;
	private final int holderSlot;

	public EssenceHolderContainerProvider(ITextComponent displayName, PlayerEntity player, Hand hand){
		this.displayName = Objects.requireNonNull(displayName);
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

	}

	@Override public ITextComponent getDisplayName(){
		return displayName;
	}
	@Nullable @Override public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity entity){
		EssenceHolderContainer c = new EssenceHolderContainer(id, playerInventory);
		c.setHolderSlot(holderSlot);
		return c;
	}
}
