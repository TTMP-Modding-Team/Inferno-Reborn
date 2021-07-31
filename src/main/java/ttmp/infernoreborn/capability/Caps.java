package ttmp.infernoreborn.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import ttmp.infernoreborn.contents.ability.holder.AbilityHolder;
import ttmp.infernoreborn.contents.sigil.holder.SigilHolder;
import ttmp.infernoreborn.util.EssenceHolder;

public final class Caps{
	private Caps(){}

	@CapabilityInject(AbilityHolder.class)
	public static Capability<AbilityHolder> abilityHolder;
	@CapabilityInject(EssenceHolder.class)
	public static Capability<EssenceHolder> essenceHolder;
	@CapabilityInject(SigilHolder.class)
	public static Capability<SigilHolder> sigilHolder;
	@CapabilityInject(ShieldHolder.class)
	public static Capability<ShieldHolder> shieldHolder;
	@CapabilityInject(TickingTaskHandler.class)
	public static Capability<TickingTaskHandler> tickingTaskHandler;
	@CapabilityInject(PlayerCapability.class)
	public static Capability<PlayerCapability> playerCapability;
	@CapabilityInject(EssenceNetProvider.class)
	public static Capability<EssenceNetProvider> essenceNetProvider;
}
