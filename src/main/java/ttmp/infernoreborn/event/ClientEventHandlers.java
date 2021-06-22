package ttmp.infernoreborn.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttmp.infernoreborn.capability.SimpleTickingTaskHandler;
import ttmp.infernoreborn.capability.TickingTaskHandler;
import ttmp.infernoreborn.contents.ability.holder.ClientAbilityHolder;

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

		living.getAttributes().getSyncableAttributes().stream()
				.map(a -> a.getValue()+" "+I18n.get(a.getAttribute().getDescriptionId()))
				.forEach(event.getRight()::add);

		ClientAbilityHolder h = ClientAbilityHolder.of(entity);
		if(h==null) return;
		event.getLeft().add("Applied Generator Scheme: "+(h.getAppliedGeneratorScheme()!=null ? h.getAppliedGeneratorScheme().getId() : "None"));
		h.getAbilities().stream()
				.map(ability -> ability.getName().getString())
				.forEach(event.getLeft()::add);
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event){
		if(event.phase!=TickEvent.Phase.START) return;
		if(Minecraft.getInstance().level==null) return;
		TickingTaskHandler h = TickingTaskHandler.of(Minecraft.getInstance().level);
		if(h instanceof SimpleTickingTaskHandler) ((SimpleTickingTaskHandler)h).update();
	}
}
