package ttmp.infernoreborn.datagen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.contents.ability.generator.AbilityGenerator;
import ttmp.infernoreborn.contents.ability.generator.AbilityGenerators;
import ttmp.infernoreborn.contents.ability.generator.node.action.Action;
import ttmp.infernoreborn.contents.ability.generator.node.action.Do;
import ttmp.infernoreborn.contents.ability.generator.node.action.Give;
import ttmp.infernoreborn.contents.ability.generator.node.variable.WeightedRandomAbility;
import ttmp.infernoreborn.contents.ability.generator.pool.WeightedAbilityPool;
import ttmp.infernoreborn.contents.ability.generator.scheme.AbilityGeneratorScheme;
import ttmp.infernoreborn.contents.ability.generator.scheme.ItemDisplay;
import ttmp.infernoreborn.contents.ability.generator.scheme.SpecialEffect;
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
		consumer.accept(new AbilityGenerator(
				new AbilityGeneratorScheme(
						new ResourceLocation(MODID, "empty"),
						null,
						null
				),
				50,
				null,
				null));
		consumer.accept(new AbilityGenerator(
				new AbilityGeneratorScheme(
						new ResourceLocation(MODID, "common"),
						SpecialEffect.create(0xFFFFFF),
						new ItemDisplay(0xFFFFFF, 0xFFFFFF, 0xFFFFFF)),
				20,
				null,
				new Do(ImmutableList.<Action>builder()
						.add(new Give(new WeightedRandomAbility(
								WeightedAbilityPool.builder()
										.add(null, 32)
										.add(Abilities.HEART.get(), 16)
										.add(Abilities.HEART2.get(), 8)
										.add(Abilities.HEART3.get(), 4)
										.add(Abilities.HEART4.get(), 2)
										.add(Abilities.HEART5.get(), 1)
										.build()
						)))
						.add(new Give(new WeightedRandomAbility(
								WeightedAbilityPool.builder()
										.add(null, 32)
										.add(Abilities.WOOD_SKIN.get(), 8)
										.add(Abilities.ROCK_SKIN.get(), 4)
										.add(Abilities.MUD_SKIN.get(), 1)
										.add(Abilities.FROZEN_SKIN.get(), 1)
										.add(Abilities.WOOLLY_SKIN.get(), 1)
										.add(Abilities.MUD_SKIN.get(), 1)
										.add(Abilities.IRON_SKIN.get(), 2)
										.build()
						)))
						.build())));
		consumer.accept(new AbilityGenerator(
				new AbilityGeneratorScheme(
						new ResourceLocation(MODID, "rare"),
						SpecialEffect.create(0xFF00FF),
						new ItemDisplay(0xFF00FF, 0xFFFFFF, 0xFFFFFF)),
				5,
				null,
				new Do(ImmutableList.<Action>builder()
						.add(new Give(new WeightedRandomAbility(
								WeightedAbilityPool.builder()
										.add(null, 2)
										.add(Abilities.HEART2.get(), 4)
										.add(Abilities.HEART3.get(), 8)
										.add(Abilities.HEART4.get(), 16)
										.add(Abilities.HEART5.get(), 32)
										.add(Abilities.HEART6.get(), 8)
										.add(Abilities.HEART7.get(), 2)
										.add(Abilities.HEART8.get(), 1)
										.build()
						)))
						.add(new Give(new WeightedRandomAbility(
								WeightedAbilityPool.builder()
										.add(null, 64)
										.add(Abilities.ROCK_SKIN.get(), 16)
										.add(Abilities.IRON_SKIN.get(), 16)
										.add(Abilities.DIAMOND_SKIN.get(), 4)
										.add(Abilities.NETHERITE_SKIN.get(), 1)
										.build()
						)))
						.build())));
	}

	@Override public void run(DirectoryCache directoryCache){
		Path output = this.generator.getOutputFolder();
		Set<ResourceLocation> set = Sets.newHashSet();
		Consumer<AbilityGenerator> consumer = (abilityGenerator) -> {
			ResourceLocation id = abilityGenerator.getScheme().getId();
			if(!set.add(id)) throw new IllegalStateException("Duplicate ability generator "+id);
			Path path = createPath(output, id);

			try{
				InfernoReborn.LOGGER.debug("Generating AbilityGenerator {} at {}", id, path);
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
