package ttmp.cafscript;

import com.google.common.collect.ImmutableMap;
import ttmp.cafscript.definitions.InitDefinition;
import ttmp.cafscript.definitions.StandardDefinitions;
import ttmp.cafscript.internal.compiler.CafCompiler;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

/**
 * It's an engine.
 */
public class CafScriptEngine{
	private static final Random DEFAULT_RANDOM = new Random();
	private static final Map<String, InitDefinition<?>> DEFAULT_DEFINITIONS = ImmutableMap.<String, InitDefinition<?>>builder()
			.put("Number", StandardDefinitions.NUMBER)
			.put("Bool", StandardDefinitions.BOOL)
			.put("Random", StandardDefinitions.RANDOM)
			.put("RandomNumber", StandardDefinitions.RANDOM_NUMBER)
			.build();

	private final Map<String, InitDefinition<?>> knownTypes;

	@Nullable private Consumer<Object> printer;
	@Nullable private Random random;

	public CafScriptEngine(){
		this(true);
	}
	public CafScriptEngine(boolean includeDefaultDefinitions){
		knownTypes = includeDefaultDefinitions ?
				new HashMap<>(DEFAULT_DEFINITIONS) :
				new HashMap<>();
	}

	public Map<String, InitDefinition<?>> getKnownTypes(){
		return knownTypes;
	}

	public CafScriptEngine setDebugPrinter(Consumer<Object> printer){
		this.printer = printer;
		return this;
	}
	public CafScriptEngine setRandom(Random random){
		this.random = random;
		return this;
	}

	public CafScript compile(String script){
		return new CafCompiler(script).parseAndCompile();
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
