package ttmp.infernoreborn.event;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttmp.infernoreborn.ability.OnAbilityEvent;
import ttmp.infernoreborn.ability.holder.AbilityHolder;
import ttmp.infernoreborn.ability.holder.ClientAbilityHolder;
import ttmp.infernoreborn.ability.holder.ServerAbilityHolder;
import ttmp.infernoreborn.capability.ShieldHolder;
import ttmp.infernoreborn.container.listener.SigilHolderSynchronizer;
import ttmp.infernoreborn.contents.ModAttributes;
import ttmp.infernoreborn.sigil.holder.ItemSigilHolder;
import ttmp.infernoreborn.sigil.holder.PlayerSigilHolder;
import ttmp.infernoreborn.sigil.holder.SigilHolder;
import ttmp.infernoreborn.util.LivingUtils;

import static ttmp.infernoreborn.InfernoReborn.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class CommonEventHandlers{
	private CommonEventHandlers(){}

	private static final ResourceLocation ABILITY_HOLDER_KEY = new ResourceLocation(MODID, "ability_holder");
	private static final ResourceLocation SIGIL_HOLDER_KEY = new ResourceLocation(MODID, "sigil_holder");
	private static final ResourceLocation SHIELD_HOLDER_KEY = new ResourceLocation(MODID, "shield_holder");

	@SubscribeEvent
	public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event){
		Entity e = event.getObject();
		if(!(e instanceof LivingEntity)) return;
		event.addCapability(SHIELD_HOLDER_KEY, new ShieldHolder((LivingEntity)e));
		if(e instanceof PlayerEntity) event.addCapability(SIGIL_HOLDER_KEY, new PlayerSigilHolder((PlayerEntity)e));
		else event.addCapability(ABILITY_HOLDER_KEY, e.level.isClientSide ? new ClientAbilityHolder() : new ServerAbilityHolder());
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

		if(!entity.level.isClientSide&&entity.invulnerableTime<=0){
			float regen = (float)LivingUtils.getAttrib(entity, ModAttributes.REGENERATION.get());
			if(regen>0) entity.heal(regen);

			float shieldRegen = (float)LivingUtils.getAttrib(entity, ModAttributes.SHIELD_REGEN.get());
			if(shieldRegen>0) LivingUtils.addShield(entity, shieldRegen);
		}

		AbilityHolder h = AbilityHolder.of(entity);
		if(h!=null) h.update(entity);
	}

	@SubscribeEvent
	public static void onLivingAttack(LivingAttackEvent event){
		if(event.getSource()==DamageSource.FALL&&LivingUtils.getAttrib(event.getEntityLiving(), ModAttributes.FALLING_DAMAGE_RESISTANCE.get())>=2)
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event){
		LivingEntity entity = event.getEntityLiving();
		LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity ? (LivingEntity)event.getSource().getEntity() : null;
		float res = (float)LivingUtils.getAttrib(entity, ModAttributes.DAMAGE_RESISTANCE.get());
		float mod = 0;

		if(event.getSource()==DamageSource.FALL)
			res += LivingUtils.getAttrib(event.getEntityLiving(), ModAttributes.FALLING_DAMAGE_RESISTANCE.get())-1;
		else if(!event.getSource().isBypassInvul()){
			if(event.getSource().isMagic()){
				res += LivingUtils.getAttrib(event.getEntityLiving(), ModAttributes.MAGIC_DAMAGE_RESISTANCE.get())-1;
				if(attacker!=null) mod += LivingUtils.getAttrib(attacker, ModAttributes.MAGIC_ATTACK.get());
			}
			if(attacker!=null&&event.getSource().getDirectEntity()==attacker){
				res += LivingUtils.getAttrib(event.getEntityLiving(), ModAttributes.MELEE_DAMAGE_RESISTANCE.get())-1;
			}
			if(event.getSource().isProjectile()){
				res += LivingUtils.getAttrib(event.getEntityLiving(), ModAttributes.RANGED_DAMAGE_RESISTANCE.get())-1;
				if(attacker!=null) mod += LivingUtils.getAttrib(attacker, ModAttributes.RANGED_ATTACK.get());
			}
		}

		event.setAmount(Math.max(0, event.getAmount()*res+mod));

		ServerAbilityHolder h = ServerAbilityHolder.of(entity);
		if(h!=null){
			for(OnAbilityEvent<LivingHurtEvent> e : h.onHurtListeners)
				e.onEvent(entity, h, event);
		}
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
	public static void onLivingDamage(LivingDamageEvent event){
		LivingEntity entity = event.getEntityLiving();
		ShieldHolder h = ShieldHolder.of(entity);
		if(h!=null&&h.getShield()>0){
			if(h.getShield()>=event.getAmount()){
				event.setAmount(0);
				h.setShield(h.getShield()-event.getAmount());
			}else{
				event.setAmount(event.getAmount()-h.getShield());
				h.setShield(0);
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
