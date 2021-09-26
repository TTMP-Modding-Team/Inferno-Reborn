package ttmp.infernoreborn.contents.recipe.sigilcraft;

import ttmp.infernoreborn.contents.sigil.Sigil;
import ttmp.infernoreborn.contents.sigil.holder.SigilHolder;
import ttmp.infernoreborn.inventory.SigilcraftInventory;

import javax.annotation.Nullable;

public interface SigilEngravingRecipe extends SigilcraftRecipe{
	@Nullable Sigil tryEngrave(SigilHolder sigilHolder, SigilcraftInventory inv);
}
