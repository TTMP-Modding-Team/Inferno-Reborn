package ttmp.infernoreborn.event;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttmp.infernoreborn.ability.Ability;
import ttmp.infernoreborn.capability.AbilityHolder;

import static ttmp.infernoreborn.InfernoReborn.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class ClientEventHandlers{
	private ClientEventHandlers(){}

	@SubscribeEvent
	public static void onTextRender(RenderGameOverlayEvent.Text event){
		RayTraceResult hitResult = Minecraft.getInstance().hitResult;
		if(hitResult==null||hitResult.getType()!=RayTraceResult.Type.ENTITY) return;
		EntityRayTraceResult result = (EntityRayTraceResult)hitResult;
		AbilityHolder h = AbilityHolder.of(result.getEntity());
		if(h==null) return;
		for(Ability ability : h.getAbilities())
			event.getLeft().add(ability.getName().getString());
	}
}
