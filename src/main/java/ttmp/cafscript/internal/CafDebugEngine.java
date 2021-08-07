package ttmp.cafscript.internal;

import ttmp.cafscript.CafScript;
import ttmp.cafscript.CafScriptEngine;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class CafDebugEngine extends CafScriptEngine{
	@Nullable private final Consumer<String> printer;

	public CafDebugEngine(){
		this(null);
	}
	public CafDebugEngine(@Nullable Consumer<String> printer){
		this.printer = printer;
	}

	@Override public CafScript compile(String script){
		CafScript cafScript = super.compile(script);
		if(printer!=null) printer.accept(cafScript.format());
		else System.out.println(cafScript.format());

		return cafScript;
	}
}
