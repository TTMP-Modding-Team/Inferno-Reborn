package ttmp.infernoreborn;

import ttmp.infernoreborn.abilities.Abilities;
import ttmp.infernoreborn.abilities.Ability;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid= InfernoReborn.MODID)
public class InfernoRebornRegistry {
    @SubscribeEvent
    public static void registerNewRegistry(RegistryEvent.NewRegistry e){
        IForgeRegistry<Ability> builder = new RegistryBuilder<Ability>().setType(Ability.class).setName(new ResourceLocation(InfernoReborn.MODID, "abilities")).create();
    }

    public static void registerItems(RegistryEvent.Register<Item> e){

    }

    @SubscribeEvent
    public static void registerAbilities(RegistryEvent.Register<Ability> e){
        e.getRegistry().registerAll(
                Abilities.HEART_1
        );
    }
}
