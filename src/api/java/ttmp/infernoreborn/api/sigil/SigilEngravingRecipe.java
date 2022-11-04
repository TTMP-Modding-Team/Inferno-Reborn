package ttmp.infernoreborn.api.sigil;

import javax.annotation.Nullable;

public interface SigilEngravingRecipe extends SigilcraftRecipe{
	@Nullable Sigil tryEngrave(SigilHolder sigilHolder, SigilcraftInventory inv);
}
