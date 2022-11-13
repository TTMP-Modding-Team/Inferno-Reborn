package ttmp.infernoreborn.capability;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import ttmp.infernoreborn.api.Caps;
import ttmp.infernoreborn.api.TickingTask;
import ttmp.infernoreborn.api.TickingTaskHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimpleTickingTaskHandler implements TickingTaskHandler, ICapabilityProvider{
	private final List<TickingTask> actions = new ArrayList<>();

	@Override public void add(TickingTask task){
		this.actions.add(task);
	}

	public void update(){
		for(Iterator<TickingTask> it = actions.iterator(); it.hasNext(); ){
			TickingTask action = it.next();
			action.onTick();
			if(action.isExpired()){
				action.onFinish();
				it.remove();
			}
		}
	}

	private final LazyOptional<TickingTaskHandler> self = LazyOptional.of(() -> this);

	@Override public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side){
		return cap==Caps.tickingTaskHandler ? self.cast() : LazyOptional.empty();
	}
}
