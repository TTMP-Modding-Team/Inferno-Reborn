package ttmp.infernoreborn;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.data.DataGenerator;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ttmp.infernoreborn.ability.Ability;
import ttmp.infernoreborn.ability.generator.scheme.AbilityGeneratorScheme;
import ttmp.infernoreborn.capability.AbilityHolder;
import ttmp.infernoreborn.capability.EssenceHolder;
import ttmp.infernoreborn.client.EssenceHolderScreen;
import ttmp.infernoreborn.contents.Abilities;
import ttmp.infernoreborn.contents.ModAttributes;
import ttmp.infernoreborn.contents.ModContainers;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.ModRecipes;
import ttmp.infernoreborn.datagen.AbilDexDataProvider;
import ttmp.infernoreborn.datagen.AbilityGeneratorDataProvider;
import ttmp.infernoreborn.datagen.ItemModelGen;
import ttmp.infernoreborn.item.FixedAbilityItem;
import ttmp.infernoreborn.item.GeneratorAbilityItem;
import ttmp.infernoreborn.network.ModNet;

import javax.annotation.Nullable;

@Mod(InfernoReborn.MODID)
@Mod.EventBusSubscriber(modid = InfernoReborn.MODID, bus = Bus.MOD)
public class InfernoReborn{
	public static final String MODID = "infernoreborn";
	public static final String VERSION = "1";
	public static final Logger LOGGER = LogManager.getLogger("Inferno Reborn");

	public InfernoReborn(){
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		Abilities.REGISTER.register(modEventBus);
		ModAttributes.REGISTER.register(modEventBus);
		ModContainers.REGISTER.register(modEventBus);
		ModItems.REGISTER.register(modEventBus);
		ModRecipes.REGISTER.register(modEventBus);

		ModNet.init();
	}

	@SubscribeEvent
	public static void commonSetup(FMLCommonSetupEvent event){
		event.enqueueWork(() -> {
			registerDefaultCapability(AbilityHolder.class);
			registerDefaultCapability(EssenceHolder.class);
		});
	}

	private static <T> void registerDefaultCapability(Class<T> clazz){
		CapabilityManager.INSTANCE.register(clazz, new Capability.IStorage<T>(){
			@Nullable @Override public INBT writeNBT(Capability<T> capability, T instance, Direction side){
				return null;
			}
			@Override public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt){}
		}, () -> {
			throw new UnsupportedOperationException();
		});
	}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event){
		DataGenerator generator = event.getGenerator();
		if(event.includeServer()){
			generator.addProvider(new AbilityGeneratorDataProvider(event.getGenerator()));
			generator.addProvider(new AbilDexDataProvider(event.getGenerator()));
		}
		if(event.includeClient()){
			generator.addProvider(new ItemModelGen(event.getGenerator(), event.getExistingFileHelper()));
		}
	}

	@Mod.EventBusSubscriber(modid = MODID, bus = Bus.MOD, value = Dist.CLIENT)
	private static final class Client{
		private Client(){}

		@SubscribeEvent
		public static void clientSetup(FMLClientSetupEvent event){
			event.enqueueWork(() -> {
				ScreenManager.register(ModContainers.ESSENCE_HOLDER.get(), EssenceHolderScreen::new);
			});
		}

		@SubscribeEvent
		public static void onItemColor(ColorHandlerEvent.Item event){
			event.getItemColors().register((stack, layer) -> {
				Ability[] abilities = FixedAbilityItem.getAbilities(stack);

				switch(layer){
					case 0:
						return abilities.length==0 ? 0x3a3a3a : abilities[0].getPrimaryColor();
					case 1:
						return abilities.length==0 ? 0xff00ff : abilities[0].getSecondaryColor();
					case 2:
						return abilities.length==0 ? 0x3a3a3a : abilities[0].getHighlightColor();
					default:
						return -1;
				}
			}, ModItems.INFERNO_SPARK.get());
			event.getItemColors().register((stack, layer) -> {
				AbilityGeneratorScheme scheme = GeneratorAbilityItem.getScheme(stack);

				switch(layer){
					case 0:
						return scheme==null||scheme.getItemDisplay()==null ? 0x3a3a3a : scheme.getItemDisplay().getPrimaryColor();
					case 1:
						return scheme==null||scheme.getItemDisplay()==null ? 0xff00ff : scheme.getItemDisplay().getSecondaryColor();
					case 2:
						return scheme==null||scheme.getItemDisplay()==null ? 0x3a3a3a : scheme.getItemDisplay().getHighlightColor();
					default:
						return -1;
				}
			}, ModItems.GENERATOR_INFERNO_SPARK.get());
		}
	}
}
