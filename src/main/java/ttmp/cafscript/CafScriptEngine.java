package ttmp.cafscript;

import ttmp.cafscript.definitions.InitDefinition;
import ttmp.cafscript.internal.compiler.CafCompiler;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * It's an engine.
 */
public class CafScriptEngine{
	private final Map<String, InitDefinition<?>> knownTypes = new HashMap<>();

	@Nullable private Consumer<Object> printer;

	public Map<String, InitDefinition<?>> getKnownTypes(){
		return knownTypes;
	}

	public CafScriptEngine setDebugPrinter(Consumer<Object> printer){
		this.printer = printer;
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
}
