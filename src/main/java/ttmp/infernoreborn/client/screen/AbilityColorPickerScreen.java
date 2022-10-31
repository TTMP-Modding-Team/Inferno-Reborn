package ttmp.infernoreborn.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.util.Strings;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.item.ability.AbilityColorPickerItem;
import ttmp.infernoreborn.network.AbilityColorPickerMsg;
import ttmp.infernoreborn.network.ModNet;

import javax.annotation.Nullable;
import java.util.function.IntConsumer;

public class AbilityColorPickerScreen extends Screen{
	private final int inventoryIndex;
	private final int originalPrimaryColor;
	private final int originalSecondaryColor;
	private final int originalHighlightColor;

	private TextFieldWidget primary;
	private TextFieldWidget secondary;
	private TextFieldWidget highlight;

	private int primaryColor;
	private int secondaryColor;
	private int highlightColor;

	public AbilityColorPickerScreen(int inventoryIndex, int originalPrimaryColor, int originalSecondaryColor, int originalHighlightColor){
		super(StringTextComponent.EMPTY);
		this.inventoryIndex = inventoryIndex;
		this.primaryColor = this.originalPrimaryColor = originalPrimaryColor;
		this.secondaryColor = this.originalSecondaryColor = originalSecondaryColor;
		this.highlightColor = this.originalHighlightColor = originalHighlightColor;
	}

	@SuppressWarnings("ConstantConditions")
	@Override protected void init(){
		minecraft.keyboardHandler.setSendRepeatsToGui(true);
		primary = rgbField(0, primary, originalPrimaryColor, value -> this.primaryColor = value);
		secondary = rgbField(20+5, secondary, originalSecondaryColor, value -> this.secondaryColor = value);
		highlight = rgbField(20+5+20+5, highlight, originalHighlightColor, value -> this.highlightColor = value);
		addButton(new Button(width-50, 0, 64, 32, new StringTextComponent("Apply"), btn -> {
			try{
				int primaryColor = color(primary.getValue().trim());
				int secondaryColor = color(secondary.getValue().trim());
				int highlightColor = color(highlight.getValue().trim());
				ModNet.CHANNEL.sendToServer(new AbilityColorPickerMsg(inventoryIndex,
						primaryColor, secondaryColor, highlightColor));
				this.minecraft.player.closeContainer();
			}catch(NumberFormatException ignored){}
		}));
	}

	private TextFieldWidget rgbField(int y, @Nullable TextFieldWidget previous, int initialColor, IntConsumer onUpdate){
		TextFieldWidget widget = new TextFieldWidget(font, 0, y, 60, 12, previous,
				new StringTextComponent(String.format("%06x", initialColor)));
		widget.setTextColor(-1);
		widget.setMaxLength(6);
		widget.setResponder(s -> {
			try{
				onUpdate.accept(color(s));
				widget.setTextColor(-1);
			}catch(NumberFormatException ex){
				widget.setTextColor(0xFF0000);
			}
		});
		return addButton(widget);
	}

	private static int color(String s){
		if(Strings.isBlank(s)) return 0xFFFFFF;
		s = s.trim();
		if(s.length()!=6) throw new NumberFormatException();
		return Integer.parseUnsignedInt(s, 16);
	}

	@Override public void tick(){
		primary.tick();
		secondary.tick();
		highlight.tick();
	}

	private final ItemStack stack = new ItemStack(ModItems.ABILITY_COLOR_PICKER.get());
	private int itemPrimaryColor;
	private int itemSecondaryColor;
	private int itemHighlightColor;

	@SuppressWarnings({"ConstantConditions", "deprecation"})
	@Override public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.primary.render(matrixStack, mouseX, mouseY, partialTicks);
		this.secondary.render(matrixStack, mouseX, mouseY, partialTicks);
		this.highlight.render(matrixStack, mouseX, mouseY, partialTicks);

		RenderSystem.pushMatrix();
		RenderSystem.translatef(width-256, 20, 0);
		RenderSystem.scalef(8, 8, 1);
		if(primaryColor!=itemPrimaryColor||
				secondaryColor!=itemSecondaryColor||
				highlightColor!=itemHighlightColor)
			AbilityColorPickerItem.set(stack,
					this.itemPrimaryColor = primaryColor,
					this.itemSecondaryColor = secondaryColor,
					this.itemHighlightColor = highlightColor);
		minecraft.getItemRenderer().renderGuiItem(stack, 0, 0);
		RenderSystem.popMatrix();
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
}
