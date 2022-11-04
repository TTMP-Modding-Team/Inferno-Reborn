package ttmp.infernoreborn.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import ttmp.infernoreborn.contents.container.FoundryContainer;
import ttmp.infernoreborn.contents.tile.FoundryTile;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

public class FoundryScreen extends ContainerScreen<FoundryContainer>{
	private static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/foundry.png");

	public FoundryScreen(FoundryContainer container, PlayerInventory inv, ITextComponent name){
		super(container, inv, name);
	}

	@Override protected void init(){
		imageWidth = 176;
		imageHeight = 156;
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

		if(menu.getFoundry().getStackInSlot(FoundryTile.ESSENCE_HOLDER_SLOT).isEmpty()){
			blit(matrixStack, leftPos+8, topPos+11, 0, 256-16, 16, 16);
		}
		if(menu.getFoundry().getStackInSlot(FoundryTile.ESSENCE_INPUT_SLOT).isEmpty()){
			blit(matrixStack, leftPos+8, topPos+29, 16, 256-16, 16, 16);
		}
		double process = menu.getProcessPercentage();
		if(process>0){
			int width = (int)(process*30);
			if(width>0) blit(matrixStack, leftPos+82, topPos+23, 256-30, 0, width, 9);
		}
	}

	@Override protected void renderLabels(MatrixStack pMatrixStack, int pX, int pY){
		this.font.draw(pMatrixStack, this.title, this.titleLabelX, this.titleLabelY, 0xFFFFFFFF);
		this.font.draw(pMatrixStack, this.inventory.getDisplayName(), this.inventoryLabelX, this.inventoryLabelY, 0xFFFFFFFF);
	}
}
