package ttmp.cafscript.internal;

import ttmp.cafscript.CafScript;
import ttmp.cafscript.CafScriptEngine;
import ttmp.cafscript.internal.compiler.CafLexer;
import ttmp.cafscript.internal.compiler.CafParser;
import ttmp.cafscript.internal.compiler.Statement;
import ttmp.cafscript.internal.compiler.Token;
import ttmp.cafscript.internal.compiler.TokenType;

public class CafDebugEngine extends CafScriptEngine{
	private final boolean debugToken;
	private final boolean debugSyntaxTree;

	public CafDebugEngine(){
		this(false, false);
	}
	public CafDebugEngine(boolean debugToken, boolean debugSyntaxTree){
		this.debugToken = debugToken;
		this.debugSyntaxTree = debugSyntaxTree;
	}

	@Override public CafScript compile(String script){
		if(debugToken){
			CafLexer lexer = new CafLexer(script);
			debug("TOKENS:");
			for(Token token; !(token = lexer.next(false)).is(TokenType.EOF);)
				debug(token);
			debug("");
		}
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
