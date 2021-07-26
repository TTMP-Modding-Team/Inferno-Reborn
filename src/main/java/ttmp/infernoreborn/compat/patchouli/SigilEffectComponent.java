package ttmp.infernoreborn.compat.patchouli;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import ttmp.infernoreborn.compat.patchouli.sigil.SigilBookEntry;
import ttmp.infernoreborn.contents.Sigils;
import ttmp.infernoreborn.contents.sigil.Sigil;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.IVariable;

import javax.annotation.Nullable;
import java.util.function.UnaryOperator;

public class SigilEffectComponent extends ActualCustomComponent{
	@Nullable private Sigil sigil;
	private int page;

	@Override public void onVariablesAvailable(UnaryOperator<IVariable> lookup){
		String sigil = lookup.apply(IVariable.wrap("#sigil#")).asString();
		this.sigil = sigil.isEmpty() ? null : Sigils.getRegistry().getValue(new ResourceLocation(sigil));
		String page = lookup.apply(IVariable.wrap("#page#")).asString();
		this.page = page.isEmpty() ? -1 : Integer.parseInt(page);
	}

	@Override public void render(MatrixStack ms, IComponentRenderContext context, float pticks, int mouseX, int mouseY){
		SigilBookEntry.EffectPage page = page();
		if(page==null) return;
		FontRenderer font = Minecraft.getInstance().font;
		int y = this.y;
		for(ITextComponent text : page.getText()){
			font.draw(ms, text.copy().withStyle(context.getFont()), x, y, 0xFF000000);
			y += font.lineHeight;
		}
	}

	@Nullable private SigilBookEntry.EffectPage page(){
		if(sigil==null||page<0) return null;
		SigilBookEntry sigilBookEntry = sigil.getSigilBookEntryContent();
		if(page>=sigilBookEntry.getEffectPages().size()) return null;
		return sigilBookEntry.getEffectPages().get(page);
	}
}
