package ttmp.infernoreborn.contents.item.weapon;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;
import ttmp.infernoreborn.contents.ModEffects;
import ttmp.infernoreborn.contents.ModItems;

import javax.annotation.Nullable;
import java.util.Map.Entry;

import static net.minecraft.entity.ai.attributes.AttributeModifier.Operation.ADDITION;

public class CrimsonClaymoreItem extends SwordItem{
	private static final IItemTier MAT = new IItemTier(){
		@Override public int getUses(){
			return 3000;
		}
		@Override public float getSpeed(){
			return 9;
		}
		@Override public float getAttackDamageBonus(){
			return 5;
		}
		@Override public int getLevel(){
			return 4;
		}
		@Override public int getEnchantmentValue(){
			return 18;
		}
		@Nullable private Ingredient repairIngredient;
		@Override public Ingredient getRepairIngredient(){
			if(repairIngredient==null) repairIngredient = Ingredient.of(ModItems.CRIMSON_METAL_SCRAP.get());
			return repairIngredient;
		}
	};

	public CrimsonClaymoreItem(Properties properties){
		super(MAT, 5, -2.4f, properties);
	}

	@Override public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected){
		if(selected){
			if(!(entity instanceof LivingEntity)) return;
			EffectInstance effect = ((LivingEntity)entity).getEffect(ModEffects.BLOOD_FRENZY.get());
			float damageBonus = getDamageBonus(stack);
			float calculatedDamageBonus = effect!=null ? (effect.getAmplifier()+1)*6 : 0;
			if(damageBonus!=calculatedDamageBonus)
				setDamageBonus(stack, calculatedDamageBonus);
		}else setDamageBonus(stack, 0);
	}

	@Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged){
		return slotChanged||oldStack.getItem()!=newStack.getItem();
	}
	@Override public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack){
		return oldStack.getItem()!=newStack.getItem();
	}

	@Override public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack){
		if(slot!=EquipmentSlotType.MAINHAND) return ImmutableMultimap.of();
		float damageBonus = getDamageBonus(stack);
		if(damageBonus!=0){
			ImmutableMultimap.Builder<Attribute, AttributeModifier> m = ImmutableMultimap.builder();
			for(Entry<Attribute, AttributeModifier> e : super.getAttributeModifiers(slot, stack).entries())
				if(e.getKey()!=Attributes.ATTACK_DAMAGE) m.putAll(e.getKey(), e.getValue());
			m.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", getDamage()+damageBonus, ADDITION));
			return m.build();
		}
		return super.getAttributeModifiers(slot, stack);
	}

	public static float getDamageBonus(ItemStack stack){
		CompoundNBT tag = stack.getTag();
		return tag!=null ? tag.getFloat("DamageBonus") : 0;
	}
	public static void setDamageBonus(ItemStack stack, float damageBonus){
		if(damageBonus==0){
			CompoundNBT tag = stack.getTag();
			if(tag!=null) tag.remove("DamageBonus");
		}else{
			CompoundNBT nbt = stack.getOrCreateTag();
			nbt.putFloat("DamageBonus", damageBonus);
		}
	}
}
