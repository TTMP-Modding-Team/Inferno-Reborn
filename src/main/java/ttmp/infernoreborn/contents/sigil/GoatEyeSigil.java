package ttmp.infernoreborn.contents.sigil;

import net.minecraft.util.text.TranslationTextComponent;
import ttmp.infernoreborn.api.sigil.Sigil;
import ttmp.infernoreborn.api.sigil.SigilSlot;
import ttmp.infernoreborn.api.sigil.page.SigilPageBuilder;

public class GoatEyeSigil extends Sigil{
	public GoatEyeSigil(Properties properties){
		super(properties);
	}

	// TODO tickable sigils?

	@Override protected void createSigilBookEntryContent(SigilPageBuilder builder){
		builder.effectsFor(SigilSlot.BODY)
				.beneficialEffect(new TranslationTextComponent("text.infernoreborn.sigil.goat_eyes.effect"));
	}
}
