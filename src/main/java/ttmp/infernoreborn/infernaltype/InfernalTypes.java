package ttmp.infernoreborn.infernaltype;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.entity.LivingEntity;
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
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.commons.io.IOUtils;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.contents.ability.holder.ServerAbilityHolder;
import ttmp.infernoreborn.infernaltype.wtf.AbilitiesInitializer;
import ttmp.infernoreborn.infernaltype.wtf.AbilityGenerationContext;
import ttmp.infernoreborn.infernaltype.wtf.ChooseAbilityInitializer;
import ttmp.infernoreborn.infernaltype.wtf.DeferredAbilityGeneratorInitializer;
import ttmp.infernoreborn.infernaltype.wtf.ImmediateAbilityGeneratorInitializer;
import ttmp.infernoreborn.network.ModNet;
import ttmp.infernoreborn.network.SyncInfernalTypeMsg;
import ttmp.infernoreborn.util.SomeAbility;
import ttmp.wtf.CompileContext;
import ttmp.wtf.WtfScript;
import ttmp.wtf.WtfScriptEngine;
import ttmp.wtf.exceptions.WtfCompileException;
import ttmp.wtf.exceptions.WtfEvalException;
import ttmp.wtf.exceptions.WtfException;
import ttmp.wtf.internal.WtfExecutor;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class InfernalTypes{
	private InfernalTypes(){}

	private static final WtfScriptEngine ENGINE = new WtfScriptEngine()
			.addType("Choose", ChooseAbilityInitializer::new);
	private static final CompileContext COMPILE_CONTEXT = CompileContext.builder()
			.addStaticConstant("NoAbility", SomeAbility.NONE)
			.addDynamicConstant("EntityType", ResourceLocation.class)
			.build();

	private static Map<ResourceLocation, InfernalType> infernalTypes = Collections.emptyMap();
	private static Map<ResourceLocation, WtfScript> generators = Collections.emptyMap();

	/**
	 * Client use only. Don't touch it. I will cut your throat if you fuck with this.
	 *
	 * @deprecated I SAID DON'T
	 */
	@SuppressWarnings("DeprecatedIsStillUsed") // shut up intellij idc
	@Deprecated
	public static void syncInfernalTypes(Collection<InfernalType> infernalTypes){
		if(ServerLifecycleHooks.getCurrentServer()!=null) return;
		Map<ResourceLocation, InfernalType> m = new HashMap<>();
		for(InfernalType t : infernalTypes) m.put(t.getId(), t);
		InfernalTypes.infernalTypes = m;
		generators = Collections.emptyMap();
		InfernoReborn.LOGGER.debug("syncing infernal types: {}",
				InfernalTypes.infernalTypes.keySet().stream().map(Object::toString).collect(Collectors.joining(", ")));
	}

	public static Collection<InfernalType> getInfernalTypes(){
		return infernalTypes.values();
	}
	@Nullable public static InfernalType get(ResourceLocation id){
		return infernalTypes.get(id);
	}

	public static void generate(LivingEntity entity, ServerAbilityHolder holder){
		if(generators.isEmpty()) return;

		AbilityGenerationContext context = new AbilityGenerationContext(entity, holder);
		List<InfernalTypeStuff> list = new ArrayList<>();
		int wgtSum = 0;
		for(Map.Entry<ResourceLocation, WtfScript> e : generators.entrySet()){
			WtfExecutor executor = new WtfExecutor(ENGINE, e.getValue(), context);
			try{
				DeferredAbilityGeneratorInitializer deferredAbilityGeneratorInitializer = executor.execute(new DeferredAbilityGeneratorInitializer());
				if(deferredAbilityGeneratorInitializer.getWeight()>0){
					list.add(new InfernalTypeStuff(e.getKey(), executor, deferredAbilityGeneratorInitializer));
					wgtSum += deferredAbilityGeneratorInitializer.getWeight();
				}
			}catch(WtfEvalException ex){
				InfernoReborn.LOGGER.warn("Script error occurred during initialization of infernal type {}", e.getKey(), ex);
			}
		}

		switch(list.size()){
			case 0:
				return;
			case 1:
				apply(holder, list.get(0));
				return;
			default:
				int r = ENGINE.getRandom().nextInt(wgtSum);
				for(InfernalTypeStuff o : list){
					if((r -= o.deferredAbilityGeneratorInitializer.getWeight())>=0) continue;
					apply(holder, o);
					break;
				}
		}
	}

	private static void apply(ServerAbilityHolder holder, InfernalTypeStuff stuff){
		InfernalType infernalType = infernalTypes.get(stuff.typeId);
		if(infernalType==null) InfernoReborn.LOGGER.warn("Referencing unknown infernal type {}", stuff.typeId);
		else holder.setAppliedInfernalType(infernalType);
		if(stuff.deferredAbilityGeneratorInitializer.hasAbilities()){
			try{
				stuff.executor.execute(new AbilitiesInitializer(stuff.executor.getContext()), stuff.deferredAbilityGeneratorInitializer.getAbilitiesCodepoint());
			}catch(WtfEvalException ex){
				InfernoReborn.LOGGER.warn("Script error occurred during generating of infernal type {}", stuff.typeId, ex);
			}
		}
	}

	public static void generate(LivingEntity entity, ServerAbilityHolder holder, InfernalType type){
		WtfScript script = generators.get(type.getId());
		if(script==null){
			InfernoReborn.LOGGER.error("Cannot generate ability with InfernalType of {} because script is not attached", type);
			return;
		}
		ENGINE.execute(script, new ImmediateAbilityGeneratorInitializer(), new AbilityGenerationContext(entity, holder));
	}

	private static final class InfernalTypeStuff{
		public final ResourceLocation typeId;
		public final WtfExecutor executor;
		public final DeferredAbilityGeneratorInitializer deferredAbilityGeneratorInitializer;

		private InfernalTypeStuff(ResourceLocation typeId, WtfExecutor executor, DeferredAbilityGeneratorInitializer deferredAbilityGeneratorInitializer){
			this.typeId = typeId;
			this.executor = executor;
			this.deferredAbilityGeneratorInitializer = deferredAbilityGeneratorInitializer;
		}
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
					InfernoReborn.LOGGER.warn("Infernal Type {} has valid JSON file but script is invalid or not provided.", id);
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

				String script = IOUtils.toString(reader);
				try{
					return ENGINE.compile(script, COMPILE_CONTEXT);
				}catch(WtfCompileException ex){
					InfernoReborn.LOGGER.error("Couldn't parse script file {} from {} due to compile error", id, res);
					ex.prettyPrint(script, InfernoReborn.LOGGER::error);
				}
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
				InfernoReborn.LOGGER.debug("Reading infernal type {}", id);
				InfernoReborn.LOGGER.debug(script.format());
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
				ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new SyncInfernalTypeMsg(getInfernalTypes()));
			}
		}
	}
}
