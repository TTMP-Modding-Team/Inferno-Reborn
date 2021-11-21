package ttmp.infernoreborn;

import net.minecraft.block.SkullBlock;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.tileentity.SkullTileEntityRenderer;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;
import ttmp.infernoreborn.capability.ClientPlayerCapability;
import ttmp.infernoreborn.capability.EssenceNetProvider;
import ttmp.infernoreborn.capability.PlayerCapability;
import ttmp.infernoreborn.capability.ShieldProvider;
import ttmp.infernoreborn.capability.TickingTaskHandler;
import ttmp.infernoreborn.client.color.EssenceHolderBookSparkColor;
import ttmp.infernoreborn.client.color.GeneratorInfernoSparkColor;
import ttmp.infernoreborn.client.color.PrimalInfernoSparkColor;
import ttmp.infernoreborn.client.color.SparkColor;
import ttmp.infernoreborn.client.render.AnvilEntityRenderer;
import ttmp.infernoreborn.client.render.CreeperMissileEntityRenderer;
import ttmp.infernoreborn.client.render.GhostEntityRenderer;
import ttmp.infernoreborn.client.render.GoldenSkullTileEntityRenderer;
import ttmp.infernoreborn.client.render.SummonedSkeletonRenderer;
import ttmp.infernoreborn.client.render.SummonedZombieRenderer;
import ttmp.infernoreborn.client.render.WindEntityRenderer;
import ttmp.infernoreborn.client.screen.EssenceHolderScreen;
import ttmp.infernoreborn.client.screen.FoundryScreen;
import ttmp.infernoreborn.client.screen.SigilEngravingTableScreen;
import ttmp.infernoreborn.client.screen.SigilScrapperScreen;
import ttmp.infernoreborn.client.screen.StigmaTableScreen;
import ttmp.infernoreborn.config.ModCfg;
import ttmp.infernoreborn.contents.Abilities;
import ttmp.infernoreborn.contents.ModAttributes;
import ttmp.infernoreborn.contents.ModBlocks;
import ttmp.infernoreborn.contents.ModContainers;
import ttmp.infernoreborn.contents.ModEffects;
import ttmp.infernoreborn.contents.ModEntities;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.ModLootModifiers;
import ttmp.infernoreborn.contents.ModRecipes;
import ttmp.infernoreborn.contents.ModTileEntities;
import ttmp.infernoreborn.contents.Sigils;
import ttmp.infernoreborn.contents.ability.holder.AbilityHolder;
import ttmp.infernoreborn.contents.block.GoldenSkullBlock;
import ttmp.infernoreborn.contents.block.essencenet.EssenceNetCoreBlock;
import ttmp.infernoreborn.contents.entity.GhostEntity;
import ttmp.infernoreborn.contents.entity.SummonedSkeletonEntity;
import ttmp.infernoreborn.contents.entity.SummonedZombieEntity;
import ttmp.infernoreborn.contents.item.EssenceNetAccessorItem;
import ttmp.infernoreborn.contents.item.JudgementItem;
import ttmp.infernoreborn.contents.sigil.holder.SigilHolder;
import ttmp.infernoreborn.datagen.BlockModelGen;
import ttmp.infernoreborn.datagen.BlockTagGen;
import ttmp.infernoreborn.datagen.BookDataProvider;
import ttmp.infernoreborn.datagen.InfernalTypeDataProvider;
import ttmp.infernoreborn.datagen.ItemModelGen;
import ttmp.infernoreborn.datagen.ItemTagGen;
import ttmp.infernoreborn.datagen.LootModifierGen;
import ttmp.infernoreborn.datagen.LootTableGen;
import ttmp.infernoreborn.datagen.McmetaGen;
import ttmp.infernoreborn.datagen.RecipeGen;
import ttmp.infernoreborn.network.ModNet;
import ttmp.infernoreborn.util.EssenceHolder;

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
		ModLootModifiers.REGISTER.register(modEventBus);
		ModRecipes.REGISTER.register(modEventBus);
		ModTileEntities.REGISTER.register(modEventBus);
		ModEntities.REGISTER.register(modEventBus);
		Sigils.REGISTER.register(modEventBus);

		ModCfg.init();
		ModNet.init();
	}

	@SubscribeEvent
	public static void sendIMC(InterModEnqueueEvent e){
		InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BELT.getMessageBuilder().build());
		InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.NECKLACE.getMessageBuilder().build());
		InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.RING.getMessageBuilder().size(2).build());
		InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.HANDS.getMessageBuilder().build());
	}

	@SubscribeEvent
	public static void commonSetup(FMLCommonSetupEvent event){
		event.enqueueWork(() -> {
			registerDefaultCapability(AbilityHolder.class);
			registerDefaultCapability(EssenceHolder.class);
			registerDefaultCapability(SigilHolder.class);
			registerDefaultCapability(TickingTaskHandler.class);
			registerDefaultCapability(PlayerCapability.class);
			registerDefaultCapability(ShieldProvider.class);
			registerDefaultCapability(EssenceNetProvider.class);
			registerDefaultCapability(EssenceNetAccessorItem.Data.class);
			registerDefaultCapability(ClientPlayerCapability.class);
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
			generator.addProvider(new BookDataProvider(event.getGenerator()));
			generator.addProvider(new RecipeGen(event.getGenerator()));
			BlockTagGen blockTagGen = new BlockTagGen(event.getGenerator(), event.getExistingFileHelper());
			generator.addProvider(blockTagGen);
			generator.addProvider(new ItemTagGen(event.getGenerator(), blockTagGen, event.getExistingFileHelper()));
			generator.addProvider(new LootModifierGen(event.getGenerator()));
			generator.addProvider(new LootTableGen(event.getGenerator()));
			generator.addProvider(new InfernalTypeDataProvider(event.getGenerator()));
		}
		if(event.includeClient()){
			generator.addProvider(new ItemModelGen(event.getGenerator(), event.getExistingFileHelper()));
			generator.addProvider(new BlockModelGen(event.getGenerator(), event.getExistingFileHelper()));
			generator.addProvider(new McmetaGen(event.getGenerator(), event.getExistingFileHelper()));
		}
	}

	@SubscribeEvent
	public static void registerAttributes(final EntityAttributeCreationEvent event){
		event.put(ModEntities.SUMMONED_ZOMBIE.get(), SummonedZombieEntity.registerAttributes().build());
		event.put(ModEntities.SUMMONED_SKELETON.get(), SummonedSkeletonEntity.registerAttributes().build());
		event.put(ModEntities.GHOST.get(), GhostEntity.registerAttributes().build());
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
				ScreenManager.register(ModContainers.STIGMA_TABLE_5X5.get(), StigmaTableScreen.X5::new);
				ScreenManager.register(ModContainers.STIGMA_TABLE_7X7.get(), StigmaTableScreen.X7::new);
				ScreenManager.register(ModContainers.SIGIL_SCRAPPER.get(), SigilScrapperScreen::new);
				ScreenManager.register(ModContainers.FOUNDRY.get(), FoundryScreen::new);

				ItemModelsProperties.register(ModItems.EXPLOSIVE_SWORD.get(), new ResourceLocation("using"),
						(stack, world, entity) -> entity!=null&&entity.isUsingItem()&&entity.getUseItem()==stack ? 1 : 0);
				ItemModelsProperties.register(ModItems.JUDGEMENT.get(), new ResourceLocation("off"),
						(stack, world, entity) -> entity==null||JudgementItem.isOff(stack) ? 1 : 0);

				ResourceLocation noNetworkId = new ResourceLocation("no_network");
				IItemPropertyGetter noNetwork = (stack, world, entity) ->
						(stack.getItem() instanceof EssenceNetCoreBlock.HasEssenceNet)&&((EssenceNetCoreBlock.HasEssenceNet)stack.getItem()).getNetwork(stack)!=0 ? 0 : 1;
				ItemModelsProperties.register(ModItems.ESSENCE_NET_ACCESSOR.get(), noNetworkId, noNetwork);
				ItemModelsProperties.register(ModItems.ESSENCE_NET_IMPORTER.get(), noNetworkId, noNetwork);
				ItemModelsProperties.register(ModItems.ESSENCE_NET_EXPORTER.get(), noNetworkId, noNetwork);

				RenderTypeLookup.setRenderLayer(ModBlocks.FOUNDRY_MOLD_1.get(), RenderType.cutout());
				RenderTypeLookup.setRenderLayer(ModBlocks.FOUNDRY_MOLD_2.get(), RenderType.cutout());
				RenderTypeLookup.setRenderLayer(ModBlocks.ESSENCE_HOLDER_BLOCK.get(), RenderType.translucent());
				RenderTypeLookup.setRenderLayer(ModBlocks.ESSENCE_NET_CORE.get(), RenderType.translucent());

				SkullTileEntityRenderer.MODEL_BY_TYPE.put(GoldenSkullBlock.TYPE, SkullTileEntityRenderer.MODEL_BY_TYPE.get(SkullBlock.Types.SKELETON));
				SkullTileEntityRenderer.SKIN_BY_TYPE.put(GoldenSkullBlock.TYPE, new ResourceLocation(MODID, "textures/entity/golden_skull.png"));
			});
			RenderingRegistry.registerEntityRenderingHandler(ModEntities.WIND.get(), WindEntityRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(ModEntities.ANVIL.get(), AnvilEntityRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(ModEntities.CREEPER_MISSILE.get(), CreeperMissileEntityRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(ModEntities.SUMMONED_ZOMBIE.get(), SummonedZombieRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(ModEntities.SUMMONED_SKELETON.get(), SummonedSkeletonRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(ModEntities.GHOST.get(), GhostEntityRenderer::new);

			ClientRegistry.bindTileEntityRenderer(ModTileEntities.GOLDEN_SKULL.get(), GoldenSkullTileEntityRenderer::new);
		}

		@SubscribeEvent
		public static void onItemColor(ColorHandlerEvent.Item event){
			event.getItemColors().register(new SparkColor(), ModItems.INFERNO_SPARK.get());
			event.getItemColors().register(new GeneratorInfernoSparkColor(), ModItems.GENERATOR_INFERNO_SPARK.get());
			event.getItemColors().register(new PrimalInfernoSparkColor(), ModItems.PRIMAL_INFERNO_SPARK.get());
			event.getItemColors().register(new EssenceHolderBookSparkColor(), ModItems.BOOK_OF_THE_UNSPEAKABLE_COMBINED.get(), ModItems.ESSENCE_HOLDER.get());
		}
	}
}
