package ttmp.infernoreborn.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import ttmp.infernoreborn.api.ability.AbilityHolder;
import ttmp.infernoreborn.api.essence.EssenceHandler;
import ttmp.infernoreborn.api.essence.EssenceHolder;
import ttmp.infernoreborn.api.essence.EssenceNetProvider;
import ttmp.infernoreborn.api.shield.ShieldProvider;
import ttmp.infernoreborn.api.sigil.SigilHolder;

public final class Caps{
	private Caps(){}

	@CapabilityInject(AbilityHolder.class)
	public static Capability<AbilityHolder> abilityHolder;
	@CapabilityInject(EssenceHolder.class)
	public static Capability<EssenceHolder> essenceHolder;
	@CapabilityInject(EssenceHandler.class)
	public static Capability<EssenceHandler> essenceHandler;
	@CapabilityInject(SigilHolder.class)
	public static Capability<SigilHolder> sigilHolder;
	@CapabilityInject(TickingTaskHandler.class)
	public static Capability<TickingTaskHandler> tickingTaskHandler;
	@CapabilityInject(ShieldProvider.class)
	public static Capability<ShieldProvider> shieldProvider;
	@CapabilityInject(EssenceNetProvider.class)
	public static Capability<EssenceNetProvider> essenceNetProvider;
}