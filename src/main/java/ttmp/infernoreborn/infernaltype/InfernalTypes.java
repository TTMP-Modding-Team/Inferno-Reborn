package ttmp.infernoreborn.infernaltype;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.io.IOUtils;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.wtf.CompileContext;
import ttmp.wtf.WtfScript;
import ttmp.wtf.WtfScriptEngine;
import ttmp.wtf.exceptions.WtfException;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class InfernalTypes{
	private InfernalTypes(){}

	private static final WtfScriptEngine ENGINE = new WtfScriptEngine();
	private static final CompileContext COMPILE_CONTEXT = CompileContext.builder()
			.addDynamicConstant("EntityType", ResourceLocation.class)
			.build();

	private static Map<ResourceLocation, InfernalType> infernalTypes = Collections.emptyMap();
	private static Map<ResourceLocation, WtfScript> generators = Collections.emptyMap();

	public static Collection<InfernalType> getInfernalTypes(){
		return infernalTypes.values();
	}
	@Nullable public static InfernalType get(ResourceLocation id){
		return infernalTypes.get(id);
	}

	@Mod.EventBusSubscriber(modid = MODID)
	public static final class InfernalTypeManager extends ReloadListener<Map<ResourceLocation, Pair<JsonElement, WtfScript>>>{
		private static final Gson GSON = new GsonBuilder().create();
		private static final String DIRECTORY = "infernal_types";

		@Override protected Map<ResourceLocation, Pair<JsonElement, WtfScript>> prepare(IResourceManager m, IProfiler iProfiler){
			Map<ResourceLocation, Pair<JsonElement, WtfScript>> map = Maps.newHashMap();

			for(ResourceLocation r : m.listResources(DIRECTORY, s -> s.endsWith(".json"))){
				String path = r.getPath();
				ResourceLocation id = new ResourceLocation(r.getNamespace(), path.substring(DIRECTORY.length()+1, path.length()-".json".length()));
				if(map.containsKey(id)) throw new IllegalStateException("Duplicate data file ignored with ID "+id);

				JsonElement json = readJson(m, r, id);
				if(json==null) continue;

				WtfScript script = readScript(m, r, id);
				if(script==null){
					InfernoReborn.LOGGER.warn("Infernal Type {} has valid JSON file but invalid/no script attached.", id);
					continue;
				}
				map.put(id, new Pair<>(json, script));
			}

			return map;
		}

		@Nullable private static JsonElement readJson(IResourceManager m, ResourceLocation res, ResourceLocation id){
			try(IResource iresource = m.getResource(res);
			    InputStream inputstream = iresource.getInputStream();
			    Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8))){
				JsonElement json = JSONUtils.fromJson(GSON, reader, JsonElement.class);
				if(json!=null) return json;
				InfernoReborn.LOGGER.error("Couldn't load data file {} from {} as it's null or empty", id, res);
			}catch(IllegalArgumentException|IOException|JsonParseException ex){
				InfernoReborn.LOGGER.error("Couldn't parse data file {} from {}", id, res, ex);
			}
			return null;
		}

		@Nullable private static WtfScript readScript(IResourceManager m, ResourceLocation res, ResourceLocation id){
			String path = res.getPath();
			res = new ResourceLocation(res.getNamespace(), path.substring(0, path.length()-4)+"wtfs");
			try(IResource iresource = m.getResource(res);
			    InputStream inputstream = iresource.getInputStream();
			    Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8))){
				return ENGINE.compile(IOUtils.toString(reader), COMPILE_CONTEXT);
			}catch(IllegalArgumentException|IOException|WtfException ex){
				InfernoReborn.LOGGER.error("Couldn't parse script file {} from {}", id, res, ex);
			}
			return null;
		}

		@Override protected void apply(Map<ResourceLocation, Pair<JsonElement, WtfScript>> resourceLocationPairMap,
		                               IResourceManager iResourceManager,
		                               IProfiler iProfiler){
			ImmutableMap.Builder<ResourceLocation, InfernalType> infernalTypes = ImmutableMap.builder();
			ImmutableMap.Builder<ResourceLocation, WtfScript> generators = ImmutableMap.builder();

			for(Map.Entry<ResourceLocation, Pair<JsonElement, WtfScript>> e : resourceLocationPairMap.entrySet()){
				ResourceLocation id = e.getKey();
				JsonElement json = e.getValue().getFirst();
				WtfScript script = e.getValue().getSecond();
				infernalTypes.put(id, new InfernalType(id, json.getAsJsonObject()));
				generators.put(id, script);
			}

			InfernalTypes.infernalTypes = infernalTypes.build();
			InfernalTypes.generators = generators.build();
			// TODO sync
		}

		@Override public String getName(){
			return "InfernalTypeManager";
		}

		@SubscribeEvent
		public static void onAddReloadListener(AddReloadListenerEvent event){
			event.addListener(new InfernalTypeManager());
		}

		@SubscribeEvent
		public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
			PlayerEntity player = event.getPlayer();
			if(player instanceof ServerPlayerEntity){
				// TODO ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new SyncAbilitySchemeMsg(AbilityGenerators.getSchemes()));
			}
		}
	}
}
