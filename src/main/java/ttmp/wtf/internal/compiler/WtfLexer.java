package ttmp.wtf.internal.compiler;

import ttmp.wtf.exceptions.WtfCompileException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WtfLexer{
	private static final Map<String, TokenType> RESERVED_WORDS = new HashMap<>();

	static{
		RESERVED_WORDS.put("define", TokenType.DEFINE);
		RESERVED_WORDS.put("true", TokenType.TRUE);
		RESERVED_WORDS.put("false", TokenType.FALSE);
		RESERVED_WORDS.put("if", TokenType.IF);
		RESERVED_WORDS.put("else", TokenType.ELSE);
		RESERVED_WORDS.put("debug", TokenType.DEBUG);
		RESERVED_WORDS.put("for", TokenType.FOR);
		RESERVED_WORDS.put("repeat", TokenType.REPEAT);
		RESERVED_WORDS.put("in", TokenType.IN);
		RESERVED_WORDS.put("return", TokenType.RETURN);
	}

	private final String script;
	private int charIndex;

	private final List<Token> tokens = new ArrayList<>();
	private int tokenIndex;

	public WtfLexer(String script){
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
		if(!t.is(token)) throw new WtfCompileException(t.start, error);
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

	/**
	 * @param token1 First token to match
	 * @param token2 Second token to match
	 * @return 1 if first token matches, 2 if second token matches, 0 if nothing matches
	 */
	public int guessNext2(TokenType token1, TokenType token2){
		return guessNext2(token1, token2, true);
	}
	public int guessNext2(TokenType token1, TokenType token2, boolean skipNewline){
		int index = tokenIndex;
		Token t = skipNewline ? next() : next0();
		if(t.is(token1)) return 1;
		if(t.is(token2)) return 2;
		tokenIndex = index;
		return 0;
	}

	/**
	 * @param token1 First token to match
	 * @param token2 Second token to match
	 * @param token3 Third token to match
	 * @param token4 Fourth token to match
	 * @return 1 if first token matches, 2 if second token matches, 3 if third token matches, 4 if fourth token matches, 0 if nothing matches
	 */
	public int guessNext4(TokenType token1, TokenType token2, TokenType token3, TokenType token4){
		return guessNext4(token1, token2, token3, token4, true);
	}
	public int guessNext4(TokenType token1, TokenType token2, TokenType token3, TokenType token4, boolean skipNewline){
		int index = tokenIndex;
		Token t = skipNewline ? next() : next0();
		if(t.is(token1)) return 1;
		if(t.is(token2)) return 2;
		if(t.is(token3)) return 3;
		if(t.is(token4)) return 4;
		tokenIndex = index;
		return 0;
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
					return new Token(TokenType.EQ, tokenStart, 1);
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
					return doubleToken('.', tokenStart, TokenType.DOT_DOT, TokenType.DOT);
				case ',':
					return new Token(TokenType.COMMA, tokenStart, 1);
				case '&':
					return doubleToken('&', tokenStart, TokenType.AND_AND, TokenType.AND);
				case '|':
					return doubleToken('|', tokenStart, TokenType.OR_OR, TokenType.OR);
				case '?':
					return new Token(TokenType.QUESTION, tokenStart, 1);
				case '~':
					return new Token(TokenType.TILDE, tokenStart, 1);
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
					return doubleToken('=', tokenStart, TokenType.COLON_EQ, TokenType.COLON);
				case ';':
					return new Token(TokenType.SEMICOLON, tokenStart, 1);
				case '"':
				case '\'':{
					String literal = grabStringLiteral(--charIndex);
					charIndex += literal.length();
					return new Token(TokenType.STRING, tokenStart, literal.length());
				}
				default:{
					if(c>='0'&&c<='9'){ // is number
						String literal = grabNumberLiteral(--charIndex);
						if(literal.isEmpty())
							throw new WtfCompileException(charIndex, "Invalid number");
						charIndex += literal.length();
						try{
							Integer.parseInt(literal);
							return new Token(TokenType.INT, tokenStart, literal.length());
						}catch(NumberFormatException ex){
							return new Token(TokenType.NUMBER, tokenStart, literal.length());
						}
					}else{
						String literal = grabIdentifierLiteral(--charIndex);
						if(literal.isEmpty())
							throw new WtfCompileException(charIndex, "Invalid character '"+c+"'("+Integer.toHexString(c)+")");
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
		boolean seenNumber = false;
		int dot = -1;
		for(; i<script.length(); i++){
			char charAt = script.charAt(i);
			if(charAt=='.'){
				if(dot>=0) break;
				else{
					dot = i;
					seenNumber = false;
				}
			}else if(charAt<'0'||charAt>'9') break;
			else seenNumber = true;
		}
		return script.substring(from, seenNumber ? i : dot);
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

	private String grabStringLiteral(int from){
		char terminator = charAt(from);
		for(int i = from+1; i<script.length(); i++){
			char c = script.charAt(i);
			switch(c){
				case '\r':
				case '\n':
					throw new WtfCompileException(i, "Unterminated string literal");
				case '\\':
					++i;
					break;
				default:
					if(c==terminator) return script.substring(from, i+1);
			}
		}
		throw new WtfCompileException(script.length(), "Unterminated string literal");
	}
}
