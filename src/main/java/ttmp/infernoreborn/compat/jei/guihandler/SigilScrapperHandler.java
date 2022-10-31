package ttmp.infernoreborn.compat.jei.guihandler;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import ttmp.infernoreborn.client.screen.SigilScrapperScreen;
import ttmp.infernoreborn.contents.item.SigilItem;
import ttmp.infernoreborn.contents.sigil.Sigil;

import javax.annotation.Nullable;

public class SigilScrapperHandler implements IGuiContainerHandler<SigilScrapperScreen>{
	@Nullable @Override public Object getIngredientUnderMouse(SigilScrapperScreen screen, double mouseX, double mouseY){
		Sigil sigil = screen.sigilAt(mouseX, mouseY);
		return sigil!=null ? SigilItem.createSigilItem(sigil) : null;
	}
}
