package ttmp.infernoreborn.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Hand;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import ttmp.infernoreborn.api.Caps;
import ttmp.infernoreborn.api.LivingUtils;
import ttmp.infernoreborn.api.essence.Essence;
import ttmp.infernoreborn.api.essence.EssenceHandler;
import ttmp.infernoreborn.capability.PlayerCapability;
import ttmp.infernoreborn.contents.ModEffects;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.shield.ArmorSets;
import ttmp.infernoreborn.util.SigilUtils;

import javax.annotation.Nullable;
import java.util.List;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public final class PlayerEventHandlers{
	private PlayerEventHandlers(){}

	private static final double CLOUD_SCARF_FLYING_SPEED = 0.02*2.5;

	@Nullable private static PlayerEntity player;
	@Nullable private static Entity target;
	private static float attackStrength;

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event){
		PlayerEntity original = event.getOriginal();
		LivingEntity entityLiving = event.getEntityLiving();
		PlayerCapability c1 = PlayerCapability.of(original);
		PlayerCapability c2 = PlayerCapability.of(entityLiving);
		if(c1!=null&&c2!=null) c1.copyTo(c2);
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event){
		if(CuriosApi.getCuriosHelper()
				.findFirstCurio(event.player, ModItems.CLOUD_SCARF.get())
				.isPresent()){
			event.player.flyingSpeed = (float)(CLOUD_SCARF_FLYING_SPEED);
		}
		if(event.side!=LogicalSide.SERVER) return;
		PlayerCapability c = PlayerCapability.of(event.player);
		if(c!=null) c.update();
		SigilUtils.forEachSigilHolder(event.player,
				(sigilHolder, sigilSlot) -> SigilUtils.onTick(sigilHolder, sigilSlot, event.player));
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onPlayerAttack(AttackEntityEvent event){
		player = event.getPlayer();
		target = event.getTarget();
		attackStrength = event.getPlayer().getAttackStrengthScale(.5f);
	}

	private static boolean isAttackStrengthFull(Entity attacker, Entity target){
		return attacker==player&&target==PlayerEventHandlers.target&&attackStrength>.9f;
	}

	@SubscribeEvent
	public static void onLivingDamage(LivingDamageEvent event){
		LivingEntity entity = event.getEntityLiving();
		if(event.getAmount()>0){
			Entity directEntity = event.getSource().getDirectEntity();
			if(entity!=directEntity){
				if(ArmorSets.CRIMSON.qualifies(entity)){
					LivingUtils.addStackEffect(entity, ModEffects.BLOOD_FRENZY.get(), 80, 0, 1, 3);
				}
				if(directEntity instanceof LivingEntity){
					LivingEntity e = (LivingEntity)directEntity;
					EffectInstance bloodFrenzy = e.getEffect(ModEffects.BLOOD_FRENZY.get());
					if(bloodFrenzy!=null){
						float drainPortion = (float)(1+bloodFrenzy.getAmplifier())/(2+bloodFrenzy.getAmplifier());
						e.heal(event.getAmount()*drainPortion);
					}
					if(isAttackStrengthFull(e, entity)&&ArmorSets.CRIMSON.qualifies(e)){
						LivingUtils.addStackEffect(e, ModEffects.BLOOD_FRENZY.get(), 80, 0, 1, 3);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onCriticalHit(CriticalHitEvent event){
		ItemStack itemInHand = event.getPlayer().getItemInHand(Hand.MAIN_HAND);
		if(itemInHand.getItem()==ModItems.DRAGON_SLAYER.get()){
			if(ArmorSets.BERSERKER.qualifies(event.getPlayer())){
				event.setDamageModifier(event.getDamageModifier()+1.5f);
			}else if(event.getTarget() instanceof LivingEntity&&((LivingEntity)event.getTarget()).getMaxHealth()>=30){
				event.setDamageModifier(event.getDamageModifier()+.5f);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onLivingHurtLast(LivingHurtEvent event){
		PlayerCapability pc = PlayerCapability.of(event.getEntityLiving());
		if(pc!=null) event.setAmount(pc.applyShieldReduction(event.getAmount()));
	}

	@SuppressWarnings("ConstantConditions")
	@SubscribeEvent
	public static void onEntityItemPickup(EntityItemPickupEvent event){
		ItemEntity e = event.getItem();
		if(e.getOwner()!=null&&e.lifespan-e.getAge()>200&&!e.getOwner().equals(event.getPlayer().getUUID()))
			return; // from ItemEntity
		ItemStack stack = e.getItem();
		Essence essence = Essence.from(stack);
		if(essence!=null){
			int amount = essence.getAmount();
			if(amount>0){
				List<SlotResult> curios = CuriosApi.getCuriosHelper().findCurios(event.getPlayer(), "essence_holder");
				if(curios.isEmpty()) return;
				for(SlotResult curio : curios){
					EssenceHandler h = curio.getStack().getCapability(Caps.essenceHandler).orElse(null);
					if(h!=null){
						amount -= h.insertEssence(essence.getType(), amount, false);
						if(amount==0) break;
					}
				}
			}
			if(essence.getAmount()==amount) return;
			event.setResult(Event.Result.ALLOW);
			e.setItem(ItemStack.EMPTY);
			for(ItemStack s : Essence.items(essence.getType(), amount)){ // spawn leftover
				ItemEntity e2 = new ItemEntity(e.level, e.getX(), e.getY(), e.getZ());
				e2.yRot = e.yRot;
				e2.setDeltaMovement(e.getDeltaMovement());
				e2.setItem(s);
				e.level.addFreshEntity(e2);
			}
		}
	}
}
