package ttmp.infernoreborn.datagen;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.infernaltype.InfernalType;
import ttmp.infernoreborn.infernaltype.ItemDisplay;
import ttmp.infernoreborn.infernaltype.SpecialEffect;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public class InfernalTypeDataProvider implements IDataProvider{
	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.disableHtmlEscaping()
			.create();

	private final DataGenerator generator;

	public InfernalTypeDataProvider(DataGenerator generator){
		this.generator = generator;
	}

	protected void generate(Consumer<InfernalType> consumer){
		consumer.accept(new InfernalType(new ResourceLocation(MODID, "test"),
				SpecialEffect.create(0xFFFFFF), new ItemDisplay(0xFFFFFF)));
	}

	@Override public void run(DirectoryCache directoryCache){
		Path output = this.generator.getOutputFolder();
		Set<ResourceLocation> set = Sets.newHashSet();
		generate((t) -> {
			ResourceLocation id = t.getId();
			if(!set.add(id)) throw new IllegalStateException("Duplicate ability generator "+id);
			Path path = createPath(output, id);
			try{
				InfernoReborn.LOGGER.debug("Generating AbilityGenerator {} at {}", id, path);
				IDataProvider.save(GSON, directoryCache, t.serialize(), path);
			}catch(IOException ex){
				InfernoReborn.LOGGER.error("Couldn't save advancement {}", path, ex);
			}
		});
	}

	@Override public String getName(){
		return "Infernal Types";
	}

	private static Path createPath(Path output, ResourceLocation resourceLocation){
		return output.resolve("data/"+resourceLocation.getNamespace()+"/infernal_types/"+resourceLocation.getPath()+".json");
	}
}
