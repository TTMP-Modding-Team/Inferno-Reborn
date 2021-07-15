package ttmp.infernoreborn;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;
import org.spongepowered.tools.obfuscation.mapping.IMappingConsumer;

public class MixinConnector implements IMixinConnector{
	@Override public void connect(){
		Mixins.addConfiguration("assets/infernoreborn/mixin.json");
	}
}
