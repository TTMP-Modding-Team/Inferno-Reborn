package datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import static ttmp.infernoreborn.InfernoReborn.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Bus.MOD)
public class Datagen{
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
		}
		if(event.includeClient()){
			generator.addProvider(new ItemModelGen(event.getGenerator(), event.getExistingFileHelper()));
			generator.addProvider(new BlockModelGen(event.getGenerator(), event.getExistingFileHelper()));
			generator.addProvider(new McmetaGen(event.getGenerator(), event.getExistingFileHelper()));
		}
	}
}
