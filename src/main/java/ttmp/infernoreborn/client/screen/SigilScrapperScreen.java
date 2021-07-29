package ttmp.infernoreborn.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import ttmp.infernoreborn.client.color.ColorUtils;
import ttmp.infernoreborn.contents.container.SigilScrapperContainer;
import ttmp.infernoreborn.contents.item.SigilItem;
import ttmp.infernoreborn.contents.sigil.Sigil;
import ttmp.infernoreborn.contents.sigil.holder.SigilHolder;
import ttmp.infernoreborn.network.ModNet;
import ttmp.infernoreborn.network.ScrapSigilMsg;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public class SigilScrapperScreen extends ContainerScreen<SigilScrapperContainer>{
	private static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/sigil_scrapper.png");
	private static final float SIGIL_SCRAP_PRESS_TICKS = 20;

	private final Map<Sigil, ItemStack> sigilItems = new HashMap<>();

	public SigilScrapperScreen(SigilScrapperContainer container, PlayerInventory inv, ITextComponent name){
		super(container, inv, name);
	}

	@Override protected void init(){
		imageWidth = 176;
		imageHeight = 170;
		super.init();

		titleLabelY = -11;
		inventoryLabelY = 70;
	}

	@Nullable private Sigil hoveringSigil;
	private int hoveringSigilIndex = -1;
	private int pressingSigilIndex = -1;
	private float pressingSigilTicks = 0;
	private boolean scrapSigilMessageSent;

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
					ModNet.CHANNEL.sendToServer(new ScrapSigilMsg(hoveringSigil));
					scrapSigilMessageSent = true;
				}
			}else resetPressingSigil();
		}
	}

	@Override protected void renderBg(MatrixStack pose, float partialTicks, int mx, int my){
		//noinspection deprecation
		RenderSystem.color4f(1, 1, 1, 1);
		//noinspection ConstantConditions
		this.minecraft.getTextureManager().bind(TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		SigilHolder sigilHolder = menu.getSigilHolder();
		if(sigilHolder!=null){
			int index = 0;
			for(Sigil sigil : sigilHolder.getSigils()){
				int sigilX = getSigilX(index);
				int sigilY = getSigilY(index);
				ItemStack item = sigilItems.computeIfAbsent(sigil, SigilItem::createSigilItem);
				itemRenderer.renderAndDecorateFakeItem(item, leftPos+sigilX, topPos+sigilY);
				itemRenderer.renderGuiItemDecorations(font, item, leftPos+sigilX, topPos+sigilY, "");
				if(isHovering(sigilX, sigilY, 16, 16, mx, my)){
					this.hoveringSigil = sigil;
					this.hoveringSigilIndex = index;
				}
				if(++index>=32) return;
			}
		}
	}

	private static int getSigilX(int index){
		return 30+index%8*18;
	}
	private static int getSigilY(int index){
		return index/8*18;
	}

	@Override protected void renderLabels(MatrixStack pose, int pX, int pY){
		this.font.draw(pose, this.title, this.titleLabelX, this.titleLabelY, 0xFFFFFFFF);
		this.font.draw(pose, this.inventory.getDisplayName(), this.inventoryLabelX, this.inventoryLabelY, 0xFFFFFFFF);
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

	@Override public boolean mouseClicked(double pMouseX, double pMouseY, int pButton){
		if(pButton==0){
			SigilHolder sigilHolder = menu.getSigilHolder();
			int sigils = sigilHolder==null ? 0 : sigilHolder.getSigils().size();
			if(sigils>0){
				for(int i = 0; i<Math.min(32, sigils); i++){ // The UI is supposed to handle 32 sigils at maximum anyway, TODO fixit I guess
					if(isHovering(getSigilX(i), getSigilY(i), 16, 16, pMouseX, pMouseY)){
						resetPressingSigil();
						pressingSigilIndex = i;
						return true;
					}
				}
			}
		}

		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}
	@Override public boolean mouseReleased(double pMouseX, double pMouseY, int pButton){
		resetPressingSigil();
		return super.mouseReleased(pMouseX, pMouseY, pButton);
	}

	private void resetPressingSigil(){
		pressingSigilIndex = -1;
		pressingSigilTicks = 0;
		scrapSigilMessageSent = false;
	}
}
