package ttmp.infernoreborn.ability.generator;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttmp.infernoreborn.ability.generator.pool.WeightedPool;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class AbilityGenerators{
	private AbilityGenerators(){}

	private static WeightedPool<AbilityGenerator> generators = new WeightedPool<>(Object2IntMaps.emptyMap());
	private static Set<ResourceLocation> generatorIDs = Collections.emptySet();

	public static WeightedPool<AbilityGenerator> getWeightedPool(){
		return generators;
	}
	public static Set<ResourceLocation> getGeneratorIDs(){
		return generatorIDs;
	}
	/** Client use only. I will cut your throat if you fuck with this */
	public static void setGeneratorIDs(Set<ResourceLocation> generatorIDs){
		AbilityGenerators.generatorIDs = generatorIDs;
	}

	@Mod.EventBusSubscriber(modid = MODID)
	public static final class Listener extends JsonReloadListener{
		public static final String FOLDER = "ability_generators";

		public Listener(){
			super(new GsonBuilder().create(), FOLDER);
		}

		@Override protected void apply(Map<ResourceLocation, JsonElement> map, IResourceManager resourceManager, IProfiler profiler){
			Object2IntMap<AbilityGenerator> m = new Object2IntArrayMap<>();
			for(Entry<ResourceLocation, JsonElement> e : map.entrySet()){
				AbilityGenerator generator = new AbilityGenerator(e.getKey(), e.getValue().getAsJsonObject());
				m.put(generator, generator.getWeight());
			}
			generators = new WeightedPool<>(m);
			generatorIDs = m.keySet().stream()
					.map(AbilityGenerator::getId)
					.collect(Collectors.toSet());
		}

		@SubscribeEvent
		public static void onAddReloadListener(AddReloadListenerEvent event){
			event.addListener(new Listener());
		}
	}
}
