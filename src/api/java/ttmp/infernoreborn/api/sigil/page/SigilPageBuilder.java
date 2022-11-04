package ttmp.infernoreborn.api.sigil.page;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import ttmp.infernoreborn.api.sigil.SigilSlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SigilPageBuilder{
	private int descriptionPages = 1;
	private final List<EffectPageBuilder> effectPages = new ArrayList<>();

	public int getDescriptionPages(){
		return descriptionPages;
	}
	public void setDescriptionPages(int descriptionPages){
		if(descriptionPages<=0) throw new IllegalArgumentException("descriptionPages<=0");
		this.descriptionPages = descriptionPages;
	}
	public List<EffectPageBuilder> getEffectPages(){
		return effectPages;
	}

	public EffectPageBuilder effectsFor(SigilSlot... slots){
		EnumSet<SigilSlot> set = EnumSet.noneOf(SigilSlot.class);
		Collections.addAll(set, slots);
		return effectsFor(set);
	}
	public EffectPageBuilder effectsFor(EnumSet<SigilSlot> slots){
		EffectPageBuilder effectPage = new EffectPageBuilder(slots);
		effectPages.add(effectPage);
		return effectPage;
	}

	public SigilBookEntry build(){
		return new SimpleSigilBookEntry(descriptionPages, effectPages.stream()
				.map(t -> new SimpleEffectPage(t.text))
				.collect(Collectors.toList()));
	}

	public static class EffectPageBuilder{
		private final List<ITextComponent> text = new ArrayList<>();

		public EffectPageBuilder(EnumSet<SigilSlot> slots){
			for(SigilSlot slot : slots)
				whenApplied(slot.name().toLowerCase(Locale.ROOT));
		}

		private void whenApplied(String slotName){
			text.add(new TranslationTextComponent("text.infernoreborn.applied."+slotName));
		}

		public List<ITextComponent> getText(){
			return text;
		}

		public EffectPageBuilder effect(ITextComponent text){
			this.text.add(new StringTextComponent(" ").append(text));
			return this;
		}

		public EffectPageBuilder attribute(Attribute attribute, double amount, AttributeModifier.Operation operation){
			return effect(SigilBookEntry.getAttributeText(attribute, amount, operation));
		}

		/**
		 * Makes text blue
		 */
		public EffectPageBuilder beneficialEffect(IFormattableTextComponent text){
			return effect(text.withStyle(TextFormatting.BLUE));
		}

		/**
		 * Makes text red
		 */
		public EffectPageBuilder harmfulEffect(IFormattableTextComponent text){
			return effect(text.withStyle(TextFormatting.RED));
		}
	}
}
