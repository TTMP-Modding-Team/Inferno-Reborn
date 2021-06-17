package ttmp.infernoreborn.sigil.context;

import net.minecraft.item.ItemStack;
import ttmp.infernoreborn.sigil.holder.SigilHolder;

/**
 * For SigilHolders attached to ItemStack.
 */
public class ItemContext extends MinimumContext{
	private final ItemStack stack;

	public ItemContext(ItemStack stack, SigilHolder holder){
		super(holder);
		this.stack = stack;
	}

	public ItemStack stack(){
		return stack;
	}

	@Override public ItemContext getAsItemContext(){
		return this;
	}
}
