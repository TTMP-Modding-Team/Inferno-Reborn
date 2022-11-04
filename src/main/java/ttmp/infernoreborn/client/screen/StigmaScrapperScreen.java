package ttmp.infernoreborn.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import ttmp.infernoreborn.api.sigil.Sigil;
import ttmp.infernoreborn.contents.container.StigmaScrapperContainer;
import ttmp.infernoreborn.network.ModNet;
import ttmp.infernoreborn.network.ScrapSigilMsg;

public class StigmaScrapperScreen extends BaseScrapperScreen<StigmaScrapperContainer>{
	public StigmaScrapperScreen(StigmaScrapperContainer menu, PlayerInventory playerInventory, ITextComponent title){
		super(menu, playerInventory, title);
	}

	@Override protected void init(){
		super.init();
		titleLabelY = -11;
		inventoryLabelY = 70;
	}

	@Override protected void renderLabels(MatrixStack pose, int mouseX, int mouseY){
		this.font.draw(pose, this.title, this.titleLabelX, this.titleLabelY, 0xFFFFFFFF);
		super.renderLabels(pose, mouseX, mouseY);
	}

	@Override protected int sigilX(){
		return 0;
	}
	@Override protected int sigilY(){
		return 0;
	}
	@Override protected int sigilRows(){
		return imageHeight/18;
	}
	@Override protected int sigilColumns(){
		return imageWidth/18;
	}
	@Override protected void handleSigilScrapped(Sigil scrappedSigil){
		ModNet.CHANNEL.sendToServer(new ScrapSigilMsg(scrappedSigil));
	}
}
