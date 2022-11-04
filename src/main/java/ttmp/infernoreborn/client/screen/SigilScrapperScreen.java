package ttmp.infernoreborn.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import ttmp.infernoreborn.api.sigil.Sigil;
import ttmp.infernoreborn.contents.container.SigilScrapperContainer;
import ttmp.infernoreborn.network.ModNet;
import ttmp.infernoreborn.network.ScrapSigilMsg;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public class SigilScrapperScreen extends BaseScrapperScreen<SigilScrapperContainer>{
	private static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/sigil_scrapper.png");

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

	@SuppressWarnings({"deprecation", "ConstantConditions"})
	@Override protected void renderBg(MatrixStack pose, float partialTicks, int mx, int my){
		RenderSystem.color4f(1, 1, 1, 1);
		this.minecraft.getTextureManager().bind(TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.renderBg(pose, partialTicks, mx, my);
	}

	@Override protected void renderLabels(MatrixStack pose, int mouseX, int mouseY){
		this.font.draw(pose, this.title, this.titleLabelX, this.titleLabelY, 0xFFFFFFFF);
		this.font.draw(pose, this.inventory.getDisplayName(), this.inventoryLabelX, this.inventoryLabelY, 0xFFFFFFFF);
		super.renderLabels(pose, mouseX, mouseY);
	}

	@Override protected int sigilX(){
		return 30;
	}
	@Override protected int sigilY(){
		return 0;
	}
	@Override protected int sigilRows(){
		return 8;
	}
	@Override protected int sigilColumns(){
		return 4;
	}
	@Override protected void handleSigilScrapped(Sigil scrappedSigil){
		ModNet.CHANNEL.sendToServer(new ScrapSigilMsg(scrappedSigil));
	}
}
