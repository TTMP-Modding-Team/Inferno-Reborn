package ttmp.infernoreborn;

import net.minecraft.data.DataGenerator;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ttmp.infernoreborn.capability.AbilityHolder;
import ttmp.infernoreborn.contents.Abilities;
import ttmp.infernoreborn.contents.ModAttributes;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.datagen.AbilityGeneratorDataProvider;
import ttmp.infernoreborn.network.ModNet;

import javax.annotation.Nullable;


@Mod(InfernoReborn.MODID)
@Mod.EventBusSubscriber(modid = InfernoReborn.MODID, bus = Bus.MOD)
public class InfernoReborn{
	public static final String MODID = "infernoreborn";
	public static final Logger LOGGER = LogManager.getLogger("Inferno Reborn");

	public InfernoReborn(){
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		Abilities.REGISTER.register(modEventBus);
		ModAttributes.REGISTER.register(modEventBus);
		ModItems.REGISTER.register(modEventBus);

		ModNet.init();
	}

	@SubscribeEvent
	public static void commonSetup(FMLCommonSetupEvent event){
		event.enqueueWork(() -> {
			CapabilityManager.INSTANCE.register(AbilityHolder.class, new Capability.IStorage<AbilityHolder>(){
				@Nullable @Override public INBT writeNBT(Capability<AbilityHolder> capability, AbilityHolder instance, Direction side){
					return null;
				}
				@Override public void readNBT(Capability<AbilityHolder> capability, AbilityHolder instance, Direction side, INBT nbt){}
			}, () -> {
				throw new UnsupportedOperationException();
			});
		});
	}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event){
		DataGenerator generator = event.getGenerator();
		if(event.includeServer()){
			generator.addProvider(new AbilityGeneratorDataProvider(event.getGenerator()));
		}
	}
}
