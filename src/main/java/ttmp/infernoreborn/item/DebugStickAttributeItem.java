package ttmp.infernoreborn.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ttmp.infernoreborn.InfernoReborn;

public class DebugStickAttributeItem extends Item {

    public DebugStickAttributeItem(Properties properties) {
        super(properties);
    }


    @Override
    public ActionResultType interactLivingEntity(ItemStack itemStack, PlayerEntity player, LivingEntity target, Hand hand) {
        if (!player.level.isClientSide) {
            for (Attribute attribute : GameRegistry.findRegistry(Attribute.class).getValues()) {
                if (target.getAttribute(attribute) != null)
                    InfernoReborn.LOGGER.debug(attribute.getRegistryName().toString() + " : " + target.getAttribute(attribute).getValue());
            }
        }
        return ActionResultType.SUCCESS;
    }
}
