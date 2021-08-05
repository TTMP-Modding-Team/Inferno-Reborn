package ttmp.cafscript;

import ttmp.cafscript.definitions.InitDefinition;
import ttmp.cafscript.internal.compiler.CafCompiler;
import ttmp.cafscript.internal.compiler.CafLexer;
import ttmp.cafscript.internal.compiler.CafParser;
import ttmp.cafscript.internal.compiler.Statement;
import ttmp.cafscript.internal.compiler.Token;
import ttmp.cafscript.internal.compiler.TokenType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * It's an engine.
 */
public class CafScriptEngine{
	private final Map<String, InitDefinition> knownTypes = new HashMap<>();

	public Map<String, InitDefinition> getKnownTypes(){
		return knownTypes;
	}

	public void debugCompile(String script, Consumer<Object> printer){
		CafLexer lexer = new CafLexer(script);
		printer.accept("LEXER:");
		while(true){
			Token next = lexer.next(false);
			if(next.is(TokenType.EOF)) break;
			printer.accept(next.type+": "+script.substring(next.start, next.start+next.length));
		}

		CafParser parser = new CafParser(script);
		printer.accept("");
		printer.accept("PARSER:");
		while(true){
			Statement stmt = parser.parse();
			if(stmt==null) break;
			printer.accept(stmt);
		}

		CafCompiler compiler = new CafCompiler(script);
		CafScript cafScript = compiler.parseAndCompile();
		printer.accept("");
		printer.accept("COMPILER:");
		printer.accept(cafScript.format());
	}
}
