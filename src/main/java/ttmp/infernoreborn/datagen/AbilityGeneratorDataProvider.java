package ttmp.infernoreborn.datagen;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.ability.generator.AbilityGenerator;
import ttmp.infernoreborn.ability.generator.AbilityGenerators;
import ttmp.infernoreborn.ability.generator.node.action.Give;
import ttmp.infernoreborn.ability.generator.node.variable.ConstantAbility;
import ttmp.infernoreborn.contents.Abilities;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public class AbilityGeneratorDataProvider implements IDataProvider{
	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.disableHtmlEscaping()
			.create();

	private final DataGenerator generator;

	public AbilityGeneratorDataProvider(DataGenerator generator){
		this.generator = generator;
	}

	protected void generate(Consumer<AbilityGenerator> consumer){
		consumer.accept(new AbilityGenerator(new ResourceLocation(MODID, "empty"), 50, null, null, false));
		consumer.accept(new AbilityGenerator(new ResourceLocation(MODID, "test"), 50, null, new Give(new ConstantAbility(Abilities.HEART.get())), true));
	}

	@Override public void run(DirectoryCache directoryCache){
		Path output = this.generator.getOutputFolder();
		Set<ResourceLocation> set = Sets.newHashSet();
		Consumer<AbilityGenerator> consumer = (abilityGenerator) -> {
			if(!set.add(abilityGenerator.getId())) throw new IllegalStateException("Duplicate ability generator "+abilityGenerator.getId());
			Path path = createPath(output, abilityGenerator.getId());

			try{
				InfernoReborn.LOGGER.debug("Generating AbilityGenerator {} at {}", abilityGenerator.getId(), path);
				IDataProvider.save(GSON, directoryCache, abilityGenerator.serialize(), path);
			}catch(IOException ex){
				InfernoReborn.LOGGER.error("Couldn't save advancement {}", path, ex);
			}
		};

		generate(consumer);
	}

	@Override public String getName(){
		return "AbilityGenerators";
	}

	private static Path createPath(Path output, ResourceLocation resourceLocation){
		return output.resolve("data/"+resourceLocation.getNamespace()+"/"+AbilityGenerators.Listener.FOLDER+"/"+resourceLocation.getPath()+".json");
	}
}
