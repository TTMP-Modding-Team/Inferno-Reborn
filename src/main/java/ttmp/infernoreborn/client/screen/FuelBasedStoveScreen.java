package ttmp.infernoreborn.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import ttmp.infernoreborn.contents.container.FuelBasedStoveContainer;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

public class FuelBasedStoveScreen extends ContainerScreen<FuelBasedStoveContainer>{
	private static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/stove.png");

	public FuelBasedStoveScreen(FuelBasedStoveContainer menu, PlayerInventory playerInventory, ITextComponent title){
		super(menu, playerInventory, title);
	}

	@Override protected void init(){
		imageWidth = 176;
		imageHeight = 118;
		super.init();
		titleLabelY = -11;
		inventoryLabelY = 57;
	}

	@Override public void render(MatrixStack matrixStack, int mx, int my, float partialTicks){
		renderBackground(matrixStack);
		super.render(matrixStack, mx, my, partialTicks);
		renderTooltip(matrixStack, mx, my);
	}

	@SuppressWarnings({"deprecation", "ConstantConditions"})
	@Override protected void renderBg(MatrixStack matrixStack, float partialTicks, int mx, int my){
		RenderSystem.color4f(1, 1, 1, 1);
		this.minecraft.getTextureManager().bind(TEXTURE);
		blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override protected void renderLabels(MatrixStack pMatrixStack, int pX, int pY){
		this.font.draw(pMatrixStack, this.title, this.titleLabelX, this.titleLabelY, 0xFFFFFFFF);
		this.font.draw(pMatrixStack, this.inventory.getDisplayName(), this.inventoryLabelX, this.inventoryLabelY, 0xFFFFFFFF);
	}
}
