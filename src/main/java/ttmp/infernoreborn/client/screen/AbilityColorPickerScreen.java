package ttmp.infernoreborn.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.fonts.TextInputUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.apache.logging.log4j.util.Strings;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.item.ability.AbilityColorPickerItem;
import ttmp.infernoreborn.network.AbilityColorPickerMsg;
import ttmp.infernoreborn.network.ModNet;

import javax.annotation.Nullable;
import java.util.function.IntSupplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbilityColorPickerScreen extends Screen{
	private static final Pattern regex = Pattern.compile("(?:#|0x)?([0-9a-fA-F]{6})");

	private final int inventoryIndex;
	private final int originalPrimaryColor;
	private final int originalSecondaryColor;
	private final int originalHighlightColor;

	private TextFieldWidget primary;
	private TextFieldWidget secondary;
	private TextFieldWidget highlight;

	private boolean updateColor = true;

	private final ItemStack stack = new ItemStack(ModItems.ABILITY_COLOR_PICKER.get());
	private SparkColor color = new SparkColor(0xFFFFFF, 0xFFFFFF, 0xFFFFFF);

	public AbilityColorPickerScreen(int inventoryIndex, int originalPrimaryColor, int originalSecondaryColor, int originalHighlightColor){
		super(StringTextComponent.EMPTY);
		this.inventoryIndex = inventoryIndex;
		this.originalPrimaryColor = originalPrimaryColor&0xFFFFFF;
		this.originalSecondaryColor = originalSecondaryColor&0xFFFFFF;
		this.originalHighlightColor = originalHighlightColor&0xFFFFFF;
	}

	@SuppressWarnings("ConstantConditions")
	@Override protected void init(){
		minecraft.keyboardHandler.setSendRepeatsToGui(true);
		primary = rgbField(4, 4, primary, originalPrimaryColor, 0xFFFFFF);
		addButton(new ColorButton(this, 4+60+4, 4, () -> color.primary, primary));
		secondary = rgbField(4, 4+20+5, secondary, originalSecondaryColor, 0xFFFFFF);
		addButton(new ColorButton(this, 4+60+4, 4+20+5, () -> color.secondary, secondary));
		highlight = rgbField(4, 4+20+5+20+5, highlight, originalHighlightColor, originalPrimaryColor);
		addButton(new ColorButton(this, 4+60+4, 4+20+5+20+5, () -> color.highlight, highlight));
		addButton(new Button(width-64-4, 4, 64, 20, new StringTextComponent("Apply"), btn -> {
			try{
				SparkColor color = readColor();
				ModNet.CHANNEL.sendToServer(new AbilityColorPickerMsg(inventoryIndex,
						color.primary, color.secondary, color.highlight));
				this.minecraft.player.closeContainer();
			}catch(NumberFormatException ignored){}
		}));
	}

	private TextFieldWidget rgbField(int x, int y, @Nullable TextFieldWidget previous, int initialColor, int fallback){
		TextFieldWidget widget = new TextFieldWidget(font, x, y, 60, 12, previous, StringTextComponent.EMPTY);
		if(previous==null&&fallback!=initialColor) widget.setValue(String.format("#%06x", initialColor));
		widget.setTextColor(-1);
		widget.setResponder(s -> {
			if(Strings.isBlank(s)||regex.matcher(s.trim()).matches()){
				updateColor = true;
				widget.setTextColor(-1);
			}else widget.setTextColor(0xFF0000);
		});
		return addButton(widget);
	}

	@Override public void tick(){
		primary.tick();
		secondary.tick();
		highlight.tick();
	}

	@SuppressWarnings({"ConstantConditions", "deprecation"})
	@Override public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
		if(updateColor){
			updateColor = false;
			SparkColor color = readColor();
			if(color!=null){
				this.color = color;
				AbilityColorPickerItem.set(stack, color.primary, color.secondary, color.highlight);
			}
		}
		renderBackground(matrixStack);
		RenderSystem.pushMatrix();
		RenderSystem.translatef(width-256, 20, 0);
		RenderSystem.scalef(8, 8, 1);
		minecraft.getItemRenderer().renderGuiItem(stack, 0, 0);
		RenderSystem.popMatrix();
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.primary.render(matrixStack, mouseX, mouseY, partialTicks);
		this.secondary.render(matrixStack, mouseX, mouseY, partialTicks);
		this.highlight.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@SuppressWarnings("ConstantConditions")
	@Override public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers){
		if(pKeyCode==256) this.minecraft.player.closeContainer();
		return this.primary.keyPressed(pKeyCode, pScanCode, pModifiers)||
				this.secondary.keyPressed(pKeyCode, pScanCode, pModifiers)||
				this.highlight.keyPressed(pKeyCode, pScanCode, pModifiers)||
				super.keyPressed(pKeyCode, pScanCode, pModifiers);
	}

	@SuppressWarnings("ConstantConditions")
	@Override public void removed(){
		super.removed();
		this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	private final Matcher m = regex.matcher("");

	@Nullable private SparkColor readColor(){
		try{
			int primary, secondary, highlight;
			String text = this.primary.getValue().trim();
			if(Strings.isBlank(text)) primary = 0xFFFFFF;
			else if(m.reset(text).matches()) primary = Integer.parseUnsignedInt(m.group(1), 16);
			else return null;
			text = this.secondary.getValue().trim();
			if(Strings.isBlank(text)) secondary = 0xFFFFFF;
			else if(m.reset(text).matches()) secondary = Integer.parseUnsignedInt(m.group(1), 16);
			else return null;
			text = this.highlight.getValue().trim();
			if(Strings.isBlank(text)) highlight = primary;
			else if(m.reset(text).matches()) highlight = Integer.parseUnsignedInt(m.group(1), 16);
			else return null;
			return new SparkColor(primary, secondary, highlight);
		}catch(NumberFormatException ex){
			return null;
		}
	}

	private static final class SparkColor{
		private final int primary;
		private final int secondary;
		private final int highlight;

		private SparkColor(int primary, int secondary, int highlight){
			this.primary = primary;
			this.secondary = secondary;
			this.highlight = highlight;
		}
	}

	private static final class ColorButton extends Button{
		private final IntSupplier color;

		@SuppressWarnings("ConstantConditions")
		public ColorButton(AbilityColorPickerScreen screen, int x, int y, IntSupplier color, TextFieldWidget text){
			super(x, y, 12, 12, StringTextComponent.EMPTY,
					btn -> {
						String s = text.getValue().trim();
						if(Strings.isBlank(s)) return;
						TextInputUtil.setClipboardContents(screen.minecraft, s.startsWith("#") ? s.substring(1) : s);
					}, (btn, matrixStack, mouseX, mouseY) ->
							screen.renderTooltip(matrixStack, new StringTextComponent("Click to copy"), mouseX, mouseY));
			this.color = color;
		}

		@Override public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
			GuiUtils.drawGradientRect(matrixStack.last().pose(), 0,
					x, y, x+width, y+height,
					0xFF000000, 0xFF000000);
			int color = 0xFF000000|this.color.getAsInt();
			GuiUtils.drawGradientRect(matrixStack.last().pose(), 0,
					x+1, y+1, x+width-1, y+height-1,
					color, color);
			if(this.isHovered())
				this.renderToolTip(matrixStack, mouseX, mouseY);
		}
	}
}
