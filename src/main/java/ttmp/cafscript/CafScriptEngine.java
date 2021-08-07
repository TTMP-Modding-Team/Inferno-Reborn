package ttmp.cafscript;

import ttmp.cafscript.definitions.InitDefinition;
import ttmp.cafscript.internal.compiler.CafCompiler;

import java.util.HashMap;
import java.util.Map;

/**
 * It's an engine.
 */
public class CafScriptEngine{
	private final Map<String, InitDefinition<?>> knownTypes = new HashMap<>();

	public Map<String, InitDefinition<?>> getKnownTypes(){
		return knownTypes;
	}

	public CafScript compile(String script){
		return new CafCompiler(script).parseAndCompile();
	}
}
