package ttmp.infernoreborn.contents.ability.generator;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import ttmp.infernoreborn.contents.ability.generator.pool.WeightedPool;
import ttmp.infernoreborn.contents.ability.generator.scheme.AbilityGeneratorScheme;
import ttmp.infernoreborn.network.ModNet;
import ttmp.infernoreborn.network.SyncAbilitySchemeMsg;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class AbilityGenerators{
	private AbilityGenerators(){}

	private static WeightedPool<AbilityGenerator> generators = new WeightedPool<>(Object2IntMaps.emptyMap());
	private static Set<AbilityGeneratorScheme> schemes = Collections.emptySet();

	public static WeightedPool<AbilityGenerator> getWeightedPool(){
		return generators;
	}
	public static Set<AbilityGeneratorScheme> getSchemes(){
		return schemes;
	}
	/** Client use only. I will cut your throat if you fuck with this */
	public static void setSchemes(Set<AbilityGeneratorScheme> schemes){
		AbilityGenerators.schemes = schemes;
	}

	@Nullable public static AbilityGenerator findGeneratorWithId(ResourceLocation id){
		return generators.getItems().keySet().stream().filter(it -> it.getScheme().getId().equals(id)).findAny().orElse(null);
	}
	@Nullable public static AbilityGeneratorScheme findSchemeWithId(ResourceLocation id){
		return schemes.stream().filter(it -> it.getId().equals(id)).findAny().orElse(null);
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
			schemes = m.keySet().stream()
					.map(AbilityGenerator::getScheme)
					.collect(Collectors.toSet());

			// TODO 싱크
			// MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
			// SyncAbilitySchemeMsg message = new SyncAbilitySchemeMsg(schemes);
			// for(ServerPlayerEntity player : server.getPlayerList().getPlayers()){
			// 	ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
			// }
		}

		@SubscribeEvent
		public static void onAddReloadListener(AddReloadListenerEvent event){
			event.addListener(new Listener());
		}

		@SubscribeEvent
		public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
			PlayerEntity player = event.getPlayer();
			if(player instanceof ServerPlayerEntity){
				ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new SyncAbilitySchemeMsg(AbilityGenerators.getSchemes()));
			}
		}
	}
}
