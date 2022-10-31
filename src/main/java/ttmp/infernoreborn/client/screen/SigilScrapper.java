package ttmp.infernoreborn.client.screen;

import ttmp.infernoreborn.contents.sigil.Sigil;

import java.util.List;

public interface SigilScrapper{
	void sync(int maxSigils, List<Sigil> sigils);
}
