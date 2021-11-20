package ttmp.infernoreborn.contents.ability;

import net.minecraft.enchantment.ThornsEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import top.theillusivec4.curios.api.CuriosApi;
import ttmp.infernoreborn.contents.ModAttributes;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.ability.cooldown.Cooldown;
import ttmp.infernoreborn.util.EssenceType;
import ttmp.infernoreborn.util.LivingUtils;

import static net.minecraft.entity.ai.attributes.AttributeModifier.Operation.ADDITION;
import static net.minecraft.entity.ai.attributes.AttributeModifier.Operation.MULTIPLY_BASE;

public final class SkinAbility{
	private SkinAbility(){}

	public static Ability woodSkin(){
		return new Ability(new Ability.Properties(0x917142, 0x5f4a2b, 0xc29d62)
				.addAttribute(Attributes.ARMOR, "7c68dd04-64b6-4509-8ded-e3507560e8f0", 2, ADDITION)
				.drops(EssenceType.METAL, 3));
	}
	public static Ability rockSkin(){
		return new Ability(new Ability.Properties(0x8a8a8a, 0x525252)
				.addAttribute(Attributes.ARMOR, "a2ab4bdf-760c-464e-ad56-d21bd367ffb3", 4, ADDITION)
				.drops(EssenceType.METAL, 6));
	}
	public static Ability ironSkin(){
		return new Ability(new Ability.Properties(0xdbdbdb, 0x686868, 0xeeeeee)
				.addAttribute(Attributes.ARMOR, "1fc727a2-4aed-4578-9c0e-47dce56f6785", 4, ADDITION)
				.drops(EssenceType.METAL, 9));
	}

	public static Ability diamondSkin(){
		return new Ability(new Ability.Properties(0x4deeec, 0x239180, 0xa1ecf3)
				.addAttribute(Attributes.ARMOR, "6319cb70-cb20-40ca-928b-c6d52ff30598", 10, ADDITION)
				.addAttribute(Attributes.ARMOR_TOUGHNESS, "1fc727a2-4aed-4578-9c0e-47dce56f6785", 4, ADDITION)
				.addAttribute(ModAttributes.DAMAGE_RESISTANCE.get(), "1e9c7ee4-7419-4fd3-ab09-81b8dfa8c763", .1, MULTIPLY_BASE)
				.drops(EssenceType.METAL, 2*9));
	}
	public static Ability netheriteSkin(){
		return new Ability(new Ability.Properties(0x4f3c3e, 0x4a2940, 0xcdbccd)
				.addAttribute(Attributes.ARMOR, "22ab3354-54fd-452f-8505-fce3eb7d2645", 14, ADDITION)
				.addAttribute(Attributes.ARMOR_TOUGHNESS, "4f256a3f-175b-4b2a-9eda-0582dcc9c6d8", 10, ADDITION)
				.addAttribute(ModAttributes.DAMAGE_RESISTANCE.get(), "85171c75-6976-4ae4-b1b7-5c0fbd2acbfd", .2, MULTIPLY_BASE)
				.drops(EssenceType.METAL, 4*9));
	}
	public static Ability mudSkin(){
		return new Ability(new Ability.Properties(0x754b3f, 0x3f231b)
				.addAttribute(Attributes.ARMOR, "fcaf52d5-fa7f-487b-af62-a7f59369e20b", 1, ADDITION)
				.onHurt(skin((event, hit) -> LivingUtils.addStackEffect(hit, Effects.DIG_SLOWDOWN, 40, 0, 1, 5, true, true)))
				.drops(EssenceType.METAL, 5)
				.drops(EssenceType.EARTH, 5));
	}
	public static Ability frozenSkin(){
		return new Ability(new Ability.Properties(0x2979bd, 0x2979bd)
				.addAttribute(Attributes.ARMOR, "66fc401d-84e0-45f2-9abf-9a0fb7dee78a", 1, ADDITION)
				.onHurt(skin((event, hit) -> LivingUtils.addStackEffect(hit, Effects.MOVEMENT_SLOWDOWN, 40, 0, 1, 3, true, true)))
				.drops(EssenceType.METAL, 5)
				.drops(EssenceType.FROST, 5));
	}
	public static Ability woollySkin(){
		return new Ability(new Ability.Properties(0xe0c1ad, 0x694d40)
				.addAttribute(Attributes.ARMOR, "8d4b6252-301f-40db-93dc-96e78f199a53", 1, ADDITION)
				.onHurt(skin((event, hit) -> LivingUtils.addStackEffect(hit, Effects.WEAKNESS, 20, 0, 1, 5, true, true)))
				.drops(EssenceType.METAL, 5)
				.drops(EssenceType.AIR, 5));
	}
	public static Ability fuzzySkin(){
		return new Ability(new Ability.Properties(0xf2f5cd, 0xeaaeee)
				.addAttribute(Attributes.ARMOR, "5fe9045c-0976-4faa-b5a7-d5b7c407723f", 1, ADDITION)
				.onHurt(skin((event, hit) -> hit.addEffect(new EffectInstance(Effects.CONFUSION, 200))))
				.drops(EssenceType.METAL, 5)
				.drops(EssenceType.MAGIC, 5));
	}
	public static Ability thornSkin(){
		return new Ability(new Ability.Properties(0xC8C8C8, 0xC8C8C8)
				.onHurt(skin((event, hit) -> {
					if(event.getSource() instanceof EntityDamageSource&&((EntityDamageSource)event.getSource()).isThorns()) return;
					hit.hurt(DamageSource.thorns(event.getEntityLiving()), ThornsEnchantment.getDamage(2, event.getEntityLiving().getRandom()));
				})).addAttribute(Attributes.ARMOR, "733dfe0f-a807-4812-b49b-3353e732fb03", 1, ADDITION)
				.drops(EssenceType.METAL, 5)
				.drops(EssenceType.EARTH, 3)
				.drops(EssenceType.MAGIC, 2));
	}
	public static Ability magmaSkin(){
		return new Ability(new Ability.Properties(0x340000, 0x340000)
				.onUpdate((entity, holder) -> entity.setSecondsOnFire(1))
				.onAttacked((entity, holder, event) -> {
					if(event.getSource().isFire()) event.setCanceled(true);
				})
				.onHit(skin((event, hit) -> {
					if(!hit.isInWaterOrRain()) hit.setSecondsOnFire(8);
				})).addAttribute(ModAttributes.DAMAGE_RESISTANCE.get(), "55a108b6-55ff-4b25-a416-afd9f806de69", .2, MULTIPLY_BASE)
				.drops(EssenceType.METAL, 2*9)
				.drops(EssenceType.FIRE, 2*9));
	}
	public static Ability electricSkin(){
		return new Ability(new Ability.Properties(0xFFFF00, 0xFFFF00)
				.withCooldownTicket((ticket, properties) ->
						properties.onAttacked((entity, holder, event) -> {
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
						}))
				.drops(EssenceType.METAL, 2*9)
				.drops(EssenceType.FIRE, 9)
				.drops(EssenceType.AIR, 9));
	}

	private static OnAbilityEvent<LivingHurtEvent> skin(SkinEffect effect){
		return (entity, holder, event) -> {
			Entity hitEntity = event.getSource().getDirectEntity();
			if(hitEntity instanceof LivingEntity&&
					hitEntity.isAlive()&&
					!event.getSource().isProjectile()&&
					!CuriosApi.getCuriosHelper().findEquippedCurio(ModItems.BATTLE_MITTS.get(), (LivingEntity)hitEntity).isPresent())
				effect.apply(event, (LivingEntity)hitEntity);
		};
	}

	@FunctionalInterface
	private interface SkinEffect{
		void apply(LivingHurtEvent event, LivingEntity hit);
	}
}
