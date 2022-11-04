package ttmp.infernoreborn.api.sigil.page;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

import static net.minecraft.item.ItemStack.ATTRIBUTE_MODIFIER_FORMAT;

public interface SigilBookEntry{
	int getDescriptionPages();
	List<EffectPage> getEffectPages();

	interface EffectPage{
		List<ITextComponent> getText();
	}

	static IFormattableTextComponent getAttributeText(Attribute attribute, double amount, AttributeModifier.Operation operation){
		double displayAmount = operation!=AttributeModifier.Operation.MULTIPLY_BASE&&operation!=AttributeModifier.Operation.MULTIPLY_TOTAL ?
				attribute.equals(Attributes.KNOCKBACK_RESISTANCE) ? amount*10 : amount :
				amount*100;

		if(amount<0) return new TranslationTextComponent("attribute.modifier.take."+operation.toValue(),
				ATTRIBUTE_MODIFIER_FORMAT.format(-displayAmount),
				I18n.get(attribute.getDescriptionId())).withStyle(TextFormatting.RED);
		else return new TranslationTextComponent("attribute.modifier.plus."+operation.toValue(),
				ATTRIBUTE_MODIFIER_FORMAT.format(displayAmount),
				I18n.get(attribute.getDescriptionId())).withStyle(TextFormatting.BLUE);
	}
}
