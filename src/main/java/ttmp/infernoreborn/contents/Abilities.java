package ttmp.infernoreborn.contents;

import net.minecraft.enchantment.ThornsEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MagmaCubeEntity;
import net.minecraft.entity.monster.SlimeEntity;
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
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import ttmp.infernoreborn.capability.TickingTaskHandler;
import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.contents.ability.AbilitySkill;
import ttmp.infernoreborn.contents.ability.OnAbilityEvent;
import ttmp.infernoreborn.contents.ability.cooldown.Cooldown;
import ttmp.infernoreborn.contents.entity.AnvilEntity;
import ttmp.infernoreborn.contents.entity.projectile.CreeperMissileEntity;
import ttmp.infernoreborn.contents.entity.projectile.wind.AbstractWindEntity;
import ttmp.infernoreborn.contents.entity.projectile.wind.DamagingWindEntity;
import ttmp.infernoreborn.contents.entity.projectile.wind.EffectWindEntity;
import ttmp.infernoreborn.network.ModNet;
import ttmp.infernoreborn.network.ParticleMsg;
import ttmp.infernoreborn.util.EssenceType;
import ttmp.infernoreborn.util.LivingOnlyEntityDamageSource;
import ttmp.infernoreborn.util.LivingUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static ttmp.infernoreborn.InfernoReborn.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Bus.MOD)
public final class Abilities{
	private Abilities(){}

	private static ForgeRegistry<Ability> registry;

	public static ForgeRegistry<Ability> getRegistry(){
		return registry;
	}

	public static final DeferredRegister<Ability> REGISTER = DeferredRegister.create(Ability.class, MODID);

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

	public static final RegistryObject<Ability> WOOD_SKIN = REGISTER.register("wood_skin", () ->
			new Ability(new Ability.Properties(0x917142, 0x5f4a2b, 0xc29d62)
					.addAttribute(Attributes.ARMOR, "7c68dd04-64b6-4509-8ded-e3507560e8f0", 2, Operation.ADDITION)
					.drops(EssenceType.METAL, 2)));

	public static final RegistryObject<Ability> ROCK_SKIN = REGISTER.register("rock_skin", () ->
			new Ability(new Ability.Properties(0x8a8a8a, 0x525252)
					.addAttribute(Attributes.ARMOR, "a2ab4bdf-760c-464e-ad56-d21bd367ffb3", 4, Operation.ADDITION)
					.drops(EssenceType.METAL, 4)));

	public static final RegistryObject<Ability> IRON_SKIN = REGISTER.register("iron_skin", () ->
			new Ability(new Ability.Properties(0xdbdbdb, 0x686868, 0xeeeeee)
					.addAttribute(Attributes.ARMOR, "1fc727a2-4aed-4578-9c0e-47dce56f6785", 4, Operation.ADDITION)
					.drops(EssenceType.METAL, 9)));

	public static final RegistryObject<Ability> DIAMOND_SKIN = REGISTER.register("diamond_skin", () ->
			new Ability(new Ability.Properties(0x4deeec, 0x239180, 0xa1ecf3)
					.addAttribute(Attributes.ARMOR, "6319cb70-cb20-40ca-928b-c6d52ff30598", 10, Operation.ADDITION)
					.addAttribute(Attributes.ARMOR_TOUGHNESS, "1fc727a2-4aed-4578-9c0e-47dce56f6785", 4, Operation.ADDITION)
					.addAttribute(ModAttributes.DAMAGE_RESISTANCE.get(), "1e9c7ee4-7419-4fd3-ab09-81b8dfa8c763", .1, Operation.MULTIPLY_BASE)
					.drops(EssenceType.METAL, 2*9)));

	public static final RegistryObject<Ability> NETHERITE_SKIN = REGISTER.register("netherite_skin", () ->
			new Ability(new Ability.Properties(0x4f3c3e, 0x4a2940, 0xcdbccd)
					.addAttribute(Attributes.ARMOR, "22ab3354-54fd-452f-8505-fce3eb7d2645", 14, Operation.ADDITION)
					.addAttribute(Attributes.ARMOR_TOUGHNESS, "4f256a3f-175b-4b2a-9eda-0582dcc9c6d8", 10, Operation.ADDITION)
					.addAttribute(ModAttributes.DAMAGE_RESISTANCE.get(), "85171c75-6976-4ae4-b1b7-5c0fbd2acbfd", .2, Operation.MULTIPLY_BASE)
					.drops(EssenceType.METAL, 4*9)));

	public static final RegistryObject<Ability> MUD_SKIN = REGISTER.register("mud_skin", () ->
			new Ability(new Ability.Properties(0x754b3f, 0x3f231b)
					.addAttribute(Attributes.ARMOR, "fcaf52d5-fa7f-487b-af62-a7f59369e20b", 1, Operation.ADDITION)
					.onHurt(skin((event, hit) -> LivingUtils.addStackEffect(hit, Effects.DIG_SLOWDOWN, 40, 0, 1, 5, true, true)))
					.drops(EssenceType.METAL, 5)
					.drops(EssenceType.EARTH, 5)));

	public static final RegistryObject<Ability> FROZEN_SKIN = REGISTER.register("frozen_skin", () ->
			new Ability(new Ability.Properties(0x2979bd, 0x2979bd)
					.addAttribute(Attributes.ARMOR, "66fc401d-84e0-45f2-9abf-9a0fb7dee78a", 1, Operation.ADDITION)
					.onHurt(skin((event, hit) -> LivingUtils.addStackEffect(hit, Effects.MOVEMENT_SLOWDOWN, 40, 0, 1, 3, true, true)))
					.drops(EssenceType.METAL, 5)
					.drops(EssenceType.WATER, 5)));

	public static final RegistryObject<Ability> WOOLLY_SKIN = REGISTER.register("woolly_skin", () ->
			new Ability(new Ability.Properties(0xe0c1ad, 0x694d40)
					.addAttribute(Attributes.ARMOR, "8d4b6252-301f-40db-93dc-96e78f199a53", 1, Operation.ADDITION)
					.onHurt(skin((event, hit) -> LivingUtils.addStackEffect(hit, Effects.WEAKNESS, 20, 0, 1, 5, true, true)))
					.drops(EssenceType.METAL, 5)
					.drops(EssenceType.AIR, 5)));

	public static final RegistryObject<Ability> FUZZY_SKIN = REGISTER.register("fuzzy_skin", () ->
			new Ability(new Ability.Properties(0xf2f5cd, 0xeaaeee)
					.addAttribute(Attributes.ARMOR, "5fe9045c-0976-4faa-b5a7-d5b7c407723f", 1, Operation.ADDITION)
					.onHurt(skin((event, hit) -> hit.addEffect(new EffectInstance(Effects.CONFUSION, 200))))
					.drops(EssenceType.METAL, 5)
					.drops(EssenceType.MAGIC, 5)));

	public static final RegistryObject<Ability> THORN_SKIN = REGISTER.register("thorn_skin", () ->
			new Ability(new Ability.Properties(0xC8C8C8, 0xC8C8C8)
					.onHurt(skin((event, hit) -> {
						if(event.getSource() instanceof EntityDamageSource&&((EntityDamageSource)event.getSource()).isThorns()) return;
						hit.hurt(DamageSource.thorns(event.getEntityLiving()), ThornsEnchantment.getDamage(2, event.getEntityLiving().getRandom()));
					}))
					.addAttribute(Attributes.ARMOR, "733dfe0f-a807-4812-b49b-3353e732fb03", 1, Operation.ADDITION)
					.drops(EssenceType.METAL, 5)
					.drops(EssenceType.EARTH, 3)
					.drops(EssenceType.MAGIC, 2)));

	public static final RegistryObject<Ability> MELEE_VETERAN = REGISTER.register("melee_veteran", () ->
			new Ability(new Ability.Properties(0xe0c1ad, 0x694d40)
					.onHit((entity, holder, event) -> {
						if(entity==event.getSource().getDirectEntity()){
							event.setAmount(event.getAmount()*(1.1f));
						}
					})
					.drops(EssenceType.FIRE, 5)
					.drops(EssenceType.DOMINANCE, 2)));

	public static final RegistryObject<Ability> RANGED_VETERAN = REGISTER.register("ranged_veteran", () ->
			new Ability(new Ability.Properties(0xe0c1ad, 0x694d40)
					.onHit((entity, holder, event) -> {
						if(entity!=event.getSource().getDirectEntity()&&event.getSource().isProjectile()&&!event.getSource().isMagic()){
							event.setAmount(event.getAmount()*(1.1f));
						}
					})
					.drops(EssenceType.AIR, 5)
					.drops(EssenceType.DOMINANCE, 2)));

	public static final RegistryObject<Ability> MAGIC_VETERAN = REGISTER.register("magic_veteran", () ->
			new Ability(new Ability.Properties(0xe0c1ad, 0x694d40)
					.onHit((entity, holder, event) -> {
						if(event.getSource().isMagic()){
							event.setAmount(event.getAmount()*(1.1f));
						}
					})
					.drops(EssenceType.MAGIC, 5)
					.drops(EssenceType.DOMINANCE, 2)));

	public static final RegistryObject<Ability> WINDBLAST_1 = REGISTER.register("windblast_1", () ->
			new Ability(new Ability.Properties(0xA1CEE3, 0xA1CEE3)
					.addTargetedSkill(4, 120, wind((world) -> new DamagingWindEntity(world).setDamage(4), 1, 1))
					.drops(EssenceType.AIR, 3)));

	public static final RegistryObject<Ability> WINDBLAST_2 = REGISTER.register("windblast_2", () ->
			new Ability(new Ability.Properties(0xA1CEE3, 0xA1CEE3)
					.addTargetedSkill(8, 180, wind((world) -> new DamagingWindEntity(world).setDamage(6), 1, 1))
					.drops(EssenceType.AIR, 6)));

	public static final RegistryObject<Ability> WINDBLAST_3 = REGISTER.register("windblast_3", () ->
			new Ability(new Ability.Properties(0xA1CEE3, 0xA1CEE3)
					.addTargetedSkill(12, 240, wind((world) -> new DamagingWindEntity(world).setDamage(8), 1, 1))
					.drops(EssenceType.AIR, 9)));

	public static final RegistryObject<Ability> BULLETPROOF = REGISTER.register("bulletproof", () ->
			new Ability(new Ability.Properties(0xC8C8C8, 0xC8C8C8)
					.addAttribute(ModAttributes.RANGED_DAMAGE_RESISTANCE.get(), "fa9b6063-66b7-4bbd-a4d8-1d35a088fa92", .8, Operation.MULTIPLY_BASE)
					.drops(EssenceType.METAL, 5)));

	public static final RegistryObject<Ability> VAMPIRE = REGISTER.register("vampire", () ->
			new Ability(new Ability.Properties(0x800000, 0x800000)
					.onHit((entity, holder, event) -> {
						float amount = event.getAmount();
						if(entity!=event.getSource().getDirectEntity()) amount /= 2;
						entity.heal(amount);
					})
					.drops(EssenceType.BLOOD, 5)
					.drops(EssenceType.DEATH, 5)));

	public static final RegistryObject<Ability> CROWD_CONTROL = REGISTER.register("crowd_control", () ->
			new Ability(new Ability.Properties(0x000000, 0x0000000)
					.onHit((entity, holder, event) -> {
						event.getEntityLiving().addEffect(new EffectInstance(Effects.LEVITATION, 100));
						LivingUtils.addStackEffect(event.getEntityLiving(), Effects.DIG_SLOWDOWN, 100, 0, 1, 5, true, true);
					})
					.drops(EssenceType.DOMINANCE, 5)));

	public static final RegistryObject<Ability> SURVIVAL_EXPERT = REGISTER.register("survival_expert", () ->
			new Ability(new Ability.Properties(0x2BB826, 0x00000)
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
					.drops(EssenceType.EARTH, 5)
					.drops(EssenceType.AIR, 5)));

	public static final RegistryObject<Ability> DESTINY_BOND = REGISTER.register("destiny_bond", () ->
			new Ability(new Ability.Properties(0x0000, 0x0000)
					.onDeath((entity, holder, event) -> {
						Entity target = event.getSource().getEntity();
						if(!(target instanceof LivingEntity)) return;
						target.hurt(DamageSource.MAGIC, (float)Math.pow(entity.position().distanceTo(target.position()), 1.5));
					})
					.drops(EssenceType.MAGIC, 5)
					.drops(EssenceType.DEATH, 5)));

	public static final RegistryObject<Ability> FOCUS = REGISTER.register("focus", () ->
			new Ability(new Ability.Properties(0x00, 0x00)
					.onHurt((entity, holder, event) -> {
						if(event.getSource().getEntity() instanceof LivingEntity){
							LivingEntity target = (LivingEntity)event.getSource().getEntity();
							if(target!=null&&target==event.getSource().getDirectEntity()) event.setAmount(event.getAmount()*.5f);
						}
					})
					.drops(EssenceType.BLOOD, 3)
					.drops(EssenceType.WATER, 3)
					.drops(EssenceType.FIRE, 3)));

	public static final RegistryObject<Ability> GUTS = REGISTER.register("guts", () ->
			new Ability(new Ability.Properties(0xB24100, 0xB24100)
					.onHurt((entity, holder, event) -> LivingUtils.addStackEffect(entity, Effects.DAMAGE_RESISTANCE, 60, 0, 1, 3))
					.drops(EssenceType.METAL, 4*9)));

	public static final RegistryObject<Ability> MAGMA_SKIN = REGISTER.register("magma_skin", () ->
			new Ability(new Ability.Properties(0x340000, 0x340000)
					.onUpdate((entity, holder) -> entity.setSecondsOnFire(1))
					.onAttacked((entity, holder, event) -> {
						if(event.getSource().isFire()) event.setCanceled(true);
					})
					.onHit(skin((event, hit) -> {
						if(!hit.isInWaterOrRain()) hit.setSecondsOnFire(8);
					}))
					.addAttribute(ModAttributes.DAMAGE_RESISTANCE.get(), "55a108b6-55ff-4b25-a416-afd9f806de69", .2, Operation.MULTIPLY_BASE)
					.drops(EssenceType.METAL, 9)
					.drops(EssenceType.FIRE, 9)));

	public static final RegistryObject<Ability> THE_BRAIN = REGISTER.register("the_brain", () ->
			new Ability(new Ability.Properties(0x00, 0x00)
					.addSkill(10, 300, (entity, holder) -> {
						if(entity.getLastHurtByMob()==null) return false;
						for(Entity e : entity.level.getEntities(entity, entity.getBoundingBox().inflate(16, 8, 16)))
							if(e instanceof LivingEntity) ((LivingEntity)e).setLastHurtByMob(entity.getLastHurtByMob());
						return true;
					}, (entity, holder) -> entity.getLastHurtByMob()!=null)
					.addAttribute(Attributes.MAX_HEALTH, "2d145dfc-dda4-4fc0-aa35-6666eae0a776", 0.25, Operation.ADDITION)
					.drops(EssenceType.DOMINANCE, 2*9)
					.drops(EssenceType.DEATH, 9)));

	public static final RegistryObject<Ability> SENTRY = REGISTER.register("sentry", () ->
			new Ability(new Ability.Properties(0x8000, 0x80000)
					.onUpdate((entity, holder) -> holder.cooldown().decreaseAll(4))
					.addAttribute(Attributes.MOVEMENT_SPEED, "41669826-6fde-4dc6-b67f-28a28a1f2dbb", -0.9, Operation.MULTIPLY_BASE)
					.drops(EssenceType.EARTH, 5)));

	public static final RegistryObject<Ability> HEALTH_KIT = REGISTER.register("health_kit", () ->
			new Ability(new Ability.Properties(0x00, 0x00)
					.addSkill(10, 500, (entity, holder) -> {
						if(entity.getHealth()+10>=entity.getMaxHealth()) return false;
						entity.heal(10);
						return true;
					}, (entity, holder) -> entity.getHealth()+10<entity.getMaxHealth()).addAttribute(Attributes.MAX_HEALTH, "25261e88-72c5-49b9-8680-8253f3c73a30", 10, Operation.ADDITION)
					.drops(EssenceType.BLOOD, 3*9)
					.drops(EssenceType.WATER, 9)));

	public static final RegistryObject<Ability> EVIOLITE_CHANSEY = REGISTER.register("eviolite_chansey", () ->
			new Ability(new Ability.Properties(0xFFCFF, 0xFFCFF)
					.addAttribute(Attributes.ATTACK_DAMAGE, "ba20b5e3-e189-444f-9233-c710d7ac810e", -0.8, Operation.MULTIPLY_TOTAL)
					.addAttribute(Attributes.MAX_HEALTH, "f72d69be-39ff-4dbc-b938-ff0740ad528c", 1.5, Operation.MULTIPLY_TOTAL)
					.addAttribute(ModAttributes.DAMAGE_RESISTANCE.get(), "41bd35ff-0f02-434c-81a2-be6791739e00", .4, Operation.MULTIPLY_BASE)
					.drops(EssenceType.METAL, 2*9)
					.drops(EssenceType.BLOOD, 9)
					.drops(EssenceType.DEATH, 9)));

	public static final RegistryObject<Ability> TOUGHNESS = REGISTER.register("toughness", () ->
			new Ability(new Ability.Properties(0x3F3F3F, 0x003FCF)
					.addAttribute(Attributes.MOVEMENT_SPEED, "ec3aa988-af1f-4ae9-8cf6-b1d5e62addaa", -0.2, Operation.MULTIPLY_TOTAL)
					.addAttribute(ModAttributes.DAMAGE_RESISTANCE.get(), "9c03cdb9-bb2d-4d03-8766-26632b0966df", .2, Operation.MULTIPLY_BASE)
					.drops(EssenceType.EARTH, 9)
					.drops(EssenceType.WATER, 9)));

	public static final RegistryObject<Ability> SWIFTNESS = REGISTER.register("swiftness", () ->
			new Ability(new Ability.Properties(0x003FCF, 0x3F3F3F)
					.addAttribute(Attributes.MOVEMENT_SPEED, "0759550c-3e02-4dee-89b8-2d490347da5e", 0.3, Operation.MULTIPLY_TOTAL)
					.addAttribute(ModAttributes.DAMAGE_RESISTANCE.get(), "3db07150-8675-4c9e-acf1-c35100cf76b8", -.1, Operation.MULTIPLY_BASE)
					.drops(EssenceType.AIR, 9)
					.drops(EssenceType.FIRE, 9)));

	public static final RegistryObject<Ability> TELEKINESIS = REGISTER.register("telekinesis", () ->
			new Ability(new Ability.Properties(0xB71100, 0xB71100)
					.addTargetedSkill(5, 250, (entity, holder, target) -> {
						double relX = (entity.getX()-target.getX());
						double relZ = (entity.getZ()-target.getZ());
						double dist = Math.sqrt(relX*relX+relZ*relZ);
						double pow = (1.5+(0.6*target.distanceTo(entity)))/7.0;

						target.setDeltaMovement(relX/dist*pow, 1.3/dist*pow, relZ/dist*pow);
						target.fallDistance -= 1.3/dist*pow;
						target.hurt(DamageSource.MAGIC, 2.5f);
						return true;
					})
					.drops(EssenceType.MAGIC, 2*9)
					.drops(EssenceType.DEATH, 2*9)));

	public static final RegistryObject<Ability> AETHER_WALKER = REGISTER.register("aether_walker", () ->
			new Ability(new Ability.Properties(0xC0E4FF, 0xC0E4FF)
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
			new Ability(new Ability.Properties(0xFFFF00, 0xFFFF00)
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
						if(event.getSource().isFire()||event.getSource()==DamageSource.LIGHTNING_BOLT) event.setCanceled(true);
					})
					.drops(EssenceType.MAGIC, 2*9)
					.drops(EssenceType.FIRE, 9)
					.drops(EssenceType.AIR, 9)));

	public static final RegistryObject<Ability> THE_RED = REGISTER.register("the_red", () ->
			new Ability(new Ability.Properties(0xC80000, 0xC80000)
					.addAttribute(Attributes.MOVEMENT_SPEED, "33a6142f-5b04-490c-85ce-11cc79510f9a", 2, Operation.MULTIPLY_BASE)
					.addAttribute(ModAttributes.FALLING_DAMAGE_RESISTANCE.get(), "5cb842c2-beb3-4da0-b4c6-aa0323fe292a", 1, Operation.MULTIPLY_BASE)
					.drops(EssenceType.BLOOD, 2*9)
					.drops(EssenceType.FIRE, 2*9)));

	public static final RegistryObject<Ability> EMPERORS_AURA = REGISTER.register("emperors_aura", () ->
			new Ability(new Ability.Properties(0xDCB600, 0xDCB600)
					.onHit((entity, holder, event) -> {
						for(Entity e : entity.level.getEntities(entity, entity.getBoundingBox().inflate(7.5)))
							if(e.isAlive()&&e instanceof LivingEntity&&!(e instanceof PlayerEntity))
								LivingUtils.addStackEffect((LivingEntity)e, Effects.DAMAGE_BOOST, 140, 0, 1, 64);
					})
					.addAttribute(Attributes.ATTACK_DAMAGE, "4b220817-9f85-432f-9ce8-ac9d282b5d38", 2, Operation.MULTIPLY_BASE)
					.addAttribute(Attributes.MAX_HEALTH, "1b071763-1bae-4c09-8c95-58c06629b9a3", 1.5, Operation.MULTIPLY_BASE)
					.drops(EssenceType.DOMINANCE, 9*9)));

	public static final RegistryObject<Ability> POISONED_MIND = REGISTER.register("poisoned_mind", () ->
			new Ability(new Ability.Properties(0x00, 0x00)
					.onDeath((entity, holder, event) -> {
						Potion[] potionArray = {Potions.SLOWNESS, Potions.STRONG_SLOWNESS, Potions.LONG_SLOWNESS, Potions.HARMING, Potions.STRONG_HARMING, Potions.POISON, Potions.LONG_POISON, Potions.STRONG_POISON, Potions.WEAKNESS, Potions.LONG_WEAKNESS};
						PotionEntity potionEntity = new PotionEntity(entity.level, entity);
						potionEntity.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potionArray[entity.getRandom().nextInt(potionArray.length)]));
						float xRot = (float)(entity.getRandom().nextDouble()*2*Math.PI);
						potionEntity.setDeltaMovement(new Vector3d(MathHelper.sin(xRot), entity.getRandom().nextDouble()*0.5+.5, MathHelper.cos(xRot)).normalize().scale(.5));
						entity.level.addFreshEntity(potionEntity);
					}).drops(EssenceType.MAGIC, 9)));

	public static final RegistryObject<Ability> DAE_KAE_MOB = REGISTER.register("dae_kae_mob", () ->
			new Ability(new Ability.Properties(0xED1C27, 0x004EA1, 0x00A0E2)
					.addTargetedSkill(10, 60, (entity, holder, target) -> {
						target.level.addFreshEntity(new AnvilEntity(target.level, target.getX(), target.getY()+20, target.getZ()));
						return true;
					})
					.onAttacked((entity, holder, event) -> {
						if(event.getSource()==DamageSource.ANVIL) event.setCanceled(true);
					}).drops(EssenceType.METAL, 3)));

	public static final RegistryObject<Ability> BATTLE_MOMENTUM = REGISTER.register("battle_momentum", () ->
			new Ability(new Ability.Properties(0x6041F8, 0x6041F8)
					.onAttacked((entity, holder, event) -> holder.cooldown().decreaseAll(10))
					.onHit((entity, holder, event) -> holder.cooldown().decreaseAll(20))
					.drops(EssenceType.DOMINANCE, 9)));

	public static final RegistryObject<Ability> SLIMEBLOOD = REGISTER.register("slimeblood", () ->
			new Ability(new Ability.Properties(0x7DCC6A, 0x7DCC6A)
					.onAttacked((entity, holder, event) -> {
						SlimeEntity slime = new SlimeEntity(EntityType.SLIME, entity.level);
						slime.setPos(entity.getX(), entity.getY(), entity.getZ());
						entity.level.addFreshEntity(slime);
					}).addAttribute(ModAttributes.FALLING_DAMAGE_RESISTANCE.get(), "6bab5413-70eb-4aa8-b709-dec6f1bc16b8", (double)1/3, Operation.MULTIPLY_BASE)));

	public static final RegistryObject<Ability> BLAZE_WAVE = REGISTER.register("blaze_wave", () ->
			new Ability(new Ability.Properties(0xFF680C, 0xFF680C)
					.addTargetedSkill(30, 180, wind((world) -> new DamagingWindEntity(world).doSetFire(true), 1, 1))));

	public static final RegistryObject<Ability> FREEZE_WAVE = REGISTER.register("freeze_wave", () ->
			new Ability(new Ability.Properties(0x92B9FA, 0x92B9FA)
					.addTargetedSkill(30, 120, wind((world) -> new EffectWindEntity(world).addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 300)), 1, 1))));

	public static final RegistryObject<Ability> ENVENOM = REGISTER.register("envenom", () ->
			new Ability(new Ability.Properties(0x4E9331, 0x4E9331)
					.addTargetedSkill(30, 160, wind((world) -> new EffectWindEntity(world).addEffect(new EffectInstance(Effects.POISON, 100, 2)), 1, 1))));

	public static final RegistryObject<Ability> ASSASSINATE = REGISTER.register("assassinate", () ->
			new Ability(new Ability.Properties(0x332B40, 0x332B40)
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
					.addAttribute(ModAttributes.FALLING_DAMAGE_RESISTANCE.get(), "086c4438-1e28-4484-a1e4-cf3b5cede40c", 1, Operation.MULTIPLY_BASE)));

	public static final RegistryObject<Ability> EVIL_TRICK = REGISTER.register("evil_trick", () ->
			new Ability(new Ability.Properties(0x191919, 0x191919)
					.addTargetedSkill(5, 50, (entity, holder, target) -> {
						if(!(entity.getHealth()/entity.getMaxHealth()<0.4)) return false;
						boolean succeed = false;
						for(Entity e : entity.level.getEntities(entity, entity.getBoundingBox().inflate(6))){
							if(e.hurt(DamageSource.GENERIC, 1)){
								succeed = true;
								entity.heal(1);
							}
						}
						return succeed;
					}, (entity, holder, target) -> entity.getHealth()/entity.getMaxHealth()<0.4)));
	public static final RegistryObject<Ability> WABBAJACK = REGISTER.register("wabbajack", () ->
			new Ability(new Ability.Properties(0x2BB826, 0x2BB826)));
	public static final RegistryObject<Ability> SLYNESS = REGISTER.register("slyness", () ->
			new Ability(new Ability.Properties(0x252525, 0x252525)));
	public static final RegistryObject<Ability> ELECTRIC_SKIN = REGISTER.register("electric_skin", () ->
			new Ability(new Ability.Properties(0xFFFF00, 0xFFFF00)
					.withCooldownTicket((ticket, properties) -> properties.onAttacked((entity, holder, event) -> {
						Cooldown cooldown = holder.cooldown();
						if(event.getSource()==DamageSource.LIGHTNING_BOLT){
							event.setCanceled(true);
							cooldown.set(ticket, 100);
						}else if(event.getSource().getEntity() instanceof LivingEntity){
							int dmg = (int)(100-cooldown.get(ticket))/10-2;
							if(dmg>0&&event.getSource().getEntity().hurt(DamageSource.LIGHTNING_BOLT, dmg)){
								cooldown.set(ticket, 100);
								cooldown.setGlobalDelay(10);
							}
						}
					}))));
	public static final RegistryObject<Ability> RECALL = REGISTER.register("recall", () ->
			new Ability(new Ability.Properties(0x64FFFF, 0x64FFFF)));
	public static final RegistryObject<Ability> ZOMBIE_NECROMANCY = REGISTER.register("zombie_necromancy", () ->
			new Ability(new Ability.Properties(0x466D36, 0x466D36)));
	public static final RegistryObject<Ability> SKELETON_NECROMANCY = REGISTER.register("skeleton_necromancy", () ->
			new Ability(new Ability.Properties(0x787878, 0x787878)));
	public static final RegistryObject<Ability> ARROW_STORM = REGISTER.register("arrow_storm", () ->
			new Ability(new Ability.Properties(0x7A00D0, 0x7A00D0)));
	public static final RegistryObject<Ability> ARROW_RUSH = REGISTER.register("arrow_rush", () ->
			new Ability(new Ability.Properties(0x7A00D0, 0x7A00D0)));
	public static final RegistryObject<Ability> CONDITIONAL_REFLEX = REGISTER.register("conditional_reflex", () ->
			new Ability(new Ability.Properties(0x0, 0x0)
					.withCooldownTicket((ticket, properties) -> properties.onAttacked((entity, holder, event) -> {
						LivingEntity target = LivingUtils.getTarget(entity);
						if(target!=null){
							holder.cooldown().setGlobalDelay(5);
							holder.cooldown().set(ticket, 5);
							entity.doHurtTarget(target);
						}
					})).addAttribute(Attributes.ATTACK_DAMAGE, "9ff4653e-cd0e-48d1-b2ac-1627ae0f5700", 3, Operation.ADDITION)));
	public static final RegistryObject<Ability> MAGMABLOOD = REGISTER.register("magmablood", () ->
			new Ability(new Ability.Properties(0x400000, 0x400000)
					.onAttacked((entity, holder, event) -> {
						World world = entity.level;
						MagmaCubeEntity magmaCube = new MagmaCubeEntity(EntityType.MAGMA_CUBE, world);
						magmaCube.setPos(entity.getX(), entity.getY(), entity.getZ());
						magmaCube.setTarget((LivingEntity)event.getSource().getEntity());
						world.addFreshEntity(magmaCube);
					})));
	public static final RegistryObject<Ability> GUNPOWDER_SWARM = REGISTER.register("gunpowder_swarm", () ->
			new Ability(new Ability.Properties(0x00C800, 0x00C800)
					.addTargetedSkill(10, 100, (entity, holder, target) -> {
						CreeperMissileEntity missile = new CreeperMissileEntity(ModEntities.CREEPER_MISSILE_ENTITY.get(), entity.level);
						missile.setPos(entity.getX(), entity.getEyeY(), entity.getZ());
						missile.shootEntityToTarget(entity, target, 1);
						entity.level.addFreshEntity(missile);
						return true;
					})));
	public static final RegistryObject<Ability> KILLER_QUEEN = REGISTER.register("killer_queen", () ->
			new Ability(new Ability.Properties(0xE3AADD, 0xE3AADD)
					.onUpdate((entity, holder) -> {
						LivingEntity target = LivingUtils.getTarget(entity);
						if(target==null||!target.isAlive()) return;
						EffectInstance effect = target.getEffect(ModEffects.KILLER_QUEEN.get());
						if(effect==null) return;
						int c = effect.getAmplifier()+1;
						double distance = target.distanceTo(entity);
						if(distance>6&&(distance>15||c>=target.getHealth())){
							entity.hurt(LivingUtils.killerQueenDamage(entity), c);
							entity.level.explode(entity, new LivingOnlyEntityDamageSource("explosion.player", null, entity).setExplosion(),
									null, target.getX(), target.getY(), target.getZ(), 1+c/2f, false, Explosion.Mode.NONE);
						}
					}).onHit((entity, holder, event) -> {
						Entity directEntity = event.getSource().getDirectEntity();
						if(entity==directEntity){
							LivingUtils.addStackEffect(event.getEntityLiving(), ModEffects.KILLER_QUEEN.get(), 600, 0, 1, 127);
						}else if(directEntity!=null&&event.getSource().isProjectile()){
							entity.level.explode(entity, new LivingOnlyEntityDamageSource("explosion.player", directEntity, entity).setExplosion(),
									null, directEntity.getX(), directEntity.getY(), directEntity.getZ(), 1.5f, false, Explosion.Mode.NONE);
							directEntity.kill();
						}
					}).onAttacked((entity, holder, event) -> {
						if(event.getSource().isExplosion()) event.setCanceled(true);
					}).addAttribute(ModAttributes.DAMAGE_RESISTANCE.get(), "ec33a2c7-5757-413a-9a79-51d507d068aa", 0.15, Operation.MULTIPLY_BASE)));
	public static final RegistryObject<Ability> DIABOLO = REGISTER.register("diabolo", () ->
			new Ability(new Ability.Properties(0x4B0000, 0x4B0000)
					.addSkill(10, 600, (entity, holder) -> {
						List<Entity> entityList = entity.level.getEntities(entity, entity.getBoundingBox().inflate(6), e -> {
							if(e instanceof LivingEntity)
								return !(e instanceof MobEntity);
							return false;
						});
						for(Entity e : entityList)
							((LivingEntity)e).addEffect(new EffectInstance(ModEffects.FEAR.get(), 300, 2-(int)e.distanceToSqr(entity)/6));

						return entity.addEffect(new EffectInstance(ModEffects.DIABOLO.get(), 600));
					})));
	public static final RegistryObject<Ability> TOUCH_OF_MIDAS = REGISTER.register("touch_of_midas", () ->
			new Ability(new Ability.Properties(0xFDF55F, 0xDD9515)
					.onHit((entity, holder, event) -> {
						if(entity!=event.getSource().getDirectEntity()) return;
						LivingEntity target = event.getEntityLiving();
						LivingUtils.addStackEffect(target, ModEffects.HAND_OF_MIDAS.get(), 100, 0, 1, 127);
						EffectInstance effect = target.getEffect(ModEffects.HAND_OF_MIDAS.get());
						if(effect!=null&&target.getHealth()<=effect.getAmplifier()+1){
							if(target.hurt(LivingUtils.midasDamage(entity), Float.MAX_VALUE)){
								target.level.addFreshEntity(new ItemEntity(target.level,
										target.getRandomX(1),
										target.getRandomY(),
										target.getRandomZ(1),
										new ItemStack(ModItems.GOLDEN_SKULL.get()).setHoverName(target.getName())));
							}
						}
					})));
	public static RegistryObject<Ability> BLACK_HOLE = REGISTER.register("black_hole", () ->
			new Ability(new Ability.Properties(0x00, 0x0)
					.onUpdate((entity, holder) -> {
						List<Entity> entityList = entity.level.getEntities(entity, entity.getBoundingBox().inflate(6), e -> !(e==entity)&&e instanceof LivingEntity);
						for(Entity target : entityList){
							target.setDeltaMovement(entity.getX()-target.getX(), entity.getY()-target.getY(), entity.getZ()-target.getZ());
						}
					})));

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
	private static OnAbilityEvent<LivingHurtEvent> skin(SkinEffect effect){
		return (entity, holder, event) -> {
			Entity hitEntity = event.getSource().getDirectEntity();
			if(hitEntity instanceof LivingEntity&&hitEntity.isAlive()&&!event.getSource().isProjectile()){
				effect.apply(event, (LivingEntity)hitEntity);
			}
		};
	}
	private static AbilitySkill.TargetedSkillAction wind(Function<World, AbstractWindEntity> constructor, float velocity, int color){
		return (entity, holder, target) -> {
			AbstractWindEntity wind = constructor.apply(entity.level);
			wind.setOwner(entity);
			wind.setColor(color);
			wind.shootEntityToTarget(entity, target, velocity);
			entity.level.addFreshEntity(wind);
			return true;
		};
	}

	@FunctionalInterface
	private interface SkinEffect{
		void apply(LivingHurtEvent event, LivingEntity hit);
	}
}
