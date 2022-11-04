package ttmp.infernoreborn.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttmp.infernoreborn.api.ability.AbilityHolder;
import ttmp.infernoreborn.api.ability.OnAbilityEvent;
import ttmp.infernoreborn.capability.ServerAbilityHolder;
import ttmp.infernoreborn.contents.ModAttributes;
import ttmp.infernoreborn.util.SigilUtils;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;
import static ttmp.infernoreborn.util.AttribUtils.getAttrib;

@Mod.EventBusSubscriber(modid = MODID)
public final class LivingEventHandlers{
	private LivingEventHandlers(){}

	@SubscribeEvent
	public static void onLivingUpdate(LivingUpdateEvent event){
		LivingEntity entity = event.getEntityLiving();
		if(!entity.isAlive()) return;

		if(!entity.level.isClientSide&&entity.invulnerableTime<=0){
			float regen = (float)getAttrib(entity, ModAttributes.REGENERATION.get());
			if(regen>0) entity.heal(regen);
		}

		AbilityHolder h = AbilityHolder.of(entity);
		if(h!=null) h.update(entity);
	}

	@SubscribeEvent
	public static void onLivingAttack(LivingAttackEvent event){
		if(event.getSource()==DamageSource.FALL&&getAttrib(event.getEntityLiving(), ModAttributes.FALLING_DAMAGE_RESISTANCE.get())>=2)
			event.setCanceled(true);

		ServerAbilityHolder h = ServerAbilityHolder.of(event.getEntityLiving());
		if(h!=null){
			for(OnAbilityEvent<LivingAttackEvent> e : h.onAttackedListeners)
				e.onEvent(event.getEntityLiving(), h, event);
		}
		if(event.isCanceled()) return;
		Entity directEntity = event.getSource().getDirectEntity();
		if(directEntity instanceof LivingEntity){
			LivingEntity livingEntity = (LivingEntity)directEntity;
			SigilUtils.forEachSigilHolder(livingEntity, (sigilHolder, sigilSlot) -> SigilUtils.onAttack(sigilHolder, sigilSlot, event, livingEntity));
		}
		SigilUtils.forEachSigilHolder(event.getEntityLiving(), (sigilHolder, sigilSlot) -> SigilUtils.onAttacked(sigilHolder, sigilSlot, event));
	}

	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event){
		LivingEntity entity = event.getEntityLiving();
		LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity ? (LivingEntity)event.getSource().getEntity() : null;
		float res;
		float mod = 0;

		if(event.getSource()==DamageSource.FALL)
			res = (float)(getAttrib(event.getEntityLiving(), ModAttributes.FALLING_DAMAGE_RESISTANCE.get())-1);
		else if(!event.getSource().isBypassInvul()){
			res = (float)getAttrib(entity, ModAttributes.DAMAGE_RESISTANCE.get());
			if(event.getSource().isMagic())
				res += getAttrib(event.getEntityLiving(), ModAttributes.MAGIC_DAMAGE_RESISTANCE.get())-1;
			if(attacker!=null&&event.getSource().getDirectEntity()==attacker)
				res += getAttrib(event.getEntityLiving(), ModAttributes.MELEE_DAMAGE_RESISTANCE.get())-1;
			if(!event.getSource().isMagic()&&event.getSource().isProjectile())
				res += getAttrib(event.getEntityLiving(), ModAttributes.RANGED_DAMAGE_RESISTANCE.get())-1;
		}else res = 0;

		if(attacker!=null){
			if(event.getSource().isMagic())
				mod += getAttrib(attacker, ModAttributes.MAGIC_ATTACK.get());
			if(!event.getSource().isMagic()&&event.getSource().isProjectile())
				mod += getAttrib(attacker, ModAttributes.RANGED_ATTACK.get());
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
		Entity entity = event.getSource().getEntity();
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
	public static void onLivingDrops(LivingDropsEvent event){
		ServerAbilityHolder h = ServerAbilityHolder.of(event.getEntityLiving());
		if(h!=null&&h.disableDrop()) event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onLivingExperienceDrop(LivingExperienceDropEvent event){
		ServerAbilityHolder h = ServerAbilityHolder.of(event.getEntityLiving());
		if(h!=null&&h.disableDrop()) event.setCanceled(true);
	}

}
