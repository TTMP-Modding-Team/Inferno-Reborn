package ttmp.infernoreborn.contents.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import ttmp.infernoreborn.contents.ModEffects;

import java.util.List;

public class ItemThanatosBelt extends Item implements ICurioItem{
	public ItemThanatosBelt(Properties properties){
		super(properties);
	}
/*
	@Override public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack){
		ICurioItem.super.curioTick(identifier, index, livingEntity, stack);
		EffectInstance effect = livingEntity.getEffect(ModEffects.GHOST_TRICK_COOLDOWN.get());
		if(!livingEntity.hasEffect(ModEffects.GHOST_TRICK_COOLDOWN.get()) && livingEntity.getHealth()<livingEntity.getMaxHealth()/2){
			List<Entity> entityList = livingEntity.level.getEntities(livingEntity, livingEntity.getBoundingBox().inflate(6), entity -> entity instanceof LivingEntity && !(entity instanceof PlayerEntity));
			entityList.forEach((entity) -> {
				entity.hurt(DamageSource.mobAttack(livingEntity).setMagic(), )
			});
		}
	}*/
}
