package ttmp.infernoreborn.client.screen;

import ttmp.infernoreborn.contents.sigil.Sigil;

import java.util.Set;

public interface SigilScreen{
	void sync(Set<Sigil> currentSigils, Set<Sigil> newSigils);
}
