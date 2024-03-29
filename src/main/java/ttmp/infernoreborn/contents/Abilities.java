package ttmp.infernoreborn.contents;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import ttmp.infernoreborn.api.LivingUtils;
import ttmp.infernoreborn.api.TickingTaskHandler;
import ttmp.infernoreborn.api.ability.Ability;
import ttmp.infernoreborn.api.essence.EssenceType;
import ttmp.infernoreborn.contents.ability.FearAbility;
import ttmp.infernoreborn.contents.ability.KillerQueenAbility;
import ttmp.infernoreborn.contents.ability.SkinAbility;
import ttmp.infernoreborn.contents.ability.SlimeBloodAbility;
import ttmp.infernoreborn.contents.ability.SummonAbility;
import ttmp.infernoreborn.contents.ability.VeteranAbility;
import ttmp.infernoreborn.contents.ability.WindAbility;
import ttmp.infernoreborn.contents.entity.AnvilEntity;
import ttmp.infernoreborn.contents.entity.CreeperMissileEntity;
import ttmp.infernoreborn.network.ModNet;
import ttmp.infernoreborn.network.ParticleMsg;
import ttmp.infernoreborn.util.damage.Damages;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = MODID, bus = Bus.MOD)
public final class Abilities{
	private Abilities(){}

	private static ForgeRegistry<Ability> registry;

	public static ForgeRegistry<Ability> getRegistry(){
		return registry;
	}

	public static final DeferredRegister<Ability> REGISTER = DeferredRegister.create(Ability.class, MODID);

	//////////////////////////////////////////////////
	//
	// Hearts
	//
	//////////////////////////////////////////////////

	public static final RegistryObject<Ability> HEART = REGISTER.register("heart", heart("55924d9f-ac7a-4472-bfea-bc4e305a363f", 1, 1));
	public static final RegistryObject<Ability> HEART2 = REGISTER.register("heart2", heart("4cff8f05-0d74-4f19-89d5-577eef01d279", 3, 3));
	public static final RegistryObject<Ability> HEART3 = REGISTER.register("heart3", heart("07e95560-304a-4564-88f8-96b612e72bdf", 5, 5));
	public static final RegistryObject<Ability> HEART4 = REGISTER.register("heart4", heart("66fb3897-dc1f-4968-b13e-ef34c3ea0f87", 10, 9));
	public static final RegistryObject<Ability> HEART5 = REGISTER.register("heart5", heart("7976d7af-2ff2-423f-b9f8-b56eb36f507a", 20, 2*9));
	public static final RegistryObject<Ability> HEART6 = REGISTER.register("heart6", heart("d2532167-e291-497e-8351-360a423b48e4", 30, 3*9));
	public static final RegistryObject<Ability> HEART7 = REGISTER.register("heart7", heart("36e4c564-5d50-4848-a507-5df6c0ec3814", 40, 5*9));
	public static final RegistryObject<Ability> HEART8 = REGISTER.register("heart8", heart("d10754b8-0ce5-4d5c-a82e-3ef40b121c4f", 50, 7*9));
	public static final RegistryObject<Ability> HEART9 = REGISTER.register("heart9", heart("ec37932d-85e5-4846-8384-5849cbd94423", 100, 9*9));
	public static final RegistryObject<Ability> HEART10 = REGISTER.register("heart10", heart("41099395-0e3c-4bf7-ab85-867b1ea87132", 200, 2*9*9));

	//////////////////////////////////////////////////
	//
	// Skins
	//
	//////////////////////////////////////////////////

	public static final RegistryObject<Ability> WOOD_SKIN = REGISTER.register("wood_skin", SkinAbility::woodSkin);
	public static final RegistryObject<Ability> ROCK_SKIN = REGISTER.register("rock_skin", SkinAbility::rockSkin);
	public static final RegistryObject<Ability> IRON_SKIN = REGISTER.register("iron_skin", SkinAbility::ironSkin);
	public static final RegistryObject<Ability> DIAMOND_SKIN = REGISTER.register("diamond_skin", SkinAbility::diamondSkin);
	public static final RegistryObject<Ability> NETHERITE_SKIN = REGISTER.register("netherite_skin", SkinAbility::netheriteSkin);
	public static final RegistryObject<Ability> MUD_SKIN = REGISTER.register("mud_skin", SkinAbility::mudSkin);
	public static final RegistryObject<Ability> FROZEN_SKIN = REGISTER.register("frozen_skin", SkinAbility::frozenSkin);
	public static final RegistryObject<Ability> WOOLLY_SKIN = REGISTER.register("woolly_skin", SkinAbility::woollySkin);
	public static final RegistryObject<Ability> FUZZY_SKIN = REGISTER.register("fuzzy_skin", SkinAbility::fuzzySkin);
	public static final RegistryObject<Ability> THORN_SKIN = REGISTER.register("thorn_skin", SkinAbility::thornSkin);
	public static final RegistryObject<Ability> MAGMA_SKIN = REGISTER.register("magma_skin", SkinAbility::magmaSkin);
	public static final RegistryObject<Ability> ELECTRIC_SKIN = REGISTER.register("electric_skin", SkinAbility::electricSkin);

	//////////////////////////////////////////////////
	//
	// Wind
	//
	//////////////////////////////////////////////////

	public static final RegistryObject<Ability> WINDBLAST = REGISTER.register("windblast", () -> WindAbility.wind(0));
	public static final RegistryObject<Ability> WINDBLAST2 = REGISTER.register("windblast2", () -> WindAbility.wind(1));
	public static final RegistryObject<Ability> WINDBLAST3 = REGISTER.register("windblast3", () -> WindAbility.wind(2));
	public static final RegistryObject<Ability> BLINDING_WIND = REGISTER.register("blinding_wind", WindAbility::blindingWind);
	public static final RegistryObject<Ability> FUZZY_WIND = REGISTER.register("fuzzy_wind", WindAbility::fuzzyWind);
	public static final RegistryObject<Ability> SCALDING_WIND = REGISTER.register("scalding_wind", WindAbility::scaldingWind);
	public static final RegistryObject<Ability> FREEZING_WIND = REGISTER.register("freezing_wind", WindAbility::freezingWind);
	public static final RegistryObject<Ability> POISONING_WIND = REGISTER.register("poisoning_wind", WindAbility::poisoningWind);

	//////////////////////////////////////////////////
	//
	// Common Abilities
	//
	//////////////////////////////////////////////////

	public static final RegistryObject<Ability> BULLETPROOF = REGISTER.register("bulletproof", () ->
			new Ability(new Ability.Properties(0x827b7a, 0x302c2b, 0xb89f9a)
					.addAttribute(ModAttributes.RANGED_DAMAGE_RESISTANCE.get(), "fa9b6063-66b7-4bbd-a4d8-1d35a088fa92", .8, Operation.MULTIPLY_BASE)
					.drops(EssenceType.METAL, 9)));

	public static final RegistryObject<Ability> BLASTPROOF = REGISTER.register("blastproof", () ->
			new Ability(new Ability.Properties(0x7f8f7b, 0x303d2c, 0xafbfaa)
					.onHit((entity, holder, event) -> {
						if(event.getSource().isExplosion()) event.setAmount(event.getAmount()*.2f);
					}).drops(EssenceType.WATER, 9)));

	public static final RegistryObject<Ability> MELEE_VETERAN = REGISTER.register("melee_veteran", VeteranAbility::meleeVeteran);
	public static final RegistryObject<Ability> RANGED_VETERAN = REGISTER.register("ranged_veteran", VeteranAbility::rangedVeteran);
	public static final RegistryObject<Ability> MAGIC_VETERAN = REGISTER.register("magic_veteran", VeteranAbility::magicVeteran);

	public static final RegistryObject<Ability> VAMPIRE = REGISTER.register("vampire", () ->
			new Ability(new Ability.Properties(0x110000, 0x410d0d, 0xcd0202)
					.onHit((entity, holder, event) -> {
						float amount = event.getAmount();
						if(entity!=event.getSource().getDirectEntity()) amount /= 2;
						entity.heal(amount);
					})
					.drops(EssenceType.BLOOD, 5)
					.drops(EssenceType.DEATH, 5)));

	public static final RegistryObject<Ability> SURVIVAL_EXPERT = REGISTER.register("survival_expert", () ->
			new Ability(new Ability.Properties(0x85cd30, 0x164d07)
					.onUpdate((entity, holder) -> {
						if(entity.isOnFire()) entity.clearFire();
						if(!entity.getActiveEffects().isEmpty()){
							List<EffectInstance> removeEffectsList = null;
							for(EffectInstance e : entity.getActiveEffects()){
								if(!e.getEffect().isBeneficial()){
									if(removeEffectsList==null) removeEffectsList = new ArrayList<>();
									removeEffectsList.add(e);
								}
							}
							if(removeEffectsList!=null)
								for(EffectInstance e : removeEffectsList)
									entity.removeEffect(e.getEffect());
						}
					})
					.addAttribute(ModAttributes.FALLING_DAMAGE_RESISTANCE.get(), "f7868c95-cd1a-4919-9d8e-55f2f4a646d1", 1, Operation.MULTIPLY_BASE)
					.drops(EssenceType.EARTH, 2)
					.drops(EssenceType.FIRE, 2)
					.drops(EssenceType.AIR, 2)
					.drops(EssenceType.WATER, 2)));

	public static final RegistryObject<Ability> THE_BRAIN = REGISTER.register("the_brain", () ->
			new Ability(new Ability.Properties(0xf5bfa4, 0x992614, 0xf2bcb3)
					.addSkill(10, 300, (entity, holder) -> {
						if(entity.getLastHurtByMob()==null) return false;
						LivingUtils.forEachLivingEntitiesInCylinder(entity, 32, 10,
								e -> e.setLastHurtByMob(entity.getLastHurtByMob()));
						return true;
					}, (entity, holder) -> entity.getLastHurtByMob()!=null)
					.addAttribute(Attributes.MAX_HEALTH, "2d145dfc-dda4-4fc0-aa35-6666eae0a776", 0.25, Operation.ADDITION)
					.drops(EssenceType.DOMINANCE, 4)
					.drops(EssenceType.DEATH, 6)));

	public static final RegistryObject<Ability> TOUGHNESS = REGISTER.register("toughness", () ->
			new Ability(new Ability.Properties(0xd8d8d8, 0xb91955, 0xf4f4f4)
					.addAttribute(Attributes.MOVEMENT_SPEED, "ec3aa988-af1f-4ae9-8cf6-b1d5e62addaa", -0.2, Operation.MULTIPLY_TOTAL)
					.addAttribute(ModAttributes.DAMAGE_RESISTANCE.get(), "9c03cdb9-bb2d-4d03-8766-26632b0966df", .2, Operation.MULTIPLY_BASE)
					.drops(EssenceType.EARTH, 5)
					.drops(EssenceType.WATER, 5)));

	public static final RegistryObject<Ability> SWIFTNESS = REGISTER.register("swiftness", () ->
			new Ability(new Ability.Properties(0xd8d8d8, 0x1061b3, 0xf4f4f4)
					.addAttribute(Attributes.MOVEMENT_SPEED, "0759550c-3e02-4dee-89b8-2d490347da5e", 0.3, Operation.MULTIPLY_TOTAL)
					.addAttribute(ModAttributes.DAMAGE_RESISTANCE.get(), "3db07150-8675-4c9e-acf1-c35100cf76b8", -.1, Operation.MULTIPLY_BASE)
					.drops(EssenceType.AIR, 5)
					.drops(EssenceType.FIRE, 5)));

	public static final RegistryObject<Ability> SENTRY = REGISTER.register("sentry", () ->
			new Ability(new Ability.Properties(0xb91c07, 0x454545, 0x858585)
					.onUpdate((entity, holder) -> holder.cooldown().decreaseAll(4))
					.addAttribute(Attributes.MOVEMENT_SPEED, "41669826-6fde-4dc6-b67f-28a28a1f2dbb", -0.9, Operation.MULTIPLY_BASE)
					.drops(EssenceType.EARTH, 9)));

	public static final RegistryObject<Ability> EVIOLITE_CHANSEY = REGISTER.register("eviolite_chansey", () ->
			new Ability(new Ability.Properties(0xff92be, 0xb62993, 0xffb7f2)
					.addAttribute(Attributes.ATTACK_DAMAGE, "ba20b5e3-e189-444f-9233-c710d7ac810e", -0.8, Operation.MULTIPLY_TOTAL)
					.addAttribute(Attributes.MAX_HEALTH, "f72d69be-39ff-4dbc-b938-ff0740ad528c", 1.5, Operation.MULTIPLY_TOTAL)
					.addAttribute(ModAttributes.DAMAGE_RESISTANCE.get(), "41bd35ff-0f02-434c-81a2-be6791739e00", .4, Operation.MULTIPLY_BASE)
					.drops(EssenceType.METAL, 5)
					.drops(EssenceType.WATER, 5)));

	public static final RegistryObject<Ability> POISONED_MIND = REGISTER.register("poisoned_mind", () ->
			new Ability(new Ability.Properties(0xea0ed8, 0x6eeaa8, 0xa3e1f3)
					.onDeath((entity, holder, event) -> {
						Potion[] potionArray = {Potions.SLOWNESS, Potions.STRONG_SLOWNESS, Potions.LONG_SLOWNESS, Potions.HARMING, Potions.STRONG_HARMING, Potions.POISON, Potions.LONG_POISON, Potions.STRONG_POISON, Potions.WEAKNESS, Potions.LONG_WEAKNESS};
						PotionEntity potionEntity = new PotionEntity(entity.level, entity);
						potionEntity.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potionArray[entity.getRandom().nextInt(potionArray.length)]));
						float xRot = (float)(entity.getRandom().nextDouble()*2*Math.PI);
						potionEntity.setDeltaMovement(new Vector3d(MathHelper.sin(xRot), entity.getRandom().nextDouble()*0.5+.5, MathHelper.cos(xRot)).normalize().scale(.5));
						entity.level.addFreshEntity(potionEntity);
					}).drops(EssenceType.MAGIC, 9)));

	public static final RegistryObject<Ability> SLIME_BLOOD = REGISTER.register("slime_blood", SlimeBloodAbility::slimeBlood);

	public static final RegistryObject<Ability> TOUCH_OF_MIDAS = REGISTER.register("touch_of_midas", () ->
			new Ability(new Ability.Properties(0xf69c0e, 0xf0d93e, 0xf7e468)
					.onHit((entity, holder, event) -> {
						if(entity!=event.getSource().getDirectEntity()) return;
						LivingEntity target = event.getEntityLiving();
						LivingUtils.addStackEffect(target, ModEffects.HAND_OF_MIDAS.get(), 100, 0, 1, 127);
						EffectInstance effect = target.getEffect(ModEffects.HAND_OF_MIDAS.get());
						if(effect!=null&&target.getHealth()<=effect.getAmplifier()+1){
							if(target.hurt(Damages.midas(entity), Float.MAX_VALUE)){
								target.level.addFreshEntity(new ItemEntity(target.level,
										target.getRandomX(1),
										target.getRandomY(),
										target.getRandomZ(1),
										new ItemStack(ModItems.GOLDEN_SKULL.get()).setHoverName(target.getName())));
							}
						}
					}).drops(EssenceType.MAGIC, 5)
					.drops(EssenceType.DOMINANCE, 5)));

	//////////////////////////////////////////////////
	//
	// Rare Abilities
	//
	//////////////////////////////////////////////////

	public static final RegistryObject<Ability> CROWD_CONTROL = REGISTER.register("crowd_control", () ->
			new Ability(new Ability.Properties(0x8a38df, 0x870059, 0x870059)
					.onHit((entity, holder, event) -> {
						event.getEntityLiving().addEffect(new EffectInstance(Effects.LEVITATION, 100));
						LivingUtils.addStackEffect(event.getEntityLiving(), Effects.DIG_SLOWDOWN, 100, 0, 1, 5, true, true);
					}).drops(EssenceType.DOMINANCE, 4*9)));

	public static final RegistryObject<Ability> DESTINY_BOND = REGISTER.register("destiny_bond", () ->
			new Ability(new Ability.Properties(0x8a38df, 0x2b0981, 0x2b0981)
					.onDeath((entity, holder, event) -> {
						Entity target = event.getSource().getEntity();
						if(!(target instanceof LivingEntity)) return;
						target.hurt(DamageSource.MAGIC, (float)Math.pow(entity.position().distanceTo(target.position()), 1.5));
					}).drops(EssenceType.MAGIC, 12)
					.drops(EssenceType.DEATH, 12)));

	public static final RegistryObject<Ability> FOCUS = REGISTER.register("focus", () ->
			new Ability(new Ability.Properties(0x693a38, 0xe47512, 0xa0a0a0)
					.onHurt((entity, holder, event) -> {
						if(event.getSource().getEntity() instanceof LivingEntity){
							LivingEntity target = (LivingEntity)event.getSource().getEntity();
							if(target!=null&&target==event.getSource().getDirectEntity())
								event.setAmount(event.getAmount()*.5f);
						}
					}).drops(EssenceType.BLOOD, 9)
					.drops(EssenceType.WATER, 9)
					.drops(EssenceType.FIRE, 9)));

	public static final RegistryObject<Ability> GUTS = REGISTER.register("guts", () ->
			new Ability(new Ability.Properties(0x494949, 0xab381e, 0xa0a0a0)
					.onHurt((entity, holder, event) -> LivingUtils.addStackEffect(entity, Effects.DAMAGE_RESISTANCE, 60, 0, 1, 3))
					.drops(EssenceType.METAL, 4*9)));

	public static final RegistryObject<Ability> HEALTH_KIT = REGISTER.register("health_kit", () ->
			new Ability(new Ability.Properties(0xf54343, 0xc3c3c3)
					.addSkill(10, 500, (entity, holder) -> {
						if(entity.getHealth()+10>=entity.getMaxHealth()) return false;
						entity.heal(10);
						return true;
					}, (entity, holder) -> entity.getHealth()+10<entity.getMaxHealth()).addAttribute(Attributes.MAX_HEALTH, "25261e88-72c5-49b9-8680-8253f3c73a30", 10, Operation.ADDITION)
					.drops(EssenceType.BLOOD, 3*9)
					.drops(EssenceType.WATER, 9)));

	public static final RegistryObject<Ability> TELEKINESIS = REGISTER.register("telekinesis", () ->
			new Ability(new Ability.Properties(0xb71100, 0x5c0c0c, 0xe70000)
					.addTargetedSkill(5, 250, (entity, holder, target) -> {
						double relX = (entity.getX()-target.getX());
						double relZ = (entity.getZ()-target.getZ());
						double dist = Math.sqrt(relX*relX+relZ*relZ);
						double pow = (1.5+(0.6*target.distanceTo(entity)))/7.0;

						target.setDeltaMovement(relX/dist*pow, 1.3/dist*pow, relZ/dist*pow);
						target.fallDistance -= 1.3/dist*pow;
						target.hurt(DamageSource.MAGIC, 2.5f);
						return true;
					}).drops(EssenceType.MAGIC, 2*9)
					.drops(EssenceType.WATER, 2*9)));

	public static final RegistryObject<Ability> AETHER_WALKER = REGISTER.register("aether_walker", () ->
			new Ability(new Ability.Properties(0xc0e4ff, 0xe3f3ff, 0xcaecfa)
					.addTargetedSkill(0, 7, (entity, holder, target) -> {
						double relX = (target.getX()-entity.getX());
						double relZ = (target.getZ()-entity.getZ());
						double dist = Math.sqrt(relX*relX+relZ*relZ);
						entity.setDeltaMovement(relX/dist*0.4, (target.getY(0.75)-entity.getY())*0.2, relZ/dist*0.4);
						return true;
					}, (entity, holder, target) -> target.getY()-entity.getY()>1||target.position().distanceTo(entity.position())>5)
					.addAttribute(ModAttributes.FALLING_DAMAGE_RESISTANCE.get(), "9ec4bc3d-d8a0-4b1a-a185-c34ca05e9175", 1, Operation.MULTIPLY_BASE)
					.drops(EssenceType.MAGIC, 2*9)
					.drops(EssenceType.AIR, 2*9)));

	public static final RegistryObject<Ability> THUNDERBOLT = REGISTER.register("thunderbolt", () ->
			new Ability(new Ability.Properties(0xffeb7e, 0xffcd03, 0xfff9bb)
					.addTargetedSkill(10, 400, (entity, holder, target) -> {
						TickingTaskHandler h = TickingTaskHandler.of(target.level);
						if(h==null) return false;
						World world = target.level;
						Vector3d dest = new Vector3d(target.getX(), target.getY(), target.getZ());
						h.add(80, () -> {
							LightningBoltEntity e = new LightningBoltEntity(EntityType.LIGHTNING_BOLT, world);
							e.moveTo(dest);
							world.addFreshEntity(e);
						});
						ModNet.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new ParticleMsg(80,
								1,
								1,
								ParticleTypes.FIREWORK,
								dest.add(0, .5, 0),
								new Vector3d(.2, .5, .2),
								Vector3d.ZERO,
								new Vector3d(.1, .2, .1),
								new int[0]));
						return true;
					})
					.onAttacked((entity, holder, event) -> {
						if(event.getSource().isFire()||event.getSource()==DamageSource.LIGHTNING_BOLT)
							event.setCanceled(true);
					}).drops(EssenceType.MAGIC, 2*9)
					.drops(EssenceType.FIRE, 9)
					.drops(EssenceType.AIR, 9)));

	public static final RegistryObject<Ability> THE_RED = REGISTER.register("the_red", () ->
			new Ability(new Ability.Properties(0xc80000, 0xc80000)
					.addAttribute(Attributes.MOVEMENT_SPEED, "33a6142f-5b04-490c-85ce-11cc79510f9a", 2, Operation.MULTIPLY_BASE)
					.addAttribute(ModAttributes.FALLING_DAMAGE_RESISTANCE.get(), "5cb842c2-beb3-4da0-b4c6-aa0323fe292a", 1, Operation.MULTIPLY_BASE)
					.drops(EssenceType.BLOOD, 2*9)
					.drops(EssenceType.FIRE, 2*9)));

	public static final RegistryObject<Ability> DAE_KAE_MOB = REGISTER.register("dae_kae_mob", () ->
			new Ability(new Ability.Properties(0xED1C27, 0x004EA1, 0x00A0E2)
					.addTargetedSkill(10, 60, (entity, holder, target) -> {
						target.level.addFreshEntity(new AnvilEntity(target.level, target.getX(), target.getY()+20, target.getZ()));
						return true;
					})
					.onAttacked((entity, holder, event) -> {
						if(event.getSource()==DamageSource.ANVIL) event.setCanceled(true);
					}).drops(EssenceType.METAL, 4*9)));

	public static final RegistryObject<Ability> BATTLE_MOMENTUM = REGISTER.register("battle_momentum", () ->
			new Ability(new Ability.Properties(0x5128d5, 0x6041f8, 0x6041f8)
					.onAttacked((entity, holder, event) -> holder.cooldown().decreaseAll(10))
					.onHit((entity, holder, event) -> holder.cooldown().decreaseAll(20))
					.drops(EssenceType.FIRE, 2*9)
					.drops(EssenceType.AIR, 9)
					.drops(EssenceType.DOMINANCE, 9)));

	public static final RegistryObject<Ability> EVIL_TRICK = REGISTER.register("evil_trick", () ->
			new Ability(new Ability.Properties(0x1d1d1d, 0x750000)
					.addTargetedSkill(5, 50, (entity, holder, target) -> {
						if(!(entity.getHealth()/entity.getMaxHealth()<0.4)) return false;
						final int[] healAmount = {0};
						LivingUtils.forEachLivingEntitiesInCylinder(entity, 12, 5, e -> {
							if(e.hurt(DamageSource.GENERIC, 1)) healAmount[0]++;
						});
						if(healAmount[0]>0){
							entity.heal(healAmount[0]);
							return true;
						}else return false;
					}, (entity, holder, target) -> entity.getHealth()/entity.getMaxHealth()<0.4)
					.drops(EssenceType.DEATH, 2*9)
					.drops(EssenceType.WATER, 9)
					.drops(EssenceType.DOMINANCE, 9)));

	public static final RegistryObject<Ability> CONDITIONAL_REFLEX = REGISTER.register("conditional_reflex", () ->
			new Ability(new Ability.Properties(0xe0fa87, 0x0d1814, 0xe5e3e2)
					.withCooldownTicket((ticket, properties) -> properties.onAttacked((entity, holder, event) -> {
						LivingEntity target = LivingUtils.getTarget(entity);
						if(target!=null){
							holder.cooldown().setGlobalDelay(5);
							holder.cooldown().set(ticket, 5);
							entity.doHurtTarget(target);
						}
					})).addAttribute(Attributes.ATTACK_DAMAGE, "9ff4653e-cd0e-48d1-b2ac-1627ae0f5700", 3, Operation.ADDITION)
					.drops(EssenceType.BLOOD, 2*9)
					.drops(EssenceType.FIRE, 9)
					.drops(EssenceType.DOMINANCE, 9)));

	public static final RegistryObject<Ability> MAGMA_BLOOD = REGISTER.register("magma_blood", SlimeBloodAbility::magmaBlood);
	public static final RegistryObject<Ability> DIABOLO = REGISTER.register("diabolo", FearAbility::diabolo);
	public static final RegistryObject<Ability> ZOMBIE_NECROMANCY = REGISTER.register("zombie_necromancy", SummonAbility::zombieNecromancy);
	public static final RegistryObject<Ability> SKELETON_NECROMANCY = REGISTER.register("skeleton_necromancy", SummonAbility::skeletonNecromancy);

	//////////////////////////////////////////////////
	//
	// Epic Abilities
	//
	//////////////////////////////////////////////////

	public static final RegistryObject<Ability> EMPERORS_AURA = REGISTER.register("emperors_aura", () ->
			new Ability(new Ability.Properties(0xb20000, 0xdcb600, 0xdcb600)
					.onHit((entity, holder, event) -> LivingUtils.forEachLivingEntitiesInCylinder(entity, 16, 10, e -> {
						if(e.isAlive()&&!(e instanceof PlayerEntity))
							LivingUtils.addStackEffect(e, Effects.DAMAGE_BOOST, 140, 0, 1, 64);
					}))
					.addAttribute(Attributes.ATTACK_DAMAGE, "4b220817-9f85-432f-9ce8-ac9d282b5d38", 2, Operation.MULTIPLY_BASE)
					.addAttribute(Attributes.MAX_HEALTH, "1b071763-1bae-4c09-8c95-58c06629b9a3", 1.5, Operation.MULTIPLY_BASE)
					.drops(EssenceType.DOMINANCE, 9*9)));

	public static final RegistryObject<Ability> ASSASSIN = REGISTER.register("assassin", () ->
			new Ability(new Ability.Properties(0x292c36, 0x292c36, 0x53555e)
					.addTargetedSkill(5, 500, (entity, holder, target) -> {
						if(entity.doHurtTarget(target)){
							entity.teleportTo(target.getX()-0.5, target.getY()+target.getEyeHeight(), target.getZ()-0.5);
							return target.isAlive();
						}
						return false;
					}, (entity, holder, target) -> {
						double dist = target.position().distanceTo(entity.position());
						return !(dist<2)&&!(dist>48);
					})
					.addTargetedSkill(5, 250, (entity, holder, target) -> {
						int hurtDuration = target.hurtDuration;
						entity.doHurtTarget(target);
						target.hurtDuration = hurtDuration;
						return true;
					}, (entity, holder, target) -> entity.getBbWidth()*2.0F*entity.getBbWidth()*2.0F+target.getBbWidth()<=entity.distanceToSqr(target.getX(), target.getY(), target.getZ()))
					.addAttribute(Attributes.ATTACK_DAMAGE, "33a76a6a-e561-4aab-886b-9b6986bc487a", 8, Operation.ADDITION)
					.addAttribute(Attributes.MOVEMENT_SPEED, "3a8d180b-88e9-488b-a590-0713cc4872c5", 1, Operation.MULTIPLY_TOTAL)
					.addAttribute(ModAttributes.FALLING_DAMAGE_RESISTANCE.get(), "086c4438-1e28-4484-a1e4-cf3b5cede40c", 1, Operation.MULTIPLY_BASE)
					.drops(EssenceType.DEATH, 9*9)));

	public static final RegistryObject<Ability> GUNPOWDER_SWARM = REGISTER.register("gunpowder_swarm", () ->
			new Ability(new Ability.Properties(0x58ba34, 0x34d80e, 0x34d80e)
					.addTargetedSkill(10, 100, (entity, holder, target) -> {
						CreeperMissileEntity missile = new CreeperMissileEntity(entity.level);
						missile.setPos(entity.getX(), entity.getEyeY(), entity.getZ());
						missile.shootEntityToTarget(entity, target, 1);
						entity.level.addFreshEntity(missile);
						return true;
					})
					.drops(EssenceType.FIRE, 9*9)));

	public static final RegistryObject<Ability> KILLER_QUEEN = REGISTER.register("killer_queen", KillerQueenAbility::killerQueen);

	//////////////////////////////////////////////////
	//
	// No
	//
	//////////////////////////////////////////////////

	public static RegistryObject<Ability> BLACK_HOLE = REGISTER.register("black_hole", () ->
			new Ability(new Ability.Properties(0, 0x97470c, 0xff7814)
					.onUpdate((entity, holder) -> {
						List<Entity> entityList = entity.level.getEntities(entity, entity.getBoundingBox().inflate(6), e -> !(e==entity)&&e instanceof LivingEntity);
						for(Entity target : entityList){
							target.setDeltaMovement(entity.getX()-target.getX(), entity.getY()-target.getY(), entity.getZ()-target.getZ());
						}
					}).drops(EssenceType.MAGIC, 4*9)));

	// TODO to be implemented

	public static final RegistryObject<Ability> WABBAJACK = REGISTER.register("wabbajack", () ->
			new Ability(new Ability.Properties(0x2BB826, 0x2BB826)));
	public static final RegistryObject<Ability> SLYNESS = REGISTER.register("slyness", () ->
			new Ability(new Ability.Properties(0x252525, 0x252525)));
	public static final RegistryObject<Ability> ARROW_STORM = REGISTER.register("arrow_storm", () ->
			new Ability(new Ability.Properties(0x7A00D0, 0x7A00D0)));
	public static final RegistryObject<Ability> ARROW_RUSH = REGISTER.register("arrow_rush", () ->
			new Ability(new Ability.Properties(0x7A00D0, 0x7A00D0)));

	@SubscribeEvent
	public static void newRegistry(RegistryEvent.NewRegistry e){
		registry = (ForgeRegistry<Ability>)new RegistryBuilder<Ability>()
				.setType(Ability.class)
				.setName(new ResourceLocation('z'+MODID, "abilities")) // FUCK you minecraft. FUCK you forge. FUCK everything.
				.create();
	}

	private static Supplier<Ability> heart(String uuid, double amount, int drops){
		return () -> new Ability(new Ability.Properties(0xf54343, 0xa60000)
				.addAttribute(Attributes.MAX_HEALTH, uuid, amount, Operation.ADDITION)
				.drops(EssenceType.BLOOD, drops));
	}
}
