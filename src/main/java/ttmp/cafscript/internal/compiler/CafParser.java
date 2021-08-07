package ttmp.cafscript.internal.compiler;

import ttmp.cafscript.exceptions.CafCompileException;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CafParser{
	private final String script;
	private final CafLexer lexer;

	public CafParser(String script){
		this.script = script;
		this.lexer = new CafLexer(script);
	}

	@Nullable public Statement parse(){
		if(lexer.next().is(TokenType.EOF)) return null;
		else return stmt();
	}

	private Statement stmt(){
		Token current = lexer.current();
		switch(current.type){
			case IDENTIFIER:
				return assignPropertyValueStmt();
			case DEFINE:
				return defineStmt();
			case COLON:
				return applyStmt();
			case IF:
				return ifStmt();
			case DEBUG:
				return debugStmt();
			default:
				throw CafCompileException.create(script, current.start, "Invalid statement");
		}
	}

	private Statement assignPropertyValueStmt(){
		int start = lexer.current().start;
		String property = complexIdentifier();
		lexer.expectNext(TokenType.COLON, "Incomplete property assign statement, missing ':'");
		return lexer.next().is(TokenType.L_BRACE) ?
				new Statement.AssignLazy(start, property, block()) :
				new Statement.Assign(start, property, expr());
	}

	private Statement.Define defineStmt(){
		int start = lexer.current().start;
		lexer.expectNext(TokenType.IDENTIFIER, "Invalid define statement, expected an identifier");
		String property = complexIdentifier();
		lexer.expectNext(TokenType.COLON, "Incomplete define statement, missing ':'");
		if(lexer.next().is(TokenType.L_BRACE))
			throw CafCompileException.create(script, lexer.next().start, "Defined properties cannot have constructors without type specified.");
		return new Statement.Define(start, property, expr());
	}

	private Statement.Apply applyStmt(){
		int start = lexer.current().start;
		lexer.next();
		return new Statement.Apply(start, expr());
	}

	private Statement.If ifStmt(){
		int start = lexer.current().start;
		lexer.expectNext(TokenType.L_PAREN, "Invalid if statement, expected '('");
		lexer.next();
		Expression condition = expr();
		lexer.expectNext(TokenType.R_PAREN, "Invalid if statement, expected ')'");

		List<Statement> then, elseThen;

		if(lexer.next().is(TokenType.ELSE)){
			then = Collections.emptyList();
			lexer.next();
			elseThen = ifBody();
		}else{
			then = ifBody();
			if(lexer.guessNext(TokenType.ELSE)){
				lexer.next();
				elseThen = ifBody();
			}else elseThen = Collections.emptyList();
		}
		return new Statement.If(start, condition, then, Objects.requireNonNull(elseThen));
	}

	private Statement.Debug debugStmt(){
		int start = lexer.current().start;
		lexer.next();
		return new Statement.Debug(start, expr());
	}

	private List<Statement> ifBody(){
		if(lexer.current().is(TokenType.L_BRACE)) return block();
		else return Collections.singletonList(stmt());
	}

	private List<Statement> block(){
		List<Statement> statements = new ArrayList<>();
		while(true){
			switch(lexer.next().type){
				case EOF:
					throw CafCompileException.create(script, script.length()-1, "Unterminated code block");
				case R_BRACE:
					return statements;
				default:
					statements.add(stmt());
			}
		}
	}

	private Expression expr(){
		return comma();
	}

	private Expression comma(){
		Expression e = ternary();
		if(!lexer.guessNext(TokenType.COMMA)) return e;
		List<Expression> expressions = new ArrayList<>();
		expressions.add(e);
		while(true){
			lexer.next();
			expressions.add(ternary());
			if(!lexer.guessNext(TokenType.COMMA))
				return new Expression.Comma(expressions);
		}
	}

	private Expression ternary(){
		Expression e = or();
		if(!lexer.guessNext(TokenType.QUESTION)) return e;
		lexer.next();
		Expression ifThen = ternary();
		lexer.expectNext(TokenType.COLON, "Invalid ternary operator, expected ':'");
		Expression elseThen = ternary();
		return new Expression.Ternary(e, ifThen, elseThen);
	}

	private Expression or(){
		Expression e = and();
		while(lexer.guessNext(TokenType.OR_OR)){
			TokenType token = lexer.current().type;
			lexer.next();
			e = new Expression.Binary(token, e, and());
		}
		return e;
	}

	private Expression and(){
		Expression e = eq();
		while(lexer.guessNext(TokenType.AND_AND)){
			TokenType token = lexer.current().type;
			lexer.next();
			e = new Expression.Binary(token, e, eq());
		}
		return e;
	}

	private Expression eq(){
		Expression e = comp();
		while(lexer.guessNext2(TokenType.EQ_EQ, TokenType.BANG_EQ)){
			TokenType token = lexer.current().type;
			lexer.next();
			e = new Expression.Binary(token, e, comp());
		}
		return e;
	}

	private Expression comp(){
		Expression e = term();
		while(lexer.guessNext2(TokenType.LT, TokenType.GT, TokenType.LT_EQ, TokenType.GT_EQ)){
			TokenType token = lexer.current().type;
			lexer.next();
			e = new Expression.Binary(token, e, term());
		}
		return e;
	}

	private Expression term(){
		Expression e = factor();
		while(lexer.guessNext2(TokenType.PLUS, TokenType.MINUS)){
			TokenType token = lexer.current().type;
			lexer.next();
			e = new Expression.Binary(token, e, factor());
		}
		return e;
	}

	private Expression factor(){
		Expression e = unary();
		while(lexer.guessNext2(TokenType.STAR, TokenType.SLASH)){
			TokenType token = lexer.current().type;
			lexer.next();
			e = new Expression.Binary(token, e, unary());
		}
		return e;
	}

	private Expression unary(){
		switch(lexer.current().type){
			case BANG:
				lexer.next();
				return new Expression.Not(unary());
			case MINUS:
				lexer.next();
				return new Expression.Negate(unary());
			case DEBUG:
				lexer.next();
				return new Expression.Debug(unary());
			default:
				return primary();
		}
	}

	private Expression primary(){
		Token current = lexer.current();
		switch(current.type){
			case L_PAREN:{
				lexer.next();
				Expression expr = expr();
				lexer.expectNext(TokenType.R_PAREN, "Invalid expression, expected ')'");
				return expr;
			}
			case TRUE:
				return Expression.Constant.TRUE;
			case FALSE:
				return Expression.Constant.FALSE;
			case NUMBER:
				return new Expression.Number(literalValue(current));
			case NAMESPACE:
				return new Expression.Namespace(literalValue(current));
			case COLOR:
				return new Expression.Color(literalValue(current));
			case IDENTIFIER:{
				String literal = complexIdentifier();
				if(lexer.guessNext(TokenType.L_BRACE)){
					return new Expression.Construct(literal, block());
				}else return new Expression.Identifier(literal);
			}
			default:
				throw CafCompileException.create(script, current.start, "Invalid expression, expected literal");
		}
	}

	private String complexIdentifier(){
		Token current = lexer.current();
		if(!current.is(TokenType.IDENTIFIER))
			throw CafCompileException.create(script, current.start, "Invalid identifier, expected literal");
		StringBuilder b = new StringBuilder(literalValue(current));
		while(lexer.guessNext(TokenType.DOT)){
			lexer.expectNext(TokenType.IDENTIFIER, "Invalid identifier, expected literal");
			b.append('.').append(literalValue(lexer.current()));
		}
		return b.toString();
	}

	private String literalValue(Token token){
		return script.substring(token.start, token.start+token.length);
	}
}
