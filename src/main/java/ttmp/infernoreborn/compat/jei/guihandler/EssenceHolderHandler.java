package ttmp.infernoreborn.compat.jei.guihandler;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import ttmp.infernoreborn.client.screen.EssenceHolderScreen;

import javax.annotation.Nullable;

public class EssenceHolderHandler implements IGuiContainerHandler<EssenceHolderScreen>{
	@Nullable @Override public Object getIngredientUnderMouse(EssenceHolderScreen screen, double mouseX, double mouseY){
		EssenceHolderScreen.EssenceSlot slot = screen.essenceSlotAt(mouseX, mouseY);
		return slot!=null&&slot.getCount()>0 ? slot.getItemStack() : null;
	}
}
