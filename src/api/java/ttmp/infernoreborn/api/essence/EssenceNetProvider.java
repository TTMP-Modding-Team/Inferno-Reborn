package ttmp.infernoreborn.api.essence;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import ttmp.infernoreborn.api.Caps;

import javax.annotation.Nullable;

public interface EssenceNetProvider{
	EssenceNetProvider EMPTY = new EssenceNetProvider(){
		@Override public int assignNew(){
			return 0;
		}
		@Nullable @Override public EssenceHolder get(int id){
			return null;
		}
	};

	/**
	 * @return Assign new shit. Return 0 if can't.
	 */
	int assignNew();
	@Nullable EssenceHolder get(int id);

	static EssenceNetProvider getInstance(){
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if(server==null) return EMPTY;
		return server.overworld().getCapability(Caps.essenceNetProvider).orElse(EMPTY);
	}
}