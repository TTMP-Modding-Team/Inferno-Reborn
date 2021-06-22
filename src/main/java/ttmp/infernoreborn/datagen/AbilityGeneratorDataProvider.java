package ttmp.infernoreborn.datagen;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.contents.Abilities;
import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.contents.ability.generator.AbilityGenerator;
import ttmp.infernoreborn.contents.ability.generator.AbilityGenerators;
import ttmp.infernoreborn.contents.ability.generator.node.action.Choose;
import ttmp.infernoreborn.contents.ability.generator.node.action.Do;
import ttmp.infernoreborn.contents.ability.generator.node.action.Give;
import ttmp.infernoreborn.contents.ability.generator.node.condition.RollCondition;
import ttmp.infernoreborn.contents.ability.generator.node.variable.RangedInteger;
import ttmp.infernoreborn.contents.ability.generator.scheme.AbilityGeneratorScheme;
import ttmp.infernoreborn.contents.ability.generator.scheme.ItemDisplay;
import ttmp.infernoreborn.contents.ability.generator.scheme.SpecialEffect;
import ttmp.infernoreborn.util.WeightedPool;

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
		WeightedPool<Ability> specialSkins = pool()
				.add(Abilities.MUD_SKIN.get(), 1)
				.add(Abilities.FROZEN_SKIN.get(), 1)
				.add(Abilities.WOOLLY_SKIN.get(), 1)
				.add(Abilities.FUZZY_SKIN.get(), 1)
				.add(Abilities.THORN_SKIN.get(), 1)
				.build();
		WeightedPool<Ability> commonSpecials = pool()
				.add(Abilities.BULLETPROOF.get(), 1)
				.add(Abilities.VAMPIRE.get(), 1)
				.add(Abilities.CROWD_CONTROL.get(), 1)
				.add(Abilities.SURVIVAL_EXPERT.get(), 1)
				.add(Abilities.THE_BRAIN.get(), 1)
				.add(Abilities.TOUGHNESS.get(), 1)
				.add(Abilities.SWIFTNESS.get(), 1)
				.add(Abilities.EVIOLITE_CHANSEY.get(), 1)
				.addAll(specialSkins)
				.build();
		WeightedPool<Ability> rareSpecials = pool()
				.add(Abilities.DESTINY_BOND.get(), 1)
				.add(Abilities.FOCUS.get(), 1)
				.add(Abilities.GUTS.get(), 1)
				.add(Abilities.MAGMA_SKIN.get(), 1)
				.add(Abilities.HEALTH_KIT.get(), 1)
				.add(Abilities.TELEKINESIS.get(), 1)
				.add(Abilities.AETHER_WALKER.get(), 1)
				.add(Abilities.THUNDERBOLT.get(), 1)
				.add(Abilities.THE_RED.get(), 1)
				.add(Abilities.EMPERORS_AURA.get(), 1)
				.build();

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
						new ItemDisplay(0xFFFFFF)),
				20,
				null,
				new Do(
						new Give(pool()
								.add(null, 32)
								.add(Abilities.HEART.get(), 16)
								.add(Abilities.HEART2.get(), 8)
								.add(Abilities.HEART3.get(), 4)
								.add(Abilities.HEART4.get(), 2)
								.add(Abilities.HEART5.get(), 1)
								.build()),
						new Give(pool()
								.add(null, 32)
								.add(Abilities.WOOD_SKIN.get(), 8)
								.add(Abilities.ROCK_SKIN.get(), 4)
								.add(Abilities.IRON_SKIN.get(), 2)
								.build()),
						new Give(commonSpecials, null, new RangedInteger(1, 4))
				)
		));
		consumer.accept(new AbilityGenerator(
				new AbilityGeneratorScheme(
						new ResourceLocation(MODID, "uncommon"),
						SpecialEffect.create(0xFF00FF, 0x00FFFF, 0xFFFF00),
						new ItemDisplay(0xFFFF00)),
				10,
				null,
				new Do(
						new Give(pool()
								.add(null, 1)
								.add(Abilities.HEART.get(), 2)
								.add(Abilities.HEART2.get(), 4)
								.add(Abilities.HEART3.get(), 8)
								.add(Abilities.HEART4.get(), 4)
								.add(Abilities.HEART5.get(), 2)
								.add(Abilities.HEART6.get(), 1)
								.add(Abilities.HEART7.get(), 1)
								.build()),
						new Give(pool()
								.add(null, 64)
								.add(Abilities.WOOD_SKIN.get(), 16)
								.add(Abilities.ROCK_SKIN.get(), 16)
								.add(Abilities.IRON_SKIN.get(), 4)
								.add(Abilities.DIAMOND_SKIN.get(), 1)
								.build()),
						new Give(commonSpecials, null, new RangedInteger(2, 8)),
						new Give(rareSpecials, new RollCondition(.5), new RangedInteger(1, 2))
				)
		));
		consumer.accept(new AbilityGenerator(
				new AbilityGeneratorScheme(
						new ResourceLocation(MODID, "rare"),
						SpecialEffect.create(0xFF00FF),
						new ItemDisplay(0xFF00FF)),
				5,
				null,
				new Do(
						new Give(pool()
								.add(Abilities.HEART2.get(), 4)
								.add(Abilities.HEART3.get(), 8)
								.add(Abilities.HEART4.get(), 16)
								.add(Abilities.HEART5.get(), 32)
								.add(Abilities.HEART6.get(), 8)
								.add(Abilities.HEART7.get(), 2)
								.add(Abilities.HEART8.get(), 1)
								.build()),
						new Give(pool()
								.add(null, 64)
								.add(Abilities.ROCK_SKIN.get(), 16)
								.add(Abilities.IRON_SKIN.get(), 16)
								.add(Abilities.DIAMOND_SKIN.get(), 4)
								.add(Abilities.NETHERITE_SKIN.get(), 1)
								.build()),
						new Give(specialSkins,
								new RollCondition(0.2),
								null),
						new Give(commonSpecials, null, new RangedInteger(4, 8)),
						new Give(rareSpecials, null, new RangedInteger(1, 3))
				)
		));
		WeightedPool<Ability> epicSkin = pool()
				.add(Abilities.IRON_SKIN.get(), 2)
				.add(Abilities.DIAMOND_SKIN.get(), 2)
				.add(Abilities.NETHERITE_SKIN.get(), 1)
				.build();
		consumer.accept(new AbilityGenerator(
				new AbilityGeneratorScheme(
						new ResourceLocation(MODID, "epic"),
						SpecialEffect.create(0x000000),
						new ItemDisplay(0x444444)),
				1,
				null,
				new Choose(
						new Do(
								new Give(Abilities.HEART8.get()),
								new Give(epicSkin),
								new Give(commonSpecials, null, new RangedInteger(10, 12)),
								new Give(rareSpecials, null, new RangedInteger(5, 6))
						),
						new Do(
								new Give(Abilities.HEART9.get()),
								new Give(epicSkin),
								new Give(commonSpecials, null, new RangedInteger(8, 10)),
								new Give(rareSpecials, null, new RangedInteger(4, 6))
						),
						new Do(
								new Give(Abilities.HEART10.get()),
								new Give(epicSkin),
								new Give(commonSpecials, null, new RangedInteger(6, 8)),
								new Give(rareSpecials, null, new RangedInteger(3, 5))
						)
				)
		));
	}

	private static WeightedPool.Builder<Ability> pool(){
		return WeightedPool.builder();
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
