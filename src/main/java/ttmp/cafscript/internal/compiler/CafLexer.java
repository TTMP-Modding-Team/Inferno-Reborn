package ttmp.cafscript.internal.compiler;

import ttmp.cafscript.exceptions.CafCompileException;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CafLexer{
	private static final Map<String, TokenType> RESERVED_WORDS = new HashMap<>();

	static{
		RESERVED_WORDS.put("define", TokenType.DEFINE);
		RESERVED_WORDS.put("true", TokenType.TRUE);
		RESERVED_WORDS.put("false", TokenType.FALSE);
		RESERVED_WORDS.put("if", TokenType.IF);
		RESERVED_WORDS.put("else", TokenType.ELSE);
		RESERVED_WORDS.put("debug", TokenType.DEBUG);
	}

	private static final Pattern COLOR_PATTERN = Pattern.compile("[0-9a-fA-F]{6}");

	private final String script;
	private int charIndex;

	private final List<Token> tokens = new ArrayList<>();
	private int tokenIndex;

	public CafLexer(String script){
		this.script = script;
	}

	private Token next0(){
		if(tokenIndex<tokens.size()){
			Token currentToken = tokens.get(tokenIndex);
			if(currentToken.is(TokenType.EOF)) return currentToken;
			if(++tokenIndex<tokens.size()) return tokens.get(tokenIndex);
			else return readTokenFromSource();
		}else return readTokenFromSource();
	}

	public Token next(){
		while(true){
			Token next = next0();
			if(!next.is(TokenType.BR)) return next;
		}
	}
	public Token next(boolean skipNewline){
		if(skipNewline) return next();
		else return next0();
	}

	public Token current(){
		return tokens.get(tokenIndex);
	}

	public void expectNext(TokenType token, String error){
		expectNext(token, error, true);
	}
	public void expectNext(TokenType token, String error, boolean skipNewline){
		Token t = skipNewline ? next() : next0();
		if(!t.is(token)) throw new CafCompileException(t.start, error);
	}

	public boolean guessNext(TokenType token){
		return guessNext(token, true);
	}
	public boolean guessNext(TokenType token, boolean skipNewline){
		int index = tokenIndex;
		Token t = skipNewline ? next() : next0();
		if(t.is(token)) return true;
		tokenIndex = index;
		return false;
	}

	public boolean guessNext2(TokenType... tokens){
		return guessNext2(true, tokens);
	}
	public boolean guessNext2(boolean skipNewline, TokenType... tokens){
		int index = tokenIndex;
		Token t = skipNewline ? next() : next0();
		for(TokenType token : tokens)
			if(t.is(token)) return true;
		tokenIndex = index;
		return false;
	}

	private Token readTokenFromSource(){
		Token token = readNextToken(!tokens.isEmpty()&&tokens.get(tokens.size()-1).is(TokenType.BR));
		tokens.add(token);
		return token;
	}

	private Token readNextToken(boolean skipIfNewline){
		BEGIN:
		while(true){
			int tokenStart = charIndex;
			char c = charAt(charIndex++);
			switch(c){
				case '\0':
					return new Token(TokenType.EOF, tokenStart);
				case ' ':
				case '\t':
					continue;
				case '\r':
				case '\n':
					if(skipIfNewline) continue BEGIN;
					return new Token(TokenType.BR, tokenStart);
				case '(':
					return new Token(TokenType.L_PAREN, tokenStart, 1);
				case ')':
					return new Token(TokenType.R_PAREN, tokenStart, 1);
				case '{':
					return new Token(TokenType.L_BRACE, tokenStart, 1);
				case '}':
					return new Token(TokenType.R_BRACE, tokenStart, 1);
				case '!':
					return doubleToken('=', tokenStart, TokenType.BANG_EQ, TokenType.BANG);
				case '=':
					return doubleToken('=', tokenStart, TokenType.EQ_EQ, TokenType.EQ);
				case '<':{
					String namespaceLiteral = grabNamespaceLiteral(charIndex-1);
					if(!namespaceLiteral.isEmpty()){
						charIndex += namespaceLiteral.length()-1;
						return new Token(TokenType.NAMESPACE, tokenStart, namespaceLiteral.length());
					}else return doubleToken('=', tokenStart, TokenType.LT_EQ, TokenType.LT);
				}
				case '>':
					return doubleToken('=', tokenStart, TokenType.GT_EQ, TokenType.GT);
				case '.':
					return new Token(TokenType.DOT, tokenStart, 1);
				case ',':
					return new Token(TokenType.COMMA, tokenStart, 1);
				case '&':
					return doubleToken('&', tokenStart, TokenType.AND_AND, TokenType.AND);
				case '|':
					return doubleToken('|', tokenStart, TokenType.OR_OR, TokenType.OR);
				case '?':
					return new Token(TokenType.QUESTION, tokenStart, 1);
				case '+':
					return new Token(TokenType.PLUS, tokenStart, 1);
				case '-':
					return new Token(TokenType.MINUS, tokenStart, 1);
				case '*':
					return new Token(TokenType.STAR, tokenStart, 1);
				case '/':
					if(charAt(charIndex)=='/'){
						while(true){
							char c2 = charAt(++charIndex);
							switch(c2){
								case '\0':
									return new Token(TokenType.EOF, charIndex);
								case '\n':
								case '\r':
									if(skipIfNewline) continue BEGIN;
									return new Token(TokenType.BR, charIndex);
							}
						}
					}else return new Token(TokenType.SLASH, tokenStart, 1);
				case ':':
					return new Token(TokenType.COLON, tokenStart, 1);
				case ';':
					return new Token(TokenType.SEMICOLON, tokenStart, 1);
				case '#':{
					String literal = grabIdentifierLiteral(charIndex);
					if(!checkMatch(COLOR_PATTERN, literal))
						throw new CafCompileException(charIndex-1, "Invalid color '"+literal+"'");
					charIndex += literal.length();
					return new Token(TokenType.COLOR, tokenStart, 1+literal.length());
				}
				default:{
					if(c>='0'&&c<='9'){ // is number
						String literal = grabNumberLiteral(--charIndex);
						if(literal.isEmpty())
							throw new CafCompileException(charIndex, "Invalid number");
						charIndex += literal.length();
						return new Token(TokenType.NUMBER, tokenStart, literal.length());
					}else{
						String literal = grabIdentifierLiteral(--charIndex);
						if(literal.isEmpty())
							throw new CafCompileException(charIndex, "Invalid character '"+c+"'("+Integer.toHexString(c)+")");
						charIndex += literal.length();
						return new Token(RESERVED_WORDS.getOrDefault(literal, TokenType.IDENTIFIER), tokenStart, literal.length());
					}
				}
			}
		}
	}

	private char charAt(int i){
		return script.length()<=i ? '\0' : script.charAt(i);
	}

	private Token doubleToken(char charToCheck, int tokenStart, TokenType doubleType, TokenType singleType){
		if(charAt(charIndex)==charToCheck){
			charIndex++;
			return new Token(doubleType, tokenStart, 2);
		}else return new Token(singleType, tokenStart, 1);
	}

	private String grabIdentifierLiteral(int from){
		int i = from;
		for(; i<script.length(); i++){
			char charAt = script.charAt(i);
			if((charAt<'a'||charAt>'z')&&
					(charAt<'A'||charAt>'Z')&&
					(charAt<'0'||charAt>'9')&&
					charAt!='_') break;
		}
		return script.substring(from, i);
	}

	private String grabNumberLiteral(int from){
		int i = from;
		boolean seenDot = false;
		boolean seenNumber = false;
		for(; i<script.length(); i++){
			char charAt = script.charAt(i);
			if(charAt=='.'){
				if(seenDot) return "";
				else{
					seenDot = true;
					seenNumber = false;
				}
			}else if(charAt<'0'||charAt>'9') break;
			else seenNumber = true;
		}
		return seenNumber ? script.substring(from, i) : "";
	}

	private String grabNamespaceLiteral(int from){
		if(charAt(from)!='<') return "";
		int i = from+1;
		boolean colonSeen = false;
		for(; i<script.length(); i++){
			char charAt = script.charAt(i);
			if(charAt=='>') return i-from==1 ? "" : script.substring(from, i+1);
			else if(charAt==':'){
				if(colonSeen) return "";
				else colonSeen = true;
			}else if(colonSeen ?
					charAt!='_'&&charAt!='-'&&(charAt<'a'||charAt>'z')&&(charAt<'0'||charAt>'9')&&charAt!='/'&&charAt!='.' :
					charAt!='_'&&charAt!='-'&&(charAt<'a'||charAt>'z')&&(charAt<'0'||charAt>'9')&&charAt!='.')
				return "";
		}
		return "";
	}

	@Nullable private Matcher matcher;

	private boolean checkMatch(Pattern pattern, String text){
		if(matcher==null){
			matcher = pattern.matcher(text);
		}else{
			if(matcher.pattern()!=pattern)
				matcher.usePattern(pattern);
			matcher.reset(text);
		}
		return matcher.matches();
	}
}
