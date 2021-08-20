package ttmp.wtf.internal;

import ttmp.wtf.WtfScript;
import ttmp.wtf.WtfScriptEngine;
import ttmp.wtf.internal.compiler.WtfLexer;
import ttmp.wtf.internal.compiler.WtfParser;
import ttmp.wtf.internal.compiler.Statement;
import ttmp.wtf.internal.compiler.Token;
import ttmp.wtf.internal.compiler.TokenType;

public class WtfDebugEngine extends WtfScriptEngine{
	private final boolean debugToken;
	private final boolean debugSyntaxTree;

	public WtfDebugEngine(){
		this(false, false);
	}
	public WtfDebugEngine(boolean debugToken, boolean debugSyntaxTree){
		this.debugToken = debugToken;
		this.debugSyntaxTree = debugSyntaxTree;
	}

	@Override public WtfScript compile(String script){
		if(debugToken){
			WtfLexer lexer = new WtfLexer(script);
			debug("TOKENS:");
			for(Token token; !(token = lexer.next(false)).is(TokenType.EOF);)
				debug(token);
			debug("");
		}
		if(debugSyntaxTree){
			WtfParser parser = new WtfParser(script);
			debug("SYNTAX TREE:");
			for(Statement stmt; (stmt = parser.parse())!=null;)
				debug(stmt);
			debug("");
		}

		long t = System.currentTimeMillis();
		WtfScript wtf = super.compile(script);
		debug("Compile took "+(System.currentTimeMillis()-t)+" ms.");
		debug(wtf.format());
		return wtf;
	}
}
