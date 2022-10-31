package ttmp.infernoreborn.compat.jei.guihandler;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import ttmp.infernoreborn.client.screen.ScrapperScreen;
import ttmp.infernoreborn.contents.item.SigilItem;
import ttmp.infernoreborn.contents.sigil.Sigil;

import javax.annotation.Nullable;

public class SigilScrapperHandler<T extends ContainerScreen<?>> implements IGuiContainerHandler<T>{
	@Nullable @Override public Object getIngredientUnderMouse(T screen, double mouseX, double mouseY){
		if(!(screen instanceof ScrapperScreen)) throw new IllegalStateException("Not ScrapperScreen");
		Sigil sigil = ((ScrapperScreen)screen).sigilAt(mouseX, mouseY);
		return sigil!=null ? SigilItem.createSigilItem(sigil) : null;
	}
}
