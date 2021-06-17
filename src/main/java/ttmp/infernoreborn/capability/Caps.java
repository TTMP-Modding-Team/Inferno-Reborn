package ttmp.infernoreborn.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import ttmp.infernoreborn.ability.holder.AbilityHolder;
import ttmp.infernoreborn.sigil.holder.SigilHolder;

public final class Caps{
	private Caps(){}

	@CapabilityInject(AbilityHolder.class)
	public static Capability<AbilityHolder> abilityHolder;
	@CapabilityInject(EssenceHolder.class)
	public static Capability<EssenceHolder> essenceHolder;
	@CapabilityInject(SigilHolder.class)
	public static Capability<SigilHolder> sigilHolder;
}
