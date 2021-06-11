package ttmp.infernoreborn.util;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public enum ExpandKey{
	SHIFT,
	CTRL,
	ALT;

	public boolean isKeyDown(){
		switch(this){
			case SHIFT:
				return Screen.hasShiftDown();
			case CTRL:
				return Screen.hasControlDown();
			case ALT:
				return Screen.hasAltDown();
			default:
				throw new IllegalStateException("Unreachable");
		}
	}

	public ITextComponent getCollapsedText(){
		return new TranslationTextComponent("tooltip.infernoreborn.expand",
				new TranslationTextComponent(expandTranslationString())
						.setStyle(Style.EMPTY.applyFormat(TextFormatting.YELLOW)))
				.setStyle(Style.EMPTY.applyFormat(TextFormatting.DARK_GRAY));
	}

	private String expandTranslationString(){
		switch(this){
			case SHIFT:
				return "tooltip.infernoreborn.expand.shift";
			case CTRL:
				return "tooltip.infernoreborn.expand.ctrl";
			case ALT:
				return "tooltip.infernoreborn.expand.alt";
			default:
				throw new IllegalStateException("Unreachable");
		}
	}
}
