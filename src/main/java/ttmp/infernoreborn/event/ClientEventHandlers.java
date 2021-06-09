package ttmp.infernoreborn.event;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttmp.infernoreborn.capability.ClientAbilityHolder;

import static ttmp.infernoreborn.InfernoReborn.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class ClientEventHandlers{
	private ClientEventHandlers(){}

	@SubscribeEvent
	public static void onTextRender(RenderGameOverlayEvent.Text event){
		RayTraceResult hitResult = Minecraft.getInstance().hitResult;
		if(hitResult==null||hitResult.getType()!=RayTraceResult.Type.ENTITY) return;
		EntityRayTraceResult result = (EntityRayTraceResult)hitResult;
		Entity entity = result.getEntity();
		if(!(entity instanceof LivingEntity)) return;

		LivingEntity living = (LivingEntity)entity;
		event.getLeft().add(entity.getDisplayName().getString()+" "+living.getHealth()+" / "+living.getMaxHealth());

		ClientAbilityHolder h = ClientAbilityHolder.of(entity);
		if(h==null) return;
		event.getLeft().add("Applied Generator Scheme: "+h.getAppliedGeneratorScheme());
		h.getAbilities().stream()
				.map(ability -> ability.getName().getString())
				.sorted(String::compareTo)
				.forEach(s -> event.getLeft().add(s));
	}
}
