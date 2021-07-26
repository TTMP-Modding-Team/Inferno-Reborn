package ttmp.infernoreborn.contents.sigil;

import net.minecraft.util.text.TranslationTextComponent;
import ttmp.infernoreborn.compat.patchouli.sigil.SigilPageBuilder;
import ttmp.infernoreborn.util.SigilSlot;

public class GoatEyeSigil extends Sigil{
	public GoatEyeSigil(Properties properties){
		super(properties);
	}

	// TODO tickable sigils?

	@Override protected void createSigilBookEntryContent(SigilPageBuilder builder){
		builder.effectsFor(SigilSlot.BODY)
				.effect(new TranslationTextComponent("text.infernoreborn.sigil.goat_eyes.effect"));
	}
}
