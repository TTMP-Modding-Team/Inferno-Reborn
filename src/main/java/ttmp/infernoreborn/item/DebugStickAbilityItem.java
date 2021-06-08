package ttmp.infernoreborn.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.ability.Ability;
import ttmp.infernoreborn.capability.AbilityHolder;

public class DebugStickAbilityItem extends Item {

    public DebugStickAbilityItem(Properties properties) {
        super(properties);
    }


    @Override
    public ActionResultType interactLivingEntity(ItemStack itemStack, PlayerEntity player, LivingEntity target, Hand hand) {
        if (!player.level.isClientSide) {
            AbilityHolder h = AbilityHolder.of(target);
            if (h != null) {
                for (Ability ability : h.getAbilities()){
                    InfernoReborn.LOGGER.debug(ability.toString());
                }
            }
        }
        return ActionResultType.SUCCESS;
    }
}
