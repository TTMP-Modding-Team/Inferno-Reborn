package ttmp.infernoreborn.util;

import com.google.common.collect.ListMultimap;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.capability.ShieldHolder;
import ttmp.infernoreborn.contents.Abilities;
import ttmp.infernoreborn.contents.ability.holder.AbilityHolder;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import static net.minecraft.item.ItemStack.ATTRIBUTE_MODIFIER_FORMAT;

public final class LivingUtils{
	private LivingUtils(){}

	private static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	private static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{
			UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
			UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
			UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
			UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")
	};

	public static UUID getAttackDamageId(){
		return BASE_ATTACK_DAMAGE_UUID;
	}
	public static UUID getAttackSpeedId(){
		return BASE_ATTACK_SPEED_UUID;
	}
	public static UUID getArmorModifierId(EquipmentSlotType type){
		if(type.getType()!=EquipmentSlotType.Group.ARMOR) throw new IllegalArgumentException("type");
		return ARMOR_MODIFIER_UUID_PER_SLOT[type.getIndex()];
	}

	public static void addStackEffect(LivingEntity entity,
	                                  Effect effect,
	                                  int duration,
	                                  int startAmplifier,
	                                  int amplifierGrow,
	                                  int maxAmplifier){
		addStackEffect(entity, effect, duration, startAmplifier, amplifierGrow, maxAmplifier, true, true);
	}

	public static void addStackEffect(LivingEntity entity,
	                                  Effect effect,
	                                  int duration,
	                                  int startAmplifier,
	                                  int amplifierGrow,
	                                  int maxAmplifier,
	                                  boolean visible,
	                                  boolean showIcon){
		EffectInstance instance = entity.getEffect(effect);
		if(instance==null){
			entity.addEffect(new EffectInstance(effect, duration, startAmplifier, false, visible, showIcon));
		}else if(instance.getAmplifier()<=maxAmplifier&&instance.getDuration()<duration){
			entity.addEffect(new EffectInstance(effect,
					duration,
					Math.min(instance.getAmplifier()+amplifierGrow, maxAmplifier),
					false,
					visible,
					showIcon));
		}
	}

	public static void addInfiniteEffect(LivingEntity entity, Effect effect, int amp){
		EffectInstance e = entity.getEffect(effect);
		if(e==null||e.getDuration()<30||e.getAmplifier()<amp){
			entity.addEffect(new EffectInstance(effect, 400, amp, true, false));
		}
	}

	@Nullable public static LivingEntity getTarget(LivingEntity entity){
		if(entity instanceof MobEntity) return ((MobEntity)entity).getTarget();
		else if(entity instanceof IAngerable) return ((IAngerable)entity).getTarget();
		else return null;
	}

	public static double getAttrib(LivingEntity entity, Attribute attrib){
		ModifiableAttributeInstance a1 = entity.getAttribute(attrib);
		if(a1!=null) return a1.getValue();
		InfernoReborn.LOGGER.warn("Cannot find {} from {}", attrib, entity);
		return attrib.getDefaultValue();
	}

	public static float getShield(LivingEntity entity){
		ShieldHolder h = ShieldHolder.of(entity);
		return h!=null ? h.getShield() : 0;
	}

	public static void setShield(LivingEntity entity, float shield){
		ShieldHolder h = ShieldHolder.of(entity);
		if(h!=null) h.setShield(shield);
	}

	public static void addShield(LivingEntity entity, float shield){
		ShieldHolder h = ShieldHolder.of(entity);
		if(h!=null) h.setShield(h.getShield()+shield);
	}

	public static void addToModifier(ListMultimap<Attribute, AttributeModifier> attributeMap, Attribute attribute, @Nullable UUID targetUuid, double amount, AttributeModifier.Operation operation){
		addToModifier(attributeMap, attribute, targetUuid, targetUuid, amount, operation);
	}
	public static void addToModifier(ListMultimap<Attribute, AttributeModifier> attributeMap, Attribute attribute, @Nullable UUID targetUuid, @Nullable UUID fallbackUuid, double amount, AttributeModifier.Operation operation){
		List<AttributeModifier> list = attributeMap.get(attribute);
		int modifyTargetIndex = -1;
		for(int i = 0; i<list.size(); i++){
			AttributeModifier m = list.get(i);
			if(m.getOperation()!=operation) continue;
			modifyTargetIndex = i;
			if(targetUuid!=null&&m.getId().equals(targetUuid)) break;
		}
		if(modifyTargetIndex>=0){
			AttributeModifier m = list.get(modifyTargetIndex);
			list.set(modifyTargetIndex, new AttributeModifier(m.getId(), m.getName(), m.getAmount()+amount, m.getOperation()));
		}else attributeMap.put(attribute, new AttributeModifier(fallbackUuid!=null ? fallbackUuid : UUID.randomUUID(), "", amount, operation));
	}
	public static Explosion.Mode getExplosionMode(World world){
		return world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
	}

	public static float explosionDamagePredicate(LivingEntity target, float explosionSize){
		if(isImmuneToExplosions(target)||target.getEyeHeight()==0) return 0;
		double density = Explosion.getSeenPercent(target.position(), target);
		return calcDamageWithArmor(target, (int)((density*density+density)/2*7*explosionSize+1));
	}

	private static boolean isImmuneToExplosions(LivingEntity target){
		if(target.ignoreExplosion()||target.isInvulnerableTo(DamageSource.explosion((LivingEntity)null))) return true;
		AbilityHolder holder = AbilityHolder.of(target);
		if(holder==null) return false;
		return holder.has(Abilities.KILLER_QUEEN.get())||holder.has(Abilities.GUNPOWDER_SWARM.get());
	}

	private static float calcDamageWithArmor(LivingEntity target, float damage){
		if(damage<=0) return 0;
		damage = CombatRules.getDamageAfterAbsorb(damage, target.getArmorValue(), (float)getAttrib(target, Attributes.ARMOR_TOUGHNESS));
		EffectInstance damageResistance = target.getEffect(Effects.DAMAGE_RESISTANCE);
		if(damageResistance!=null)
			damage = damage*(25-(damageResistance.getAmplifier()+1)*5)/25;
		if(damage<=0) return 0;
		int k = EnchantmentHelper.getDamageProtection(target.getArmorSlots(), DamageSource.explosion((LivingEntity)null));
		if(k>0) damage = CombatRules.getDamageAfterMagicAbsorb(damage, k);
		return Math.max(damage-target.getAbsorptionAmount(), 0);
	}

	public static IFormattableTextComponent getAttributeText(Attribute attribute, double amount, AttributeModifier.Operation operation){
		double displayAmount = operation!=AttributeModifier.Operation.MULTIPLY_BASE&&operation!=AttributeModifier.Operation.MULTIPLY_TOTAL ?
				attribute.equals(Attributes.KNOCKBACK_RESISTANCE) ? amount*10 : amount :
				amount*100;

		if(amount<0) return new TranslationTextComponent("attribute.modifier.take."+operation.toValue(),
				ATTRIBUTE_MODIFIER_FORMAT.format(-displayAmount),
				I18n.get(attribute.getDescriptionId())).withStyle(TextFormatting.RED);
		else return new TranslationTextComponent("attribute.modifier.plus."+operation.toValue(),
				ATTRIBUTE_MODIFIER_FORMAT.format(displayAmount),
				I18n.get(attribute.getDescriptionId())).withStyle(TextFormatting.BLUE);
	}
}
