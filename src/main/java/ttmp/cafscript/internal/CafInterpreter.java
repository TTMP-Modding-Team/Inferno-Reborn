package ttmp.cafscript.internal;

import ttmp.cafscript.CafScript;
import ttmp.cafscript.CafScriptEngine;
import ttmp.cafscript.definitions.Initializer;

public class CafInterpreter{
	private final CafScriptEngine engine;
	private final CafScript script;
	private final Initializer<?> rootInitializer;

	private int ip;

	public CafInterpreter(CafScriptEngine engine, CafScript script, Initializer<?> rootInitializer){
		this.engine = engine;
		this.script = script;
		this.rootInitializer = rootInitializer;
	}

	public void interpret(){
		// TODO
	}
}
