package ttmp.cafscript.internal;

import ttmp.cafscript.CafScript;
import ttmp.cafscript.CafScriptEngine;
import ttmp.cafscript.internal.compiler.CafParser;
import ttmp.cafscript.internal.compiler.Statement;

public class CafDebugEngine extends CafScriptEngine{
	private final boolean debugSyntaxTree;

	public CafDebugEngine(){
		this(false);
	}
	public CafDebugEngine(boolean debugSyntaxTree){
		this.debugSyntaxTree = debugSyntaxTree;
	}

	@Override public CafScript compile(String script){
		if(debugSyntaxTree){
			CafParser parser = new CafParser(script);
			debug("SYNTAX TREE:");
			for(Statement stmt; (stmt = parser.parse())!=null;)
				debug(stmt);
			debug("");
		}

		long t = System.currentTimeMillis();
		CafScript cafScript = super.compile(script);
		debug("Compile took "+(System.currentTimeMillis()-t)+" ms.");
		debug(cafScript.format());
		return cafScript;
	}
}
