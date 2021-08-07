package ttmp.cafscript.internal;

import ttmp.cafscript.CafScript;
import ttmp.cafscript.CafScriptEngine;

public class CafDebugEngine extends CafScriptEngine{
	@Override public CafScript compile(String script){
		CafScript cafScript = super.compile(script);
		debug(cafScript.format());
		return cafScript;
	}
}
