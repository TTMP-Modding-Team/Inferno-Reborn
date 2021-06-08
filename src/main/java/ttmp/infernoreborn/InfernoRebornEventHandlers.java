package ttmp.infernoreborn;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static ttmp.infernoreborn.InfernoReborn.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class InfernoRebornEventHandlers{
	private InfernoRebornEventHandlers(){}

	@SubscribeEvent
	public static void onEntityJoin(EntityJoinWorldEvent e){
		if(e.getEntity() instanceof LivingEntity){
			LivingEntity entity = (LivingEntity)e.getEntity();
			entity.getEntity().setCustomNameVisible(true);
			InfernoReborn.LOGGER.debug(entity.getName());
		}
	}
}
