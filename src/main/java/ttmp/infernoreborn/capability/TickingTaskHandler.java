package ttmp.infernoreborn.capability;

import net.minecraftforge.common.capabilities.ICapabilityProvider;
import ttmp.infernoreborn.network.TickingTask;

import javax.annotation.Nullable;

public interface TickingTaskHandler{
	@SuppressWarnings("ConstantConditions") @Nullable static TickingTaskHandler of(ICapabilityProvider provider){
		return provider.getCapability(Caps.tickingTaskHandler).orElse(null);
	}

	default void add(int ticks, Runnable onFinish){
		this.add(ticks, onFinish, null);
	}
	default void add(int ticks, @Nullable Runnable onFinish, @Nullable Runnable onTick){
		this.add(new TickingTask.Simple(ticks, onFinish, onTick));
	}
	void add(TickingTask task);
}
