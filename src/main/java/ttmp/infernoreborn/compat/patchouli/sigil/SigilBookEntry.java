package ttmp.infernoreborn.compat.patchouli.sigil;

import net.minecraft.util.text.ITextComponent;

import java.util.List;

public interface SigilBookEntry{
	int getDescriptionPages();
	List<EffectPage> getEffectPages();

	interface EffectPage{
		List<ITextComponent> getText();
	}
}
