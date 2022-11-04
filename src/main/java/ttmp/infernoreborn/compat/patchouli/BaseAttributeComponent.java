package ttmp.infernoreborn.compat.patchouli;

import com.google.common.collect.Multimap;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.text.TranslationTextComponent;
import ttmp.infernoreborn.api.sigil.page.SigilBookEntry;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.IVariable;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;

public abstract class BaseAttributeComponent extends ActualCustomComponent{
	@Override public void render(MatrixStack ms, IComponentRenderContext context, float pticks, int mouseX, int mouseY){
		Multimap<Attribute, AttributeModifier> attributes = getAttributes();
		FontRenderer font = Minecraft.getInstance().font;
		int y = this.y;

		String headText = getHeadText();
		if(headText!=null){
			font.draw(ms, new TranslationTextComponent(headText).setStyle(context.getFont()), x, y, 0xFF000000);
			y += font.lineHeight;
		}
		for(Entry<Attribute, Collection<AttributeModifier>> e : attributes.asMap().entrySet()){
			Attribute attr = e.getKey();
			for(AttributeModifier m : e.getValue()){
				double amount = m.getAmount();
				if(amount==0) continue;
				font.draw(ms, SigilBookEntry.getAttributeText(attr, amount, m.getOperation()).withStyle(context.getFont()), x, y, 0xFF000000);
				y += font.lineHeight;
			}
		}
	}

	@Override public void onVariablesAvailable(UnaryOperator<IVariable> lookup){}
	@Nullable protected abstract String getHeadText();
	protected abstract Multimap<Attribute, AttributeModifier> getAttributes();
}
