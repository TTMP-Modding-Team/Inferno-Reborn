package ttmp.wtf.internal.compiler;

public enum TokenType{
	// END OF FILE
	EOF,
	// NEWLINE
	BR,
	// ()
	L_PAREN, R_PAREN,
	// {}
	L_BRACE, R_BRACE,
	// ! !=
	BANG, BANG_EQ,
	// =
	EQ,
	// < <=
	LT, LT_EQ,
	// > >=
	GT, GT_EQ,
	// . .. ,
	DOT, DOT_DOT, COMMA,
	// & &&
	AND, AND_AND,
	// | ||
	OR, OR_OR,
	// ?
	QUESTION,
	// ~
	TILDE,
	// + - * /
	PLUS, MINUS, STAR, SLASH,
	// : ; :=
	COLON, SEMICOLON, COLON_EQ,
	// RESERVED SHITS
	TRUE, FALSE, IF, ELSE, DEBUG, FOR, REPEAT, IN, RETURN, THIS, NULL, LOCAL, FN,
	// SHITS
	NUMBER, INT, NAMESPACE, IDENTIFIER, STRING
}
