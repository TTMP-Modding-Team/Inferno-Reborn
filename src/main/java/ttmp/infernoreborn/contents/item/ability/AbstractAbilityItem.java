package ttmp.infernoreborn.contents.item.ability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;

public abstract class AbstractAbilityItem extends Item{
	public AbstractAbilityItem(Properties properties){
		super(properties);
	}

	@Override public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand){
		if(!player.level.isClientSide){
			boolean succeed = false;
			if(player.isCrouching()&&player.isCreative()){
				for(LivingEntity e : player.level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(5, 5, 5))){
					if(generate(stack, e)) succeed = true;
				}
			}else{
				succeed = generate(stack, target);
			}
			if(succeed){
				stack.shrink(1);
				return ActionResultType.CONSUME;
			}
		}
		return ActionResultType.SUCCESS;
	}

	protected abstract boolean generate(ItemStack stack, LivingEntity entity);
}
