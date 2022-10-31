package ttmp.infernoreborn.infernaltype;

import among.AmongDefinition;
import among.AmongEngine;
import among.CompileResult;
import among.ReadResult;
import among.Report;
import among.RootAndDefinition;
import among.Source;
import among.internals.library.DefaultInstanceProvider;
import among.macro.Macro;
import among.macro.MacroType;
import among.obj.Among;
import among.operator.OperatorType;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.contents.ability.holder.ServerAbilityHolder;
import ttmp.infernoreborn.infernaltype.dsl.abilitygen.AbilityGen;
import ttmp.infernoreborn.infernaltype.dsl.dynamic.Dynamic;
import ttmp.infernoreborn.infernaltype.dsl.effect.InfernalEffect;
import ttmp.infernoreborn.util.Weighted;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public final class InfernalTypes{
	private InfernalTypes(){}

	// This isn't necessary per se, it's just for convenience in debugging
	// because apparently classloading error is ignored by worker thread, and you can't handle it either
	static{
		Dynamic.loadClass();
		AbilityGen.loadClass();
	}

	private static final String INFERNAL_TYPES_FILENAME = "infernal_types";
	private static final String ABILITIES_FILENAME = "abilities";

	private static final Random random = new Random();
	private static final AmongEngine engine = new InfernalTypeConfigEngine();

	private static final ExecutorService configLoadService = Executors.newSingleThreadExecutor(r -> new Thread(r, "Infernal Type Loader"));
	private static volatile boolean configLoadInProgress;
	private static final DecimalFormat fmt = new DecimalFormat("0.##");

	private static List<InfernalType> infernalTypes = Collections.emptyList();

	public static List<InfernalType> getInfernalTypes(){
		return Collections.unmodifiableList(infernalTypes);
	}

	@Nullable public static InfernalType getInfernalType(String name){
		for(InfernalType infernalType : infernalTypes)
			if(name.equals(infernalType.getName())) return infernalType;
		return null;
	}

	private static void copyResource(String resource, Path dest) throws IOException{
		Path p = ModList.get().getModFileById(MODID).getFile().findResource(resource);
		if(!Files.exists(p)){
			InfernoReborn.LOGGER.error("Cannot locate resource {}", resource);
			return;
		}
		InfernoReborn.LOGGER.info("Copying resource {} to path {}", resource, dest);
		Files.createDirectories(dest.getParent());
		Files.copy(p, dest);
	}

	public static void load(){
		load(defaultLogger);
	}
	public static void load(LogHandler logHandler){
		if(configLoadInProgress) return;
		configLoadInProgress = true;
		configLoadService.submit(() -> {
			loadNow(logHandler);
			configLoadInProgress = false;
		});
	}

	public static void loadNow(){
		loadNow(defaultLogger);
	}
	public static void loadNow(LogHandler logHandler){
		logHandler.logInfo("Loading infernal types...");
		long start = System.nanoTime();
		infernalTypes = loadFromConfig(logHandler);
		long elapsed = System.nanoTime()-start;
		logHandler.logInfo(infernalTypes.size()+" infernal type(s) loaded in "+fmt.format(elapsed/100000.0)+" ms");
	}

	private static List<InfernalType> loadFromConfig(LogHandler logHandler){
		try{
			ReadResult rad = engine.readFrom(INFERNAL_TYPES_FILENAME, InfernoReborn.LOGGER::warn);
			engine.clearInstances();
			if(rad.isSuccess()){
				List<InfernalType> infernalTypes = new ArrayList<>();
				for(Among a : rad.root().values()){
					if(a.isObj()){
						InfernalType t = InfernalType.INFERNAL_TYPE.construct(a.asObj(), (type, message, srcIndex, ex, hints) -> {
							Report r = new Report(type, message, srcIndex, ex, hints);
							switch(type){
								case INFO: r.print(rad.source(), logHandler::logInfo); break;
								case WARN: r.print(rad.source(), logHandler::logWarn); break;
								case ERROR: r.print(rad.source(), logHandler::logError); break;
							}
						});
						if(t!=null) infernalTypes.add(t);
					}else logHandler.logWarn("Skipping over non-object value '"+a+"' in infernal generators");
				}
				return infernalTypes;
			}
		}catch(RuntimeException ex){
			logHandler.logError("Cannot continue loading infernal types due to an unexpected exception", ex);
		}
		return Collections.emptyList();
	}

	public static void generate(LivingEntity entity, ServerAbilityHolder holder){
		if(infernalTypes.isEmpty()) return;
		InfernalGenContext context = new InfernalGenContext(entity, holder, random);
		try{
			InfernalType type = Weighted.pick(random, infernalTypes, t -> t.getWeight().evaluateInt(context));
			if(type==null) return;
			generate(context, type);
		}catch(RuntimeException ex){
			InfernoReborn.LOGGER.warn("An error occurred while generating infernal type", ex);
		}
	}

	public static void generate(LivingEntity entity, ServerAbilityHolder holder, InfernalType type){
		generate(new InfernalGenContext(entity, holder, random), type);
	}

	private static void generate(InfernalGenContext context, InfernalType type){
		context.getHolder().clearParticleEffects();
		try{
			for(InfernalEffect e : type.getEffects()) e.apply(context);
			for(Ability ability : type.getAbilityGen().generate(context))
				context.getHolder().add(ability);
		}catch(RuntimeException ex){
			InfernoReborn.LOGGER.warn("An error occurred while generating infernal type {}", type, ex);
		}
	}

	private static final LogHandler defaultLogger = new LogHandler(){
		@Override public void logInfo(String message){
			InfernoReborn.LOGGER.info(message);
		}
		@Override public void logWarn(String message){
			InfernoReborn.LOGGER.warn(message);
		}
		@Override public void logWarn(String message, Throwable exception){
			InfernoReborn.LOGGER.warn(message, exception);
		}
		@Override public void logError(String message){
			InfernoReborn.LOGGER.error(message);
		}
		@Override public void logError(String message, Throwable exception){
			InfernoReborn.LOGGER.error(message, exception);
		}
	};

	public interface LogHandler{
		void logInfo(String message);
		void logWarn(String message);
		void logWarn(String message, Throwable exception);
		void logError(String message);
		void logError(String message, Throwable exception);
	}

	private static class InfernalTypeConfigEngine extends AmongEngine{
		{
			addSourceProvider(path -> {
				Path p = FMLPaths.CONFIGDIR.get().resolve(MODID+"/"+path+".among");
				if(!Files.exists(p)) switch(path){
					case INFERNAL_TYPES_FILENAME: copyResource("config/default_infernal_types.among", p); break;
					case ABILITIES_FILENAME: copyResource("config/default_abilities.among", p); break;
					default: return null;
				}
				return Files.exists(p) ? Source.read(Files.newBufferedReader(p, StandardCharsets.UTF_8)) : null;
			});
		}

		@Override protected void handleSourceResolveException(String path, Exception ex){
			InfernoReborn.LOGGER.warn("An error occurred while resolving '"+path+"': ", ex);
		}
		@Override protected void handleInstanceResolveException(String path, Exception ex){
			InfernoReborn.LOGGER.warn("An error occurred while resolving '"+path+"': ", ex);
		}
		@Override protected void handleCompileSuccess(String path, CompileResult result){
			result.printReports(path, InfernoReborn.LOGGER::info);
		}
		@Override protected void handleCompileError(String path, CompileResult result){
			result.printReports(path, InfernoReborn.LOGGER::warn);
		}

		@Nullable private RootAndDefinition abilityGenerator;
		@Override @Nullable protected RootAndDefinition createDefaultDefinition(String path){
			if(!path.equals(INFERNAL_TYPES_FILENAME)) return null;
			if(abilityGenerator==null){
				abilityGenerator = new RootAndDefinition(DefaultInstanceProvider.defaultOperators());
				AmongDefinition def = abilityGenerator.definition();
				def.operators().addOperator("~", OperatorType.BINARY, "range", 4.5);
				def.operators().addKeyword("weight", OperatorType.BINARY, 0.5);
				def.macros().add(Macro.builder("weight", MacroType.OPERATION)
						.param("element").param("weight")
						.build((args, copyConstant, reportHandler) ->
								Among.namedObject("weighted")
										.prop("element", args[0])
										.prop("weight", args[1])), null);
			}
			return abilityGenerator.copy();
		}
	}
}
