package ttmp.infernoreborn.compat.patchouli;

import com.google.common.collect.Multimap;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.IVariable;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;

import static net.minecraft.item.ItemStack.ATTRIBUTE_MODIFIER_FORMAT;

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
				double displayAmount = m.getOperation()!=Operation.MULTIPLY_BASE&&m.getOperation()!=Operation.MULTIPLY_TOTAL ?
						attr.equals(Attributes.KNOCKBACK_RESISTANCE) ? amount*10 : amount :
						amount*100;

				ITextComponent s;
				if(amount>0){
					s = new TranslationTextComponent("attribute.modifier.plus."+m.getOperation().toValue(),
							ATTRIBUTE_MODIFIER_FORMAT.format(displayAmount),
							I18n.get(attr.getDescriptionId())).setStyle(context.getFont()).withStyle(TextFormatting.BLUE);
				}else if(amount<0){
					s = new TranslationTextComponent("attribute.modifier.take."+m.getOperation().toValue(),
							ATTRIBUTE_MODIFIER_FORMAT.format(-displayAmount),
							I18n.get(attr.getDescriptionId())).setStyle(context.getFont()).withStyle(TextFormatting.RED);
				}else continue;
				font.draw(ms, s, x, y, 0xFF000000);
				y += font.lineHeight;
			}
		}
	}

	@Override public void onVariablesAvailable(UnaryOperator<IVariable> lookup){}
	@Nullable protected abstract String getHeadText();
	protected abstract Multimap<Attribute, AttributeModifier> getAttributes();
}
