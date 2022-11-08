package ttmp.infernoreborn.compat.jei.guihandler;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import ttmp.infernoreborn.api.sigil.Sigil;
import ttmp.infernoreborn.client.screen.ScrapperScreen;
import ttmp.infernoreborn.contents.item.SigilItem;

import javax.annotation.Nullable;

public class SigilScrapperHandler<T extends ContainerScreen<?>> implements IGuiContainerHandler<T>{
	@Nullable @Override public Object getIngredientUnderMouse(T screen, double mouseX, double mouseY){
		if(!(screen instanceof ScrapperScreen)) throw new IllegalStateException("Not ScrapperScreen");
		Sigil sigil = ((ScrapperScreen)screen).sigilAt(mouseX, mouseY);
		return sigil!=null ? SigilItem.createStack(sigil) : null;
	}
}
