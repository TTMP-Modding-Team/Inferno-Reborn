package ttmp.infernoreborn.event;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.ability.OnAbilityEvent;
import ttmp.infernoreborn.ability.holder.AbilityHolder;
import ttmp.infernoreborn.ability.holder.ClientAbilityHolder;
import ttmp.infernoreborn.ability.holder.ServerAbilityHolder;
import ttmp.infernoreborn.container.listener.SigilHolderSynchronizer;
import ttmp.infernoreborn.contents.ModAttributes;
import ttmp.infernoreborn.sigil.holder.ItemSigilHolder;
import ttmp.infernoreborn.sigil.holder.PlayerSigilHolder;
import ttmp.infernoreborn.sigil.holder.SigilHolder;

import static ttmp.infernoreborn.InfernoReborn.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class CommonEventHandlers{
	private CommonEventHandlers(){}

	private static final ResourceLocation ABILITY_HOLDER_KEY = new ResourceLocation(MODID, "ability_holder");
	private static final ResourceLocation SIGIL_HOLDER_KEY = new ResourceLocation(MODID, "sigil_holder");

	@SubscribeEvent
	public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event){
		Entity e = event.getObject();
		if(e instanceof PlayerEntity){
			event.addCapability(SIGIL_HOLDER_KEY, new PlayerSigilHolder((PlayerEntity)e));
		}else if(e instanceof LivingEntity)
			event.addCapability(ABILITY_HOLDER_KEY, e.level.isClientSide ? new ClientAbilityHolder() : new ServerAbilityHolder());
	}

	@SubscribeEvent
	public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event){
		event.addCapability(SIGIL_HOLDER_KEY, new ItemSigilHolder(event.getObject()));
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
		container.addSlotListener(new SigilHolderSynchronizer(player));
	}

	@SubscribeEvent
	public static void onLivingUpdate(LivingUpdateEvent event){
		LivingEntity entity = event.getEntityLiving();
		if(!entity.isAlive()) return;
		AbilityHolder h = AbilityHolder.of(entity);
		if(h!=null) h.update(entity);
	}

	@SubscribeEvent
	public static void onLivingAttack(LivingAttackEvent event){
		if(event.getSource()==DamageSource.FALL){
			ModifiableAttributeInstance attr = event.getEntityLiving().getAttribute(ModAttributes.FALLING_DAMAGE_RESISTANCE.get());
			if(attr==null) InfernoReborn.LOGGER.debug("wat");
			else if(attr.getValue()>=2) event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event){
		LivingEntity entity = event.getEntityLiving();
		LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity ? (LivingEntity)event.getSource().getEntity() : null;
		float res = (float)getAttrib(entity, ModAttributes.DAMAGE_RESISTANCE.get());
		float mod = 0;

		if(event.getSource()==DamageSource.FALL)
			res += getAttrib(event.getEntityLiving(), ModAttributes.FALLING_DAMAGE_RESISTANCE.get())-1;
		else if(!event.getSource().isBypassInvul()){
			if(event.getSource().isMagic()){
				res += getAttrib(event.getEntityLiving(), ModAttributes.MAGIC_DAMAGE_RESISTANCE.get())-1;
				if(attacker!=null) mod += getAttrib(attacker, ModAttributes.MAGIC_ATTACK.get());
			}
			if(attacker!=null&&event.getSource().getDirectEntity()==attacker){
				res += getAttrib(event.getEntityLiving(), ModAttributes.MELEE_DAMAGE_RESISTANCE.get())-1;
			}
			if(event.getSource().isProjectile()){
				res += getAttrib(event.getEntityLiving(), ModAttributes.RANGED_DAMAGE_RESISTANCE.get())-1;
				if(attacker!=null) mod += getAttrib(attacker, ModAttributes.RANGED_ATTACK.get());
			}
		}

		event.setAmount(Math.max(0, event.getAmount()*res+mod));

		ServerAbilityHolder h = ServerAbilityHolder.of(entity);
		if(h!=null){
			for(OnAbilityEvent<LivingHurtEvent> e : h.onHurtListeners)
				e.onEvent(entity, h, event);
		}
	}

	private static double getAttrib(LivingEntity entity, Attribute attrib){
		ModifiableAttributeInstance a1 = entity.getAttribute(ModAttributes.MAGIC_DAMAGE_RESISTANCE.get());
		if(a1!=null) return a1.getValue();
		InfernoReborn.LOGGER.debug("wat");
		return attrib.getDefaultValue();
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onLivingHurtAfter(LivingHurtEvent event){
		Entity entity = event.getSource().getDirectEntity();
		if(entity instanceof LivingEntity){
			if(!entity.isAlive()) return;
			ServerAbilityHolder h = ServerAbilityHolder.of(entity);
			if(h!=null){
				for(OnAbilityEvent<LivingHurtEvent> e : h.onAttackListeners)
					e.onEvent((LivingEntity)entity, h, event);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event){
		LivingEntity entity = event.getEntityLiving();
		ServerAbilityHolder h = ServerAbilityHolder.of(entity);
		if(h!=null){
			for(OnAbilityEvent<LivingDeathEvent> e : h.onDeathListeners)
				e.onEvent(entity, h, event);
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

	@SubscribeEvent
	public static void onItemAttributeModifier(ItemAttributeModifierEvent event){
		SigilHolder h = SigilHolder.of(event.getItemStack());
		if(h==null) return;
		ListMultimap<Attribute, AttributeModifier> m = ArrayListMultimap.create(event.getModifiers());
		h.applyAttributes(event.getSlotType(), m);
		event.clearModifiers();
		m.forEach(event::addModifier);
	}
}
