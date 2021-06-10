package ttmp.infernoreborn.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ttmp.infernoreborn.InfernoReborn;
import vazkii.patchouli.api.PatchouliAPI;

import javax.annotation.Nonnull;

public class AbilDexItem extends Item {
    public AbilDexItem(Properties properties){
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
        ItemStack stack = playerIn.getItemInHand(hand);
        if (playerIn instanceof ServerPlayerEntity) {
            if(playerIn.isCrouching()) {
                System.out.println("Crouching");
                return new ActionResult<>(ActionResultType.SUCCESS, stack);
                // TODO
            }
            PatchouliAPI.get().openBookGUI((ServerPlayerEntity)playerIn, Registry.ITEM.getKey(this));

            //playerIn.playSound(GameRegistry.findRegistry(SoundEvent.class).getValue(new ResourceLocation("patchouli:book_open")), 1.0F, (float)(0.7D + Math.random() * 0.4D));
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

}
