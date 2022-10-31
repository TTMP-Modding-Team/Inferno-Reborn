package ttmp.infernoreborn.compat.jei.guihandler;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Rectangle2d;
import ttmp.infernoreborn.client.screen.SigilWidget;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class SigilTableHandler<T extends ContainerScreen<?>> implements IGuiContainerHandler<T>{
	private final Function<T, SigilWidget> widgetFunction;

	public SigilTableHandler(Function<T, SigilWidget> widgetFunction){
		this.widgetFunction = widgetFunction;
	}

	@Override public List<Rectangle2d> getGuiExtraAreas(T containerScreen){
		SigilWidget widget = widgetFunction.apply(containerScreen);
		Rectangle2d rec = new Rectangle2d(widget.x, widget.y, widget.getWidth(), widget.getHeight());
		return Collections.singletonList(rec);
	}
}
