package ttmp.infernoreborn;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
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
import ttmp.infernoreborn.capability.EssenceHolder;
import ttmp.infernoreborn.capability.ShieldHolder;
import ttmp.infernoreborn.capability.TickingTaskHandler;
import ttmp.infernoreborn.client.EssenceHolderBookSparkColor;
import ttmp.infernoreborn.client.ItemColorUtils;
import ttmp.infernoreborn.client.PrimalInfernoSparkColor;
import ttmp.infernoreborn.client.screen.EssenceHolderScreen;
import ttmp.infernoreborn.client.screen.SigilEngravingTableScreen;
import ttmp.infernoreborn.contents.Abilities;
import ttmp.infernoreborn.contents.ModAttributes;
import ttmp.infernoreborn.contents.ModBlocks;
import ttmp.infernoreborn.contents.ModContainers;
import ttmp.infernoreborn.contents.ModEffects;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.ModRecipes;
import ttmp.infernoreborn.contents.ModTileEntities;
import ttmp.infernoreborn.contents.Sigils;
import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.contents.ability.generator.scheme.AbilityGeneratorScheme;
import ttmp.infernoreborn.contents.ability.holder.AbilityHolder;
import ttmp.infernoreborn.contents.item.FixedAbilityItem;
import ttmp.infernoreborn.contents.item.GeneratorAbilityItem;
import ttmp.infernoreborn.contents.sigil.holder.SigilHolder;
import ttmp.infernoreborn.datagen.AbilityGeneratorDataProvider;
import ttmp.infernoreborn.datagen.BlockTagGen;
import ttmp.infernoreborn.datagen.BookDataProvider;
import ttmp.infernoreborn.datagen.ItemModelGen;
import ttmp.infernoreborn.datagen.ItemTagGen;
import ttmp.infernoreborn.datagen.McmetaGen;
import ttmp.infernoreborn.datagen.RecipeGen;
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
		ModBlocks.REGISTER.register(modEventBus);
		ModContainers.REGISTER.register(modEventBus);
		ModEffects.REGISTER.register(modEventBus);
		ModItems.REGISTER.register(modEventBus);
		ModRecipes.REGISTER.register(modEventBus);
		ModTileEntities.REGISTER.register(modEventBus);
		Sigils.REGISTER.register(modEventBus);

		ModNet.init();
	}

	@SubscribeEvent
	public static void commonSetup(FMLCommonSetupEvent event){
		event.enqueueWork(() -> {
			registerDefaultCapability(AbilityHolder.class);
			registerDefaultCapability(EssenceHolder.class);
			registerDefaultCapability(SigilHolder.class);
			registerDefaultCapability(ShieldHolder.class);
			registerDefaultCapability(TickingTaskHandler.class);
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
			generator.addProvider(new BookDataProvider(event.getGenerator()));
			generator.addProvider(new RecipeGen(event.getGenerator()));
			BlockTagGen blockTagGen = new BlockTagGen(event.getGenerator(), event.getExistingFileHelper());
			generator.addProvider(blockTagGen);
			generator.addProvider(new ItemTagGen(event.getGenerator(), blockTagGen, event.getExistingFileHelper()));
		}
		if(event.includeClient()){
			generator.addProvider(new ItemModelGen(event.getGenerator(), event.getExistingFileHelper()));
			generator.addProvider(new McmetaGen(event.getGenerator(), event.getExistingFileHelper()));
		}
	}

	@Mod.EventBusSubscriber(modid = MODID, bus = Bus.MOD, value = Dist.CLIENT)
	private static final class Client{
		private Client(){}

		@SubscribeEvent
		public static void clientSetup(FMLClientSetupEvent event){
			event.enqueueWork(() -> {
				ScreenManager.register(ModContainers.ESSENCE_HOLDER.get(), EssenceHolderScreen::new);
				ScreenManager.register(ModContainers.SIGIL_ENGRAVING_TABLE_3X3.get(), SigilEngravingTableScreen.X3::new);
				ScreenManager.register(ModContainers.SIGIL_ENGRAVING_TABLE_5X5.get(), SigilEngravingTableScreen.X5::new);
				ScreenManager.register(ModContainers.SIGIL_ENGRAVING_TABLE_7X7.get(), SigilEngravingTableScreen.X7::new);

				ItemModelsProperties.register(ModItems.EXPLOSIVE_SWORD.get(), new ResourceLocation("using"),
						(stack, world, entity) -> entity!=null&&entity.isUsingItem()&&entity.getUseItem()==stack ? 1 : 0);
			});
		}

		@SubscribeEvent
		public static void onItemColor(ColorHandlerEvent.Item event){
			event.getItemColors().register((stack, layer) -> {
				Ability[] abilities = FixedAbilityItem.getAbilities(stack);

				switch(layer){
					case 0:
						return ItemColorUtils.getPrimaryColorBlend(abilities);
					case 1:
						return ItemColorUtils.getSecondaryColorBlend(abilities);
					case 2:
						return ItemColorUtils.getHighlightColorBlend(abilities);
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
			event.getItemColors().register(new PrimalInfernoSparkColor(), ModItems.PRIMAL_INFERNO_SPARK.get());
			event.getItemColors().register(new EssenceHolderBookSparkColor(), ModItems.BOOK_OF_THE_UNSPEAKABLE_COMBINED.get());
		}
	}
}
