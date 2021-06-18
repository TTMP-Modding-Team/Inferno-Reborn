package ttmp.infernoreborn.contents.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import ttmp.infernoreborn.contents.ability.holder.ServerAbilityHolder;

public class RandomAbilityItem extends AbstractAbilityItem{
	public RandomAbilityItem(Properties properties){
		super(properties);
	}

	@Override protected boolean generate(ItemStack stack, LivingEntity entity){
		ServerAbilityHolder h = ServerAbilityHolder.of(entity);
		if(h==null) return false;
		h.setGenerateAbility(true);
		return true;
	}
}
