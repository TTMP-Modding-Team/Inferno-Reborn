package ttmp.infernoreborn.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import ttmp.infernoreborn.capability.Caps;
import ttmp.infernoreborn.client.GibberishFactory;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.Sigils;
import ttmp.infernoreborn.contents.container.StigmaTableContainer;
import ttmp.infernoreborn.contents.item.SigilItem;
import ttmp.infernoreborn.contents.sigil.Sigil;
import ttmp.infernoreborn.contents.sigil.holder.EmptySigilHolder;
import ttmp.infernoreborn.contents.sigil.holder.SigilHolder;
import ttmp.infernoreborn.network.EngraveBodySigilMsg;
import ttmp.infernoreborn.network.ModNet;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Objects;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public abstract class StigmaTableScreen extends ContainerScreen<StigmaTableContainer>{
	private final GibberishFactory gibFactory = new GibberishFactory();

	private int sigilIdCache = -1;
	@Nullable private Sigil sigilCache;
	@Nullable private ItemStack sigilItem;
	private Btn button;

	public StigmaTableScreen(StigmaTableContainer container, PlayerInventory inv, ITextComponent title){
		super(container, inv, title);
	}

	protected abstract ResourceLocation getImage();
	protected abstract int textStart();

	@Override public void init(Minecraft pMinecraft, int pWidth, int pHeight){
		titleLabelY = 0;
		super.init(pMinecraft, pWidth, pHeight);
		button = new Btn(leftPos+menu.centerSlotX(), topPos+menu.centerSlotY());
		addButton(button);
	}

	@Override public void render(MatrixStack stack, int mx, int my, float partialTicks){
		renderBackground(stack);
		super.render(stack, mx, my, partialTicks);
		renderTooltip(stack, mx, my);
	}

	@Override protected void renderBg(MatrixStack stack, float partialTicks, int mx, int my){
		//noinspection deprecation
		RenderSystem.color4f(1, 1, 1, 1);
		//noinspection ConstantConditions
		this.minecraft.getTextureManager().bind(getImage());
		blit(stack, leftPos, topPos+12, 0, 0, imageWidth, imageHeight-12);

		int currentSigil = menu.getCurrentSigil();
		if(sigilIdCache!=currentSigil){
			sigilIdCache = currentSigil;
			sigilCache = Sigils.getRegistry().getValue(currentSigil);
			if(sigilCache!=null){
				if(sigilItem==null) sigilItem = new ItemStack(ModItems.SIGIL.get());
				SigilItem.setSigil(sigilItem, sigilCache);
			}
		}
		SigilHolder h = inventory.player.getCapability(Caps.sigilHolder).orElse(EmptySigilHolder.INSTANCE);
		if(h.getMaxPoints()<=0) return;

		drawString(stack,
				font,
				(sigilCache!=null ? h.getTotalPoint()+sigilCache.getPoint() : h.getTotalPoint())+" / "+menu.getMaxPoints(),
				leftPos+textStart(),
				topPos+12+2,
				0xFFFFFF);
		font.drawWordWrap(sigilCache!=null ? gibFactory.toText(h, sigilCache) : gibFactory.toText(h),
				leftPos+textStart(),
				topPos+12+2+9,
				(width-imageWidth)/2-4-20,
				0xFFFFFF);
	}

	@Override protected void renderTooltip(MatrixStack matrixStack, int x, int y){
		super.renderTooltip(matrixStack, x, y);
		if(button.isHovered()) button.renderToolTip(matrixStack, x, y);
	}

	@Override protected void renderLabels(MatrixStack pMatrixStack, int pX, int pY){
		this.font.draw(pMatrixStack, this.title, this.titleLabelX, this.titleLabelY, 0xFFFFFFFF);
		this.font.draw(pMatrixStack, this.inventory.getDisplayName(), this.inventoryLabelX, this.inventoryLabelY, 0xFFFFFFFF);
	}

	public class Btn extends AbstractButton{
		public Btn(int x, int y){
			super(x, y, 16, 16, StringTextComponent.EMPTY);
		}

		@Override public void onPress(){
			ModNet.CHANNEL.sendToServer(new EngraveBodySigilMsg());
		}

		@Override public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks){
			this.active = sigilCache!=null;
			super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		}

		@Override public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
			Minecraft.getInstance().getTextureManager().bind(getImage());
			//noinspection deprecation
			RenderSystem.color4f(1, 1, 1, this.alpha);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			this.blit(matrixStack, this.x, this.y, 240, sigilCache!=null ? isHovered() ? 16 : 0 : 32, 16, 16);
			if(sigilCache!=null){
				itemRenderer.renderGuiItem(Objects.requireNonNull(sigilItem), this.x, this.y);
				itemRenderer.renderGuiItemDecorations(font, Objects.requireNonNull(sigilItem), this.x, this.y, "");
			}
		}

		@Override public void renderToolTip(MatrixStack matrixStack, int pMouseX, int pMouseY){
			if(sigilCache!=null) renderWrappedToolTip(matrixStack,
					Collections.singletonList(new TranslationTextComponent("")),
					pMouseX,
					pMouseY,
					font);
		}
	}

	public static class X5 extends StigmaTableScreen{
		private static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/stigma_table_5x5.png");

		public X5(StigmaTableContainer container, PlayerInventory playerInventory, ITextComponent name){
			super(container, playerInventory, name);
		}

		@Override protected ResourceLocation getImage(){
			return TEXTURE;
		}
		@Override protected int textStart(){
			return 100;
		}

		@Override protected void init(){
			imageWidth = 176;
			imageHeight = 192+12;
			inventoryLabelY = imageHeight-90-12;
			super.init();
		}
	}

	public static class X7 extends StigmaTableScreen{
		private static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/stigma_table_7x7.png");

		public X7(StigmaTableContainer container, PlayerInventory playerInventory, ITextComponent name){
			super(container, playerInventory, name);
		}

		@Override protected ResourceLocation getImage(){
			return TEXTURE;
		}
		@Override protected int textStart(){
			return 134;
		}

		@Override protected void init(){
			imageWidth = 176;
			imageHeight = 226+12;
			inventoryLabelY = imageHeight-90-12;
			super.init();
		}
	}
}