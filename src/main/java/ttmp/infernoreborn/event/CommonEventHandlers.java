package ttmp.infernoreborn.event;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttmp.infernoreborn.capability.ShieldHolder;
import ttmp.infernoreborn.capability.SimpleTickingTaskHandler;
import ttmp.infernoreborn.capability.TickingTaskHandler;
import ttmp.infernoreborn.contents.ModAttributes;
import ttmp.infernoreborn.contents.ModEffects;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.ability.OnAbilityEvent;
import ttmp.infernoreborn.contents.ability.holder.AbilityHolder;
import ttmp.infernoreborn.contents.ability.holder.ClientAbilityHolder;
import ttmp.infernoreborn.contents.ability.holder.ServerAbilityHolder;
import ttmp.infernoreborn.contents.sigil.holder.ItemSigilHolder;
import ttmp.infernoreborn.contents.sigil.holder.PlayerSigilHolder;
import ttmp.infernoreborn.contents.sigil.holder.SigilHolder;
import ttmp.infernoreborn.util.ArmorSet;
import ttmp.infernoreborn.util.CannotHurtNonLiving;
import ttmp.infernoreborn.util.LivingUtils;
import ttmp.infernoreborn.util.SigilUtils;

import static ttmp.infernoreborn.InfernoReborn.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class CommonEventHandlers{
	private CommonEventHandlers(){}

	private static final ResourceLocation ABILITY_HOLDER_KEY = new ResourceLocation(MODID, "ability_holder");
	private static final ResourceLocation SIGIL_HOLDER_KEY = new ResourceLocation(MODID, "sigil_holder");
	private static final ResourceLocation SHIELD_HOLDER_KEY = new ResourceLocation(MODID, "shield_holder");
	private static final ResourceLocation TICKING_TASK_HANDLER_KEY = new ResourceLocation(MODID, "ticking_task_handler");

	private static final ArmorSet CRIMSON_ARMOR_SET = new ArmorSet.ItemSet(null, ModItems.CRIMSON_CHESTPLATE, ModItems.CRIMSON_LEGGINGS, ModItems.CRIMSON_BOOTS);
	private static final ArmorSet BERSERKER_ARMOR_SET = new ArmorSet.ItemSet(ModItems.BERSERKER_HELMET, ModItems.BERSERKER_CHESTPLATE, ModItems.BERSERKER_LEGGINGS, ModItems.BERSERKER_BOOTS);

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
	public static void attachWorldCapabilities(AttachCapabilitiesEvent<World> event){
		event.addCapability(TICKING_TASK_HANDLER_KEY, new SimpleTickingTaskHandler());
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

		ServerAbilityHolder h = ServerAbilityHolder.of(event.getEntityLiving());
		if(h!=null){
			for(OnAbilityEvent<LivingAttackEvent> e : h.onAttackedListeners)
				e.onEvent(event.getEntityLiving(), h, event);
		}
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
			if(!event.getSource().isMagic()&&event.getSource().isProjectile()){
				res += LivingUtils.getAttrib(event.getEntityLiving(), ModAttributes.RANGED_DAMAGE_RESISTANCE.get())-1;
				if(attacker!=null) mod += LivingUtils.getAttrib(attacker, ModAttributes.RANGED_ATTACK.get());
			}
		}

		float dmg = event.getAmount()+mod;
		event.setAmount(Math.max(Math.min(1, dmg), dmg*(2-res)));

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
				for(OnAbilityEvent<LivingHurtEvent> e : h.onHitListeners)
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
		if(event.getAmount()>0){
			Entity directEntity = event.getSource().getDirectEntity();
			if(entity!=directEntity){
				if(CRIMSON_ARMOR_SET.qualifies(entity)){ // TODO no "spam to full stack"
					LivingUtils.addStackEffect(entity, ModEffects.BLOOD_FRENZY.get(), 80, 0, 1, 3);
				}
				if(directEntity instanceof LivingEntity){
					LivingEntity e = (LivingEntity)directEntity;
					EffectInstance bloodFrenzy = e.getEffect(ModEffects.BLOOD_FRENZY.get());
					if(bloodFrenzy!=null){
						float drainPortion = (float)(1+bloodFrenzy.getAmplifier())/(2+bloodFrenzy.getAmplifier());
						e.heal(event.getAmount()*drainPortion);
					}
					if(CRIMSON_ARMOR_SET.qualifies(e)){
						LivingUtils.addStackEffect(e, ModEffects.BLOOD_FRENZY.get(), 80, 0, 1, 3);
					}
				}
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
		SigilUtils.applyAttributes(h, event.getSlotType(), m);
		event.clearModifiers();
		m.forEach(event::addModifier);
	}

	@SubscribeEvent
	public static void onWorldTick(TickEvent.WorldTickEvent event){
		if(event.phase!=TickEvent.Phase.START) return;
		TickingTaskHandler h = TickingTaskHandler.of(event.world);
		if(h instanceof SimpleTickingTaskHandler) ((SimpleTickingTaskHandler)h).update();
	}

	@SubscribeEvent
	public static void onExplosionDetonate(ExplosionEvent.Detonate event){
		Explosion explosion = event.getExplosion();
		if(explosion.getDamageSource() instanceof CannotHurtNonLiving){
			event.getAffectedEntities().removeIf(entity -> !(entity instanceof LivingEntity));
		}
	}

	@SubscribeEvent
	public static void onCriticalHit(CriticalHitEvent event){
		ItemStack itemInHand = event.getPlayer().getItemInHand(Hand.MAIN_HAND);
		if(itemInHand.getItem()==ModItems.DRAGON_SLAYER.get()){
			if(BERSERKER_ARMOR_SET.qualifies(event.getPlayer())){
				event.setDamageModifier(event.getDamageModifier()+1.5f);
			}else if(event.getTarget() instanceof LivingEntity&&((LivingEntity)event.getTarget()).getMaxHealth()>=30){
				event.setDamageModifier(event.getDamageModifier()+.5f);
			}
		}
	}
}
