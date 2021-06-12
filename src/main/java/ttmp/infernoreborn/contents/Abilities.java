package ttmp.infernoreborn.contents;

import net.minecraft.enchantment.ThornsEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.ability.Ability;
import ttmp.infernoreborn.util.AbilityUtils;

import java.util.UUID;
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

	public static final RegistryObject<Ability> HEART = REGISTER.register("heart", heart("55924d9f-ac7a-4472-bfea-bc4e305a363f", 1));
	public static final RegistryObject<Ability> HEART2 = REGISTER.register("heart2", heart("4cff8f05-0d74-4f19-89d5-577eef01d279", 3));
	public static final RegistryObject<Ability> HEART3 = REGISTER.register("heart3", heart("07e95560-304a-4564-88f8-96b612e72bdf", 5));
	public static final RegistryObject<Ability> HEART4 = REGISTER.register("heart4", heart("66fb3897-dc1f-4968-b13e-ef34c3ea0f87", 10));
	public static final RegistryObject<Ability> HEART5 = REGISTER.register("heart5", heart("7976d7af-2ff2-423f-b9f8-b56eb36f507a", 20));
	public static final RegistryObject<Ability> HEART6 = REGISTER.register("heart6", heart("d2532167-e291-497e-8351-360a423b48e4", 30));
	public static final RegistryObject<Ability> HEART7 = REGISTER.register("heart7", heart("36e4c564-5d50-4848-a507-5df6c0ec3814", 40));
	public static final RegistryObject<Ability> HEART8 = REGISTER.register("heart8", heart("d10754b8-0ce5-4d5c-a82e-3ef40b121c4f", 50));
	public static final RegistryObject<Ability> HEART9 = REGISTER.register("heart9", heart("ec37932d-85e5-4846-8384-5849cbd94423", 100));
	public static final RegistryObject<Ability> HEART10 = REGISTER.register("heart10", heart("41099395-0e3c-4bf7-ab85-867b1ea87132", 200));

	public static final RegistryObject<Ability> WOOD_SKIN = REGISTER.register("wood_skin", () ->
			new Ability(new Ability.Properties(0x917142, 0x5f4a2b, 0xc29d62)
					.addAttribute(Attributes.ARMOR, UUID.fromString("7c68dd04-64b6-4509-8ded-e3507560e8f0"), 2, Operation.ADDITION)));
	public static final RegistryObject<Ability> ROCK_SKIN = REGISTER.register("rock_skin", () ->
			new Ability(new Ability.Properties(0x8a8a8a, 0x525252)
					.addAttribute(Attributes.ARMOR, UUID.fromString("a2ab4bdf-760c-464e-ad56-d21bd367ffb3"), 4, Operation.ADDITION)));
	public static final RegistryObject<Ability> IRON_SKIN = REGISTER.register("iron_skin", () ->
			new Ability(new Ability.Properties(0xdbdbdb, 0x686868, 0xeeeeee)
					.addAttribute(Attributes.ARMOR, UUID.fromString("1fc727a2-4aed-4578-9c0e-47dce56f6785"), 4, Operation.ADDITION)));
	public static final RegistryObject<Ability> DIAMOND_SKIN = REGISTER.register("diamond_skin", () ->
			new Ability(new Ability.Properties(0x4deeec, 0x239180, 0xa1ecf3)
					.addAttribute(Attributes.ARMOR, UUID.fromString("6319cb70-cb20-40ca-928b-c6d52ff30598"), 10, Operation.ADDITION)
					.addAttribute(Attributes.ARMOR_TOUGHNESS, UUID.fromString("1fc727a2-4aed-4578-9c0e-47dce56f6785"), 4, Operation.ADDITION)
					.addAttribute(ModAttributes.DAMAGE_RESISTANCE.get(), UUID.fromString("1e9c7ee4-7419-4fd3-ab09-81b8dfa8c763"), 0.1, Operation.ADDITION)));
	public static final RegistryObject<Ability> NETHERITE_SKIN = REGISTER.register("netherite_skin", () ->
			new Ability(new Ability.Properties(0x4f3c3e, 0x4a2940, 0xcdbccd)
					.addAttribute(Attributes.ARMOR, UUID.fromString("22ab3354-54fd-452f-8505-fce3eb7d2645"), 14, Operation.ADDITION)
					.addAttribute(Attributes.ARMOR_TOUGHNESS, UUID.fromString("4f256a3f-175b-4b2a-9eda-0582dcc9c6d8"), 10, Operation.ADDITION)
					.addAttribute(ModAttributes.DAMAGE_RESISTANCE.get(), UUID.fromString("85171c75-6976-4ae4-b1b7-5c0fbd2acbfd"), 0.2, Operation.ADDITION)));

	public static final RegistryObject<Ability> MUD_SKIN = REGISTER.register("mud_skin", () ->
			new Ability(new Ability.Properties(0x754b3f, 0x3f231b)
					.addAttribute(Attributes.ARMOR, UUID.fromString("fcaf52d5-fa7f-487b-af62-a7f59369e20b"), 1, Operation.ADDITION)
					.onHurt((entity, holder, event) -> {
						DamageSource source = event.getSource();
						Entity hitEntity = source.getEntity();
						if(hitEntity instanceof LivingEntity&&!source.isProjectile()){
							AbilityUtils.addStackEffect((LivingEntity)hitEntity, Effects.DIG_SLOWDOWN, 40, 0, 1, 5, true, true);
						}
					})));
	public static final RegistryObject<Ability> FROZEN_SKIN = REGISTER.register("frozen_skin", () ->
			new Ability(new Ability.Properties(0x2979bd, 0x2979bd)
					.addAttribute(Attributes.ARMOR, UUID.fromString("66fc401d-84e0-45f2-9abf-9a0fb7dee78a"), 1, Operation.ADDITION)
					.onHurt((entity, holder, event) -> {
						DamageSource source = event.getSource();
						Entity hitEntity = source.getEntity();
						if(hitEntity instanceof LivingEntity&&!source.isProjectile()){
							AbilityUtils.addStackEffect((LivingEntity)hitEntity, Effects.MOVEMENT_SLOWDOWN, 40, 0, 1, 5, true, true);
						}
					})));
	public static final RegistryObject<Ability> WOOLLY_SKIN = REGISTER.register("woolly_skin", () ->
			new Ability(new Ability.Properties(0xe0c1ad, 0x694d40)
					.addAttribute(Attributes.ARMOR, UUID.fromString("8d4b6252-301f-40db-93dc-96e78f199a53"), 1, Operation.ADDITION)
					.onHurt((entity, holder, event) -> {
						DamageSource source = event.getSource();
						Entity hitEntity = source.getEntity();
						if(hitEntity instanceof LivingEntity&&!source.isProjectile()){
							AbilityUtils.addStackEffect((LivingEntity)hitEntity, Effects.WEAKNESS, 20, 0, 1, 5, true, true);
						}
					})));
	public static final RegistryObject<Ability> FUZZY_SKIN = REGISTER.register("fuzzy_skin", () ->
			new Ability(new Ability.Properties(0xf2f5cd, 0xeaaeee)
					.addAttribute(Attributes.ARMOR, UUID.fromString("5fe9045c-0976-4faa-b5a7-d5b7c407723f"), 1, Operation.ADDITION)
					.onHurt((entity, holder, event) -> {
						DamageSource source = event.getSource();
						Entity hitEntity = source.getEntity();
						if(hitEntity instanceof LivingEntity&&!source.isProjectile()){
							((LivingEntity)hitEntity).addEffect(new EffectInstance(Effects.CONFUSION, 200));
						}
					})));
	// TODO NEED TO CHANGE COLORS
	public static final RegistryObject<Ability> SPINESKIN = REGISTER.register("spine_skin", () ->
			new Ability(new Ability.Properties(0xC8C8C8, 0xC8C8C8)
					.onHurt((entity, holder, event) -> {
						if(event.getSource() instanceof EntityDamageSource&&((EntityDamageSource)event.getSource()).isThorns()) return;
						Entity source = event.getSource().getEntity();
						if(source!=null&&source.isAlive())
							source.hurt(DamageSource.thorns(entity), ThornsEnchantment.getDamage(2, entity.getRandom()));
					}).addAttribute(Attributes.ARMOR, UUID.fromString("733dfe0f-a807-4812-b49b-3353e732fb03"), 1, Operation.ADDITION)));

	public static final RegistryObject<Ability> MELEE_VETERAN = REGISTER.register("melee_veteran", () ->
			new Ability(new Ability.Properties(0xe0c1ad, 0x694d40)
					.onAttack((entity, holder, event) -> {
						if(entity==event.getSource().getDirectEntity()){
							event.setAmount(event.getAmount()*(1.1f));
						}
					})));
	public static final RegistryObject<Ability> RANGE_VETERAN = REGISTER.register("range_veteran", () ->
			new Ability(new Ability.Properties(0xe0c1ad, 0x694d40)
					.onAttack((entity, holder, event) -> {
						if(entity!=event.getSource().getDirectEntity()&&event.getSource().isProjectile()&&!event.getSource().isMagic()){
							event.setAmount(event.getAmount()*(1.1f));
						}
					})));
	public static final RegistryObject<Ability> MAGIC_VETERAN = REGISTER.register("magic_veteran", () ->
			new Ability(new Ability.Properties(0xe0c1ad, 0x694d40)
					.onAttack((entity, holder, event) -> {
						if(event.getSource().isMagic()){
							event.setAmount(event.getAmount()*(1.1f));
						}
					})));

	public static final RegistryObject<Ability> BULLETPROOF = REGISTER.register("bulletproof", () ->
			new Ability(new Ability.Properties(0xC8C8C8, 0xC8C8C8)
					.onHurt((entity, holder, event) -> {
								DamageSource source = event.getSource();
								if(source.isProjectile()) event.setAmount(event.getAmount()*0.2f);
							}
					)));

	public static final RegistryObject<Ability> VAMPIRE = REGISTER.register("vampire", () ->
			new Ability(new Ability.Properties(0x800000, 0x800000)
					.onAttack((entity, holder, event) -> {
						LivingEntity target = event.getEntityLiving();
						float amount = event.getAmount();
						if(entity!=event.getSource().getDirectEntity()) amount /= 2;
						entity.heal(amount);
					})));

	public static final RegistryObject<Ability> CROWD_CONTROL = REGISTER.register("crowd_control", () ->
			new Ability(new Ability.Properties(0x000000, 0x0000000)
					.onAttack((entity, holder, event) -> {
						event.getEntityLiving().addEffect(new EffectInstance(Effects.LEVITATION, 100));
						event.getEntityLiving().addEffect(new EffectInstance(Effects.DIG_SLOWDOWN, 64, 1));
					})));


	@SubscribeEvent
	public static void newRegistry(RegistryEvent.NewRegistry e){
		registry = (ForgeRegistry<Ability>)new RegistryBuilder<Ability>()
				.setType(Ability.class)
				.setName(new ResourceLocation('z'+MODID, "abilities")) // FUCK you minecraft. FUCK you forge. FUCK everything.
				.create();
	}

	private static Supplier<Ability> heart(String uuid, double amount){
		UUID id = UUID.fromString(uuid);
		return () -> new Ability(new Ability.Properties(0xf54343, 0xa60000)
				.addAttribute(Attributes.MAX_HEALTH, id, amount, Operation.ADDITION));
	}
}
