package ttmp.infernoreborn.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import ttmp.infernoreborn.api.sigil.Sigil;
import ttmp.infernoreborn.client.color.ColorUtils;
import ttmp.infernoreborn.contents.item.SigilItem;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO multiple page support
public abstract class BaseScrapperScreen<C extends Container> extends ContainerScreen<C> implements ScrapperScreen{
	private static final float SIGIL_SCRAP_PRESS_TICKS = 20;

	private final Map<Sigil, ItemStack> sigilItems = new HashMap<>();

	private int maxSigils;
	private List<Sigil> sigils = Collections.emptyList();

	@Nullable private Sigil hoveringSigil;
	private int hoveringSigilIndex = -1;
	private int pressingSigilIndex = -1;
	private float pressingSigilTicks = 0;
	private boolean scrapSigilMessageSent;

	public BaseScrapperScreen(C pMenu, PlayerInventory pPlayerInventory, ITextComponent pTitle){
		super(pMenu, pPlayerInventory, pTitle);
	}

	@Override public void sync(int maxSigils, List<Sigil> sigils){
		this.maxSigils = maxSigils;
		this.sigils = sigils;
	}

	@Override public void render(MatrixStack matrixStack, int mx, int my, float partialTicks){
		hoveringSigil = null;
		hoveringSigilIndex = -1;
		renderBackground(matrixStack);
		super.render(matrixStack, mx, my, partialTicks);
		renderTooltip(matrixStack, mx, my);

		if(pressingSigilIndex>=0){
			if(pressingSigilIndex==hoveringSigilIndex){
				if(scrapSigilMessageSent) return;
				pressingSigilTicks += partialTicks;
				if(pressingSigilTicks>=SIGIL_SCRAP_PRESS_TICKS){
					handleSigilScrapped(hoveringSigil);
					scrapSigilMessageSent = true;
				}
			}else resetPressingSigil();
		}
	}

	@Override protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY){
		for(int i = 0; i<Math.min(sigilSize(), sigils.size()); i++){
			Sigil sigil = sigils.get(i);
			int sigilX = getSigilX(i);
			int sigilY = getSigilY(i);
			ItemStack item = sigilItems.computeIfAbsent(sigil, SigilItem::createSigilItem);
			itemRenderer.renderAndDecorateFakeItem(item, leftPos+sigilX, topPos+sigilY);
			itemRenderer.renderGuiItemDecorations(font, item, leftPos+sigilX, topPos+sigilY, "");
			if(isHovering(sigilX, sigilY, 16, 16, mouseX, mouseY)){
				this.hoveringSigil = sigil;
				this.hoveringSigilIndex = i;
			}
		}
	}

	@Override protected void renderLabels(MatrixStack pose, int mouseX, int mouseY){
		if(hoveringSigil!=null){
			int sigilX = getSigilX(hoveringSigilIndex);
			int sigilY = getSigilY(hoveringSigilIndex);
			float reds = pressingSigilIndex==hoveringSigilIndex ? pressingSigilTicks/SIGIL_SCRAP_PRESS_TICKS : 0;

			RenderSystem.colorMask(true, true, true, false);
			if(reds<=0){
				fillGradient(pose, sigilX, sigilY, sigilX+16, sigilY+16, 0x80FFFFFF, 0x80FFFFFF);
			}else{
				int redHeight = Math.max(0, Math.min(16, (int)(16*reds)));
				fillGradient(pose, sigilX, sigilY, sigilX+16, sigilY+16-redHeight, 0x80FFFFFF, 0x80FFFFFF);
				fillGradient(pose, sigilX, sigilY+16-redHeight, sigilX+16, sigilY+16, 0x80FF5555, 0x80FF5555);
			}
			RenderSystem.colorMask(true, true, true, true);
		}
	}

	@Override protected void renderTooltip(MatrixStack pose, int x, int y){
		super.renderTooltip(pose, x, y);
		//noinspection ConstantConditions
		if(this.hoveringSigil!=null&&this.minecraft.player.inventory.getCarried().isEmpty()){
			renderComponentTooltip(pose, Arrays.asList(
					new StringTextComponent("")
							.append(hoveringSigil.getName())
							.append(new StringTextComponent(" (")
									.append(String.valueOf(hoveringSigil.getPoint()))
									.append(")")
									.withStyle(TextFormatting.GOLD))
							.withStyle(Style.EMPTY.withColor(ColorUtils.SIGIL_TEXT_COLOR)),
					new TranslationTextComponent("tooltip.infernoreborn.sigil_scrapper")
			), x, y);
		}
	}

	@Override public boolean mouseClicked(double mouseX, double mouseY, int button){
		if(button==0){
			int i = sigilIndexAt(mouseX, mouseY);
			if(i>=0&&i<sigils.size()){
				resetPressingSigil();
				pressingSigilIndex = i;
				return true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	@Override public boolean mouseReleased(double mouseX, double mouseY, int button){
		resetPressingSigil();
		return super.mouseReleased(mouseX, mouseY, button);
	}

	private void resetPressingSigil(){
		pressingSigilIndex = -1;
		pressingSigilTicks = 0;
		scrapSigilMessageSent = false;
	}

	@Nullable @Override public Sigil sigilAt(double mouseX, double mouseY){
		int i = sigilIndexAt(mouseX, mouseY);
		return i>=0&&i<sigils.size() ? sigils.get(i) : null;
	}

	public int sigilIndexAt(double mouseX, double mouseY){
		for(int i = 0; i<Math.min(sigilSize(), sigils.size()); i++)
			if(isHovering(getSigilX(i), getSigilY(i), 16, 16, mouseX, mouseY))
				return i;
		return -1;
	}

	protected final int getSigilX(int index){
		return isDisplayed(index) ? sigilX()+index%sigilColumns()*18 : -1;
	}
	protected final int getSigilY(int index){
		return isDisplayed(index) ? sigilY()+index/sigilColumns()*18 : -1;
	}

	protected abstract int sigilX();
	protected abstract int sigilY();

	protected abstract int sigilRows();
	protected abstract int sigilColumns();
	protected final int sigilSize(){
		return sigilRows()*sigilColumns();
	}
	protected final boolean isDisplayed(int index){
		return index>=0&&index<sigilSize();
	}

	protected abstract void handleSigilScrapped(Sigil scrappedSigil);
}
