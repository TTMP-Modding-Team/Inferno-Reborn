package ttmp.wtf;

import com.google.common.collect.ImmutableMap;
import ttmp.wtf.definitions.InitDefinition;
import ttmp.wtf.definitions.StandardDefinitions;
import ttmp.wtf.exceptions.WtfCompileException;
import ttmp.wtf.internal.compiler.WtfCompiler;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

/**
 * It's an engine.
 */
public class WtfScriptEngine{
	private static final Random DEFAULT_RANDOM = new Random();
	private static final Map<String, InitDefinition<?>> DEFAULT_DEFINITIONS = ImmutableMap.<String, InitDefinition<?>>builder()
			.put("Number", StandardDefinitions.NUMBER)
			.put("Bool", StandardDefinitions.BOOL)
			.put("Random", StandardDefinitions.RANDOM)
			.put("RandomNumber", StandardDefinitions.RANDOM_NUMBER)
			.put("Set", StandardDefinitions.SET)
			.build();

	private final Map<String, InitDefinition<?>> knownTypes;

	@Nullable private Consumer<Object> printer;
	@Nullable private Random random;

	public WtfScriptEngine(){
		this(true);
	}
	public WtfScriptEngine(boolean includeDefaultDefinitions){
		knownTypes = includeDefaultDefinitions ?
				new HashMap<>(DEFAULT_DEFINITIONS) :
				new HashMap<>();
	}

	public Map<String, InitDefinition<?>> getKnownTypes(){
		return knownTypes;
	}

	public WtfScriptEngine setDebugPrinter(Consumer<Object> printer){
		this.printer = printer;
		return this;
	}
	public WtfScriptEngine setRandom(Random random){
		this.random = random;
		return this;
	}

	@Nullable public WtfScript tryCompile(String script, ErrorHandler<WtfCompileException> compileExceptionHandler){
		try{
			return compile(script);
		}catch(WtfCompileException ex){
			compileExceptionHandler.handle(ex, this, script);
			return null;
		}
	}

	/**
	 * @throws WtfCompileException on compile error
	 *
	 */
	public WtfScript compile(String script){
		return new WtfCompiler(script).parseAndCompile();
	}

	/**
	 * Debug print
	 */
	public void debug(Object object){
		if(printer!=null) printer.accept(object);
		else System.out.println(object);
	}
	public Random getRandom(){
		return random!=null ? random : DEFAULT_RANDOM;
	}
}
