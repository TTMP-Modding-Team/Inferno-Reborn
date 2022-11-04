package ttmp.infernoreborn.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttmp.infernoreborn.capability.ClientAbilityHolder;
import ttmp.infernoreborn.capability.ClientPlayerCapability;
import ttmp.infernoreborn.capability.ItemSigilHolder;
import ttmp.infernoreborn.capability.PlayerCapability;
import ttmp.infernoreborn.capability.ServerAbilityHolder;
import ttmp.infernoreborn.capability.SimpleEssenceNetProvider;
import ttmp.infernoreborn.capability.SimpleTickingTaskHandler;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public final class AttachCapabilityEventHandlers{
	private AttachCapabilityEventHandlers(){}

	private static final ResourceLocation ABILITY_HOLDER_KEY = new ResourceLocation(MODID, "ability_holder");
	private static final ResourceLocation SIGIL_HOLDER_KEY = new ResourceLocation(MODID, "sigil_holder");
	private static final ResourceLocation PLAYER_CAP_KEY = new ResourceLocation(MODID, "player");
	private static final ResourceLocation TICKING_TASK_HANDLER_KEY = new ResourceLocation(MODID, "ticking_task_handler");
	private static final ResourceLocation ESSENCE_NET = new ResourceLocation(MODID, "essence_net");

	private static final ResourceLocation CLIENT_PLAYER_CAPS = new ResourceLocation(MODID, "client_caps");

	@SubscribeEvent
	public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event){
		Entity e = event.getObject();
		if(!(e instanceof LivingEntity)) return;
		if(e instanceof PlayerEntity){
			if(e instanceof ServerPlayerEntity)
				event.addCapability(PLAYER_CAP_KEY, new PlayerCapability((ServerPlayerEntity)e));
		}else{
			event.addCapability(ABILITY_HOLDER_KEY, e.level.isClientSide ? new ClientAbilityHolder() : new ServerAbilityHolder());
		}
	}

	@SubscribeEvent
	public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event){
		event.addCapability(SIGIL_HOLDER_KEY, new ItemSigilHolder(event.getObject()));
	}

	@SubscribeEvent
	public static void attachWorldCapabilities(AttachCapabilitiesEvent<World> event){
		event.addCapability(TICKING_TASK_HANDLER_KEY, new SimpleTickingTaskHandler());
		if(event.getObject().dimension()==World.OVERWORLD)
			event.addCapability(ESSENCE_NET, new SimpleEssenceNetProvider());
	}


	@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
	public static final class Client{
		private Client(){}

		@SubscribeEvent
		public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event){
			Entity e = event.getObject();
			if(e instanceof PlayerEntity&&e.level.isClientSide()){
				event.addCapability(CLIENT_PLAYER_CAPS, new ClientPlayerCapability((PlayerEntity)e));
			}
		}
	}
}
