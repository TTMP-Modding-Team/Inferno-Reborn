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
				throw new CafCompileException(current.start, "Invalid statement");
		}
	}

	private Statement assignPropertyValueStmt(){
		int start = lexer.current().start;
		String property = complexIdentifier();
		lexer.expectNext(TokenType.COLON, "Incomplete property assign statement, missing ':'");
		return lexer.next().is(TokenType.L_BRACE) ?
				new Statement.AssignLazy(start, property, block()) :
				new Statement.Assign(start, property, expr(null));
	}

	private Statement.Define defineStmt(){
		int start = lexer.current().start;
		lexer.expectNext(TokenType.IDENTIFIER, "Invalid define statement, expected an identifier");
		String property = complexIdentifier();
		lexer.expectNext(TokenType.COLON, "Incomplete define statement, missing ':'");
		if(lexer.next().is(TokenType.L_BRACE))
			throw new CafCompileException(lexer.next().start, "Defined properties cannot have constructors without type specified.");
		return new Statement.Define(start, property, expr(null));
	}

	private Statement.Apply applyStmt(){
		int start = lexer.current().start;
		lexer.next();
		return new Statement.Apply(start, expr(null));
	}

	private Statement ifStmt(){
		int start = lexer.current().start;
		lexer.expectNext(TokenType.L_PAREN, "Invalid if statement, expected '('");
		lexer.next();
		Expression condition = expr(Boolean.class);
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
		if(condition.isConstant())
			return new Statement.StatementList(start, condition.expectConstantObject(Boolean.class) ? then : elseThen);
		return new Statement.If(start, condition, then, Objects.requireNonNull(elseThen));
	}

	private Statement.Debug debugStmt(){
		int start = lexer.current().start;
		lexer.next();
		return new Statement.Debug(start, expr(null));
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
					throw new CafCompileException(script.length()-1, "Unterminated code block");
				case R_BRACE:
					return statements;
				default:
					statements.add(stmt());
			}
		}
	}

	private Expression expr(@Nullable Class<?> expectedType){
		Expression expr = comma();
		expr.checkType(expectedType);
		return expr;
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
				return new Expression.Comma(e.position, expressions);
		}
	}

	private Expression ternary(){
		Expression e = or();
		if(!lexer.guessNext(TokenType.QUESTION)) return e;
		lexer.next();
		Expression ifThen = ternary();
		lexer.expectNext(TokenType.COLON, "Invalid ternary operator, expected ':'");
		Expression elseThen = ternary();
		if(e.isConstant())
			return e.expectConstantObject(Boolean.class) ? ifThen : elseThen;
		return new Expression.Ternary(e.position, e, ifThen, elseThen);
	}

	private Expression or(){
		Expression e = and();
		if(!lexer.guessNext(TokenType.OR_OR)) return e;
		Boolean b = e.isConstant() ?
				e.expectConstantObject(Boolean.class) :
				null;
		do{
			lexer.next();
			Expression e2 = and();
			if(b==null){
				e = new Expression.Or(e.position, e, e2);
			}else if(!b){
				e = e2;
				b = e.isConstant() ?
						e.expectConstantObject(Boolean.class) :
						null;
			}
		}while(lexer.guessNext(TokenType.OR_OR));
		return e;
	}

	private Expression and(){
		Expression e = eq();
		if(!lexer.guessNext(TokenType.AND_AND)) return e;
		Boolean b = e.isConstant() ?
				e.expectConstantObject(Boolean.class) :
				null;
		do{
			lexer.next();
			Expression e2 = eq();
			if(b==null){
				e = new Expression.And(e.position, e, e2);
			}else if(b){
				e = e2;
				b = e.isConstant() ?
						e.expectConstantObject(Boolean.class) :
						null;
			}
		}while(lexer.guessNext(TokenType.AND_AND));
		return e;
	}

	private Expression eq(){
		Expression e = comp();
		while(lexer.guessNext2(TokenType.EQ_EQ, TokenType.BANG_EQ)){
			TokenType token = lexer.current().type;
			lexer.next();
			Expression e2 = comp();
			if(e.isConstant()&&e2.isConstant()){
				boolean eq = Objects.equals(e.getConstantObject(), e2.getConstantObject());
				e = new Expression.Bool(e.position, (token==TokenType.EQ_EQ)==eq);
			}else{
				e = token==TokenType.EQ_EQ ?
						new Expression.Eq(e.position, e, e2) :
						new Expression.NotEq(e.position, e, e2);
			}
		}
		return e;
	}

	private Expression comp(){
		Expression e = term();
		if(!lexer.guessNext2(TokenType.LT, TokenType.GT, TokenType.LT_EQ, TokenType.GT_EQ)) return e;
		Double d = e.isConstant() ?
				e.expectConstantObject(Double.class) :
				null;
		do{
			TokenType token = lexer.current().type;
			lexer.next();
			Expression e2 = term();
			if(d!=null&&e2.isConstant()){
				double d2 = e2.expectConstantObject(Double.class);
				boolean r;
				switch(token){
					case LT:
						r = d<d2;
						break;
					case GT:
						r = d>d2;
						break;
					case LT_EQ:
						r = d<=d2;
						break;
					default: // GTEQ
						r = d>=d2;
						break;
				}
				e = new Expression.Bool(e.position, r);

			}else{
				switch(token){
					case LT:
						e = new Expression.Lt(e.position, e, e2);
						break;
					case GT:
						e = new Expression.Gt(e.position, e, e2);
						break;
					case LT_EQ:
						e = new Expression.LtEq(e.position, e, e2);
						break;
					default: // GTEQ
						e = new Expression.GtEq(e.position, e, e2);
						break;
				}
			}
		}while(lexer.guessNext2(TokenType.LT, TokenType.GT, TokenType.LT_EQ, TokenType.GT_EQ));
		return e;
	}

	private Expression term(){
		Expression e = factor();
		if(!lexer.guessNext2(TokenType.PLUS, TokenType.MINUS)) return e;
		Double d = e.isConstant() ?
				e.expectConstantObject(Double.class) :
				null;
		do{
			TokenType token = lexer.current().type;
			lexer.next();
			Expression e2 = factor();
			if(d!=null&&e2.isConstant()){
				double d2 = e2.expectConstantObject(Double.class);
				double r = token==TokenType.PLUS ? d+d2 :
						d-d2;
				e = new Expression.Number(e.position, r);
				d = r;
			}else{
				e = token==TokenType.PLUS ?
						new Expression.Add(e.position, e, e2) :
						new Expression.Subtract(e.position, e, e2);
			}
		}while(lexer.guessNext2(TokenType.PLUS, TokenType.MINUS));
		return e;
	}

	private Expression factor(){
		Expression e = unary();
		if(!lexer.guessNext2(TokenType.STAR, TokenType.SLASH)) return e;
		Double d = e.isConstant() ?
				e.expectConstantObject(Double.class) :
				null;
		do{
			TokenType token = lexer.current().type;
			lexer.next();
			Expression e2 = unary();
			if(d!=null&&e2.isConstant()){
				double d2 = e2.expectConstantObject(Double.class);
				double r = token==TokenType.STAR ? d*d2 :
						d/d2;
				e = new Expression.Number(e.position, r);
				d = r;
			}else{
				e = token==TokenType.STAR ?
						new Expression.Add(e.position, e, e2) :
						new Expression.Subtract(e.position, e, e2);
			}
		}while(lexer.guessNext2(TokenType.STAR, TokenType.SLASH));
		return e;
	}

	private Expression unary(){
		Token current = lexer.current();
		switch(current.type){
			case BANG:{
				lexer.next();
				Expression e = unary();
				return e.isConstant() ?
						new Expression.Bool(current.start, !e.expectConstantObject(Boolean.class)) :
						new Expression.Not(current.start, e);
			}
			case MINUS:{
				lexer.next();
				Expression e = unary();
				return e.isConstant() ?
						new Expression.Number(current.start, -e.expectConstantObject(Double.class)) :
						new Expression.Negate(current.start, e);
			}
			case DEBUG:
				lexer.next();
				return new Expression.Debug(current.start, unary());
			default:
				return primary();
		}
	}

	private Expression primary(){
		Token current = lexer.current();
		switch(current.type){
			case L_PAREN:{
				lexer.next();
				Expression expr = expr(null);
				lexer.expectNext(TokenType.R_PAREN, "Invalid expression, expected ')'");
				return expr;
			}
			case TRUE:
				return new Expression.Bool(current.start, true);
			case FALSE:
				return new Expression.Bool(current.start, false);
			case NUMBER:
				return new Expression.Number(current.start, literalValue(current));
			case NAMESPACE:
				return new Expression.Namespace(current.start, literalValue(current));
			case COLOR:
				return new Expression.Color(current.start, literalValue(current));
			case IDENTIFIER:{
				String literal = complexIdentifier();
				if(lexer.guessNext(TokenType.L_BRACE)){
					return new Expression.Construct(current.start, literal, block());
				}else return new Expression.Identifier(current.start, literal);
			}
			default:
				throw new CafCompileException(current.start, "Invalid expression, expected literal");
		}
	}

	private String complexIdentifier(){
		Token current = lexer.current();
		if(!current.is(TokenType.IDENTIFIER))
			throw new CafCompileException(current.start, "Invalid identifier, expected literal");
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
