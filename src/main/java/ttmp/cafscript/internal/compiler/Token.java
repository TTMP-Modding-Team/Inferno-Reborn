package ttmp.cafscript.internal.compiler;

public final class Token{
	public final TokenType type;
	public final int start;
	public final int length;

	public Token(TokenType type, int start){
		this(type, start, 0);
	}
	public Token(TokenType type, int start, int length){
		this.type = type;
		this.start = start;
		this.length = length;
	}

	public boolean is(TokenType type){
		return this.type==type;
	}
}
