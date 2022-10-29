package ttmp.infernoreborn.infernaltype;

import among.AmongDefinition;
import among.AmongEngine;
import among.CompileResult;
import among.ReadResult;
import among.Report;
import among.ReportType;
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
import org.apache.logging.log4j.Level;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.contents.ability.holder.ServerAbilityHolder;
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

	private static final String ABILITY_GENERATOR_FILENAME = "infernal_generators";
	private static final String ABILITIES_FILENAME = "abilities";

	private static final Random random = new Random();
	private static final AmongEngine engine = new AmongEngine(){
		{
			addSourceProvider(path -> {
				Path p = FMLPaths.CONFIGDIR.get().resolve(MODID+"/"+path+".among");
				if(!Files.exists(p)) switch(path){
					case ABILITY_GENERATOR_FILENAME: copyResource("config/default_infernal_generators.among", p); break;
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
			result.printReports(path, InfernoReborn.LOGGER::debug);
		}
		@Override protected void handleCompileError(String path, CompileResult result){
			result.printReports(path, InfernoReborn.LOGGER::warn);
		}

		@Nullable private RootAndDefinition abilityGenerator;
		@Override @Nullable protected RootAndDefinition createDefaultDefinition(String path){
			if(path.equals(ABILITY_GENERATOR_FILENAME)){
				if(abilityGenerator==null){
					abilityGenerator = new RootAndDefinition(DefaultInstanceProvider.defaultOperators());
					AmongDefinition def = abilityGenerator.definition();
					def.operators().addOperator("~", OperatorType.BINARY, "range", 4.5);
					def.operators().addKeyword("weight", OperatorType.BINARY);
					def.macros().add(Macro.builder("weight", MacroType.OPERATION)
							.param("element").param("weight")
							.build((args, copyConstant, reportHandler) ->
									Among.namedObject("weighted")
											.prop("element", args[0])
											.prop("weight", args[1])), null);
				}
				return abilityGenerator;
			}else return null;
		}
	};

	private static final ExecutorService configLoadService = Executors.newSingleThreadExecutor(r -> new Thread(r, "Infernal Type Loader"));
	private static volatile boolean configLoadInProgress;

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
		if(configLoadInProgress) return;
		configLoadInProgress = true;
		configLoadService.submit(() -> {
			loadNow();
			configLoadInProgress = false;
		});
	}

	private static final DecimalFormat fmt = new DecimalFormat("0.##");
	public static void loadNow(){
		InfernoReborn.LOGGER.info("Loading infernal types...");
		long start = System.nanoTime();
		infernalTypes = loadFromConfig();
		long elapsed = System.nanoTime()-start;
		InfernoReborn.LOGGER.info("{} infernal type(s) loaded in {} ms", infernalTypes.size(), fmt.format(elapsed/100000.0));
	}

	private static List<InfernalType> loadFromConfig(){
		try{
			ReadResult rad = engine.readFrom(ABILITY_GENERATOR_FILENAME, InfernoReborn.LOGGER::warn);
			engine.clearInstances();
			if(rad.isSuccess()){
				List<InfernalType> infernalTypes = new ArrayList<>();
				for(Among a : rad.root().values()){
					if(a.isObj()){
						InfernalType t = InfernalType.INFERNAL_TYPE.construct(a.asObj(), (type, message, srcIndex, ex, hints) -> {
							Report r = new Report(type, message, srcIndex, ex, hints);
							r.print(rad.source(), s -> InfernoReborn.LOGGER.log(
									type==ReportType.ERROR ? Level.ERROR :
											type==ReportType.WARN ? Level.WARN :
													Level.INFO, s));
						});
						if(t!=null) infernalTypes.add(t);
					}else InfernoReborn.LOGGER.warn("Skipping over non-object value '"+a+"' in infernal generators");
				}
				return infernalTypes;
			}
		}catch(RuntimeException ex){
			InfernoReborn.LOGGER.error("Cannot continue loading infernal types due to an unexpected exception", ex);
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
		try{
			for(InfernalEffect e : type.getEffects())
				e.apply(context);
			if(type.getAbilityGen()!=null)
				for(Ability ability : type.getAbilityGen().generate(context))
					context.getHolder().add(ability);
		}catch(RuntimeException ex){
			InfernoReborn.LOGGER.warn("An error occurred while generating infernal type {}", type, ex);
		}
	}
}
