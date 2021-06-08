package ttmp.infernoreborn;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistryEntry;
import ttmp.infernoreborn.capability.AbilityHolder;
import ttmp.infernoreborn.capability.ClientAbilityHolder;
import ttmp.infernoreborn.capability.ServerAbilityHolder;

import java.util.Objects;
import java.util.stream.Collectors;

import static ttmp.infernoreborn.InfernoReborn.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class InfernoRebornEventHandlers{
	private InfernoRebornEventHandlers(){}

	private static final ResourceLocation ABILITY_HOLDER_KEY = new ResourceLocation(MODID, "ability_holder");

	@SubscribeEvent
	public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event){
		Entity e = event.getObject();
		if(e instanceof LivingEntity&&!(e instanceof PlayerEntity))
			event.addCapability(ABILITY_HOLDER_KEY, e.level.isClientSide ? new ClientAbilityHolder() : new ServerAbilityHolder());
	}

	@SubscribeEvent
	public static void onLivingUpdate(LivingUpdateEvent event){
		LivingEntity entity = event.getEntityLiving();
		AbilityHolder h = AbilityHolder.of(entity);
		if(h!=null) h.update(entity);
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event){
		LivingEntity entity = event.getEntityLiving();
		AbilityHolder h = AbilityHolder.of(entity);
		if(h!=null&&!h.getAbilities().isEmpty()){
			InfernoReborn.LOGGER.debug("Entity {} had: {}", entity, h.getAbilities().stream()
					.map(ForgeRegistryEntry::getRegistryName)
					.filter(Objects::nonNull)
					.map(ResourceLocation::toString)
					.collect(Collectors.joining(", ")));
		}
	}
}
