package ttmp.infernoreborn.contents.sigil.holder;

import net.minecraftforge.common.capabilities.ICapabilityProvider;
import ttmp.infernoreborn.capability.Caps;
import ttmp.infernoreborn.contents.sigil.Sigil;
import ttmp.infernoreborn.contents.sigil.context.SigilEventContext;

import javax.annotation.Nullable;
import java.util.Set;

public interface SigilHolder{
	@SuppressWarnings("ConstantConditions") @Nullable static SigilHolder of(ICapabilityProvider provider){
		return provider.getCapability(Caps.sigilHolder).orElse(null);
	}

	Set<Sigil> getSigils();

	int getMaxPoints();
	int getTotalPoint();

	boolean has(Sigil sigil);
	boolean add(Sigil sigil, boolean force);
	boolean remove(Sigil sigil);
	boolean isEmpty();
	void clear();

	SigilEventContext createContext();

	long getGibberishSeed();

	default boolean canAdd(Sigil sigil){
		return !has(sigil)&&
				sigil.canBeAttachedTo(createContext())&&
				getMaxPoints()>=sigil.getPoint()+getTotalPoint();
	}
	default boolean add(Sigil sigil){
		return add(sigil, false);
	}
	default boolean forceAdd(Sigil sigil){
		return add(sigil, true);
	}
}
