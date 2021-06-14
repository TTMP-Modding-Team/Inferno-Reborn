package ttmp.infernoreborn.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistryEntry;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.ability.Ability;
import ttmp.infernoreborn.ability.AbilitySkill;
import ttmp.infernoreborn.ability.OnEvent;
import ttmp.infernoreborn.capability.AbilityHolder;
import ttmp.infernoreborn.capability.ClientAbilityHolder;
import ttmp.infernoreborn.capability.ServerAbilityHolder;

import java.util.Objects;
import java.util.stream.Collectors;

import static ttmp.infernoreborn.InfernoReborn.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class CommonEventHandlers{
	private CommonEventHandlers(){}

	private static final ResourceLocation ABILITY_HOLDER_KEY = new ResourceLocation(MODID, "ability_holder");

	@SubscribeEvent
	public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event){
		Entity e = event.getObject();
		if(e instanceof LivingEntity&&!(e instanceof PlayerEntity))
			event.addCapability(ABILITY_HOLDER_KEY, e.level.isClientSide ? new ClientAbilityHolder() : new ServerAbilityHolder());
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
		if(event.getPlayer() instanceof ServerPlayerEntity)
			addSlotListeners((ServerPlayerEntity)event.getPlayer(), event.getPlayer().inventoryMenu);
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event){
		if(event.getPlayer() instanceof ServerPlayerEntity)
			addSlotListeners((ServerPlayerEntity)event.getPlayer(), event.getPlayer().inventoryMenu);
	}

	@SubscribeEvent
	public static void onPlayerContainerOpen(PlayerContainerEvent.Open event){
		if(event.getPlayer() instanceof ServerPlayerEntity)
			addSlotListeners((ServerPlayerEntity)event.getPlayer(), event.getContainer());
	}

	private static void addSlotListeners(ServerPlayerEntity player, Container container){
		// container.addSlotListener(new EssenceHolderSynchronizer(player));
	}

	@SubscribeEvent
	public static void onLivingUpdate(LivingUpdateEvent event){
		LivingEntity entity = event.getEntityLiving();
		AbilityHolder h = AbilityHolder.of(entity);
		if(h!=null)
			h.update(entity);
		if(h instanceof ServerAbilityHolder){
			for(OnEvent<LivingUpdateEvent> e : ((ServerAbilityHolder)h).getOnUpdateListeners().values())
				e.onEvent(entity, (ServerAbilityHolder)h, event);
			for(Ability ability : h.getAbilities())
				for(AbilitySkill skill : ability.getSkills())
					((ServerAbilityHolder)h).tryCast(skill, entity);
		}
	}

	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event){
		LivingEntity entity = event.getEntityLiving();
		ServerAbilityHolder h = ServerAbilityHolder.of(entity);
		if(h!=null){
			for(OnEvent<LivingHurtEvent> e : h.getOnHurtListeners().values())
				e.onEvent(entity, h, event);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onLivingHurtAfter(LivingHurtEvent event){
		Entity entity = event.getSource().getDirectEntity();
		if(entity instanceof LivingEntity){
			ServerAbilityHolder h = ServerAbilityHolder.of(entity);
			if(h!=null){
				for(OnEvent<LivingHurtEvent> e : h.getOnAttackListeners().values())
					e.onEvent((LivingEntity)entity, h, event);
			}
		}
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
		if(h instanceof ServerAbilityHolder){
			for(OnEvent<LivingDeathEvent> e : ((ServerAbilityHolder)h).getOnDeathListeners().values())
				e.onEvent((LivingEntity)entity, (ServerAbilityHolder)h, event);
		}
	}

	@SubscribeEvent
	public static void onStartTracking(PlayerEvent.StartTracking event){
		if(event.getTarget() instanceof LivingEntity){
			LivingEntity target = (LivingEntity)event.getTarget();
			ServerAbilityHolder holder = ServerAbilityHolder.of(target);
			if(holder!=null) holder.syncAbilityToClient(target);
		}
	}
}
