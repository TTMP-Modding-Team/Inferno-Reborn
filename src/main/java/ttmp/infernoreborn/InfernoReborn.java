package ttmp.infernoreborn;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(InfernoReborn.MODID)
@Mod.EventBusSubscriber(modid = InfernoReborn.MODID)
public class InfernoReborn {
    public static final String MODID = "infernoreborn";

    public static Logger logger;
    public InfernoReborn(){
        MinecraftForge.EVENT_BUS.register(this);

        MinecraftForge.EVENT_BUS.register(InfernoRebornEvent.class);

        logger = LogManager.getLogger();
    }
}
