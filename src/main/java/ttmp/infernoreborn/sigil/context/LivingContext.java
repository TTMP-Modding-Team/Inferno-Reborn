package ttmp.infernoreborn.sigil.context;

import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.sigil.holder.SigilHolder;

/**
 * For SigilHolders attached directly to living creature.
 */
public class LivingContext extends MinimumContext{
	private final LivingEntity living;

	public LivingContext(LivingEntity living, SigilHolder holder){
		super(holder);
		this.living = living;
	}

	public LivingEntity entity(){
		return living;
	}

	@Override public LivingContext getAsLivingContext(){
		return this;
	}
}
