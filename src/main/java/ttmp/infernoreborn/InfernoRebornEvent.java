package ttmp.infernoreborn;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InfernoRebornEvent {
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinWorldEvent e) {
        if (e.getEntity() instanceof LivingEntity){
            LivingEntity entity = (LivingEntity) e.getEntity();
            entity.getEntity().setCustomNameVisible(true);
            InfernoReborn.logger.debug(entity.getName());
        }
    }
}
