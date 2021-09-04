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
	// = ==
	EQ, EQ_EQ,
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
	// : ;
	COLON, SEMICOLON,
	// RESERVED SHITS
	DEFINE, TRUE, FALSE, IF, ELSE, DEBUG, FOR, REPEAT, IN,
	// SHITS
	NUMBER, INT, NAMESPACE, COLOR, IDENTIFIER, STRING
}
