package ttmp.infernoreborn.client.screen;

import ttmp.infernoreborn.api.sigil.Sigil;

import javax.annotation.Nullable;
import java.util.List;

public interface ScrapperScreen{
	void sync(int maxSigils, List<Sigil> sigils);

	@Nullable Sigil sigilAt(double mouseX, double mouseY);
}
