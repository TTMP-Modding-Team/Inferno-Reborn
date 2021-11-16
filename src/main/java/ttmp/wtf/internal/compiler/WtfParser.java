package ttmp.wtf.internal.compiler;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import ttmp.wtf.CompileContext;
import ttmp.wtf.Wtf;
import ttmp.wtf.exceptions.WtfCompileException;
import ttmp.wtf.exceptions.WtfException;
import ttmp.wtf.obj.Address;
import ttmp.wtf.obj.Bundle;
import ttmp.wtf.obj.RGB;
import ttmp.wtf.obj.Range;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WtfParser{
	private final String script;
	private final CompileContext context;
	private final WtfLexer lexer;

	private final List<Map<String, Definition>> definitions = new ArrayList<>();

	public WtfParser(String script, CompileContext context){
		this.script = script;
		this.context = context;
		this.lexer = new WtfLexer(script);
		pushBlock();
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
			case FOR:
				return forStmt();
			case REPEAT:
				return repeatStmt();
			case DEBUG:
				return debugStmt();
			default:
				throw new WtfCompileException(current.start, "Invalid statement");
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

	private Statement defineStmt(){
		int start = lexer.current().start;
		lexer.expectNext(TokenType.IDENTIFIER, "Invalid define statement, expected an identifier");
		String property = identifier();
		lexer.expectNext(TokenType.COLON, "Incomplete define statement, missing ':'");
		if(lexer.next().is(TokenType.L_BRACE))
			throw new WtfCompileException(lexer.next().start, "Defined properties cannot have constructors without type specified.");
		Expression expr = expr(null);
		setDefinition(property, expr, start);
		return expr.isConstant() ?
				new Statement.StatementList(start, Collections.emptyList()) :
				new Statement.Define(start, property, expr);
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
			elseThen = body();
		}else{
			then = body();
			if(lexer.guessNext(TokenType.ELSE)){
				lexer.next();
				elseThen = body();
			}else elseThen = Collections.emptyList();
		}
		if(condition.isConstant())
			return new Statement.StatementList(start, condition.expectConstantBool() ? then : elseThen);
		return new Statement.If(start, condition, then, Objects.requireNonNull(elseThen));
	}

	private Statement forStmt(){
		int start = lexer.current().start;
		lexer.expectNext(TokenType.L_PAREN, "Invalid for statement, expected '('");
		lexer.expectNext(TokenType.IDENTIFIER, "Invalid for statement, expected identifier");
		String property = literalValue(lexer.current());
		int propertyPosition = lexer.current().start;
		lexer.expectNext(TokenType.IN, "Invalid for statement, expected 'in'");
		lexer.next();
		Expression expr = expr(Iterable.class);
		lexer.expectNext(TokenType.R_PAREN, "Invalid if statement, expected ')'");
		lexer.next();
		List<Statement> body = body(property, propertyPosition);
		return new Statement.For(start, property, expr, body);
	}

	private Statement repeatStmt(){
		int start = lexer.current().start;
		lexer.expectNext(TokenType.L_PAREN, "Invalid repeat statement, expected '('");
		lexer.next();
		Expression times = expr(Integer.class);
		lexer.expectNext(TokenType.R_PAREN, "Invalid repeat statement, expected ')'");
		lexer.next();
		List<Statement> body = body();
		return new Statement.Repeat(start, times, body);
	}

	private Statement.Debug debugStmt(){
		int start = lexer.current().start;
		lexer.next();
		return new Statement.Debug(start, expr(null));
	}

	private List<Statement> body(){
		return body(null, 0);
	}

	private List<Statement> body(@Nullable String variable, int variablePosition){
		pushBlock();
		if(variable!=null) setDefinition(variable, null, variablePosition);
		List<Statement> statements;
		if(lexer.current().is(TokenType.L_BRACE)) statements = block(false);
		else{
			Statement stmt = stmt();
			if(stmt instanceof Statement.Define)
				throw new WtfCompileException(stmt.position, "Only declaration inside block");
			statements = Collections.singletonList(stmt);
		}
		popBlock();
		return statements;
	}

	private List<Statement> block(){
		return block(true);
	}

	private List<Statement> block(boolean push){
		if(push) pushBlock();
		List<Statement> statements = new ArrayList<>();
		while(true){
			switch(lexer.next().type){
				case EOF:
					throw new WtfCompileException(script.length(), "Unterminated code block");
				case R_BRACE:
					if(push) popBlock();
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
		Expression e = stringConjunction();
		if(!lexer.guessNext(TokenType.COMMA)) return e;
		List<Expression> expressions = new ArrayList<>();
		expressions.add(e);
		do{
			lexer.next();
			expressions.add(stringConjunction());
		}while(lexer.guessNext(TokenType.COMMA));
		for(Expression expr : expressions)
			if(!expr.isConstant())
				return new Expression.Comma(e.position, expressions);
		List<Object> list = new ArrayList<>();
		for(Expression expr : expressions) list.add(expr.getConstantObject());
		return new Expression.Constant(e.position, new Bundle(list.toArray()));
	}

	private Expression stringConjunction(){
		Expression e = ternary();
		if(!lexer.guessNext(TokenType.OR)) return e;
		List<Expression> expressions = new ArrayList<>();
		expressions.add(e);
		do{
			lexer.next();
			expressions.add(ternary());
		}while(lexer.guessNext(TokenType.OR));

		for(int i = 1; i<expressions.size(); i++){
			Expression next = expressions.get(i);
			if(next.isConstant()){
				Expression prev = expressions.get(i-1);
				if(prev.isConstant()){
					expressions.set(i-1, new Expression.Constant(
							prev.position,
							String.valueOf(prev.getConstantObject())+next.getConstantObject()));
					expressions.remove(i--);
				}
			}
		}
		switch(expressions.size()){
			case 0:
				throw new IllegalStateException("Internal error, please contact your local pufferfish");
			case 1:
				return expressions.get(0);
			default:
				return new Expression.Append(e.position, expressions);
		}
	}

	private Expression ternary(){
		Expression e = in();
		if(!lexer.guessNext(TokenType.QUESTION)) return e;
		lexer.next();
		Expression ifThen = ternary();
		lexer.expectNext(TokenType.COLON, "Invalid ternary operator, expected ':'");
		lexer.next();
		Expression elseThen = ternary();
		if(e.isConstant()) return e.expectConstantBool() ? ifThen : elseThen;
		return new Expression.Ternary(e.position, e, ifThen, elseThen);
	}

	private Expression in(){
		Expression e = or();
		while(lexer.guessNext(TokenType.IN)){
			lexer.next();
			Expression e2 = or();
			e = e.isConstant()&&e2.isConstant() ?
					new Expression.Bool(e.position, Wtf.isIn(e.expectConstantObject(), e2.expectConstantObject(Iterable.class))) :
					new Expression.In(e.position, e, e2);
		}
		return e;
	}

	private Expression or(){
		Expression e = and();
		while(lexer.guessNext(TokenType.OR_OR)){
			lexer.next();
			Expression e2 = and();
			if(e.isConstant()){
				if(!e.expectConstantBool()) e = e2;
			}else if(e2.isConstant()){
				if(e2.expectConstantBool()) e = e2;
			}else e = new Expression.Or(e.position, e, e2);
		}
		return e;
	}

	private Expression and(){
		Expression e = eq();
		while(lexer.guessNext(TokenType.AND_AND)){
			lexer.next();
			Expression e2 = eq();
			if(e.isConstant()){
				if(e.expectConstantBool()) e = e2;
			}else if(e2.isConstant()){
				if(!e2.expectConstantBool()) e = e2;
			}else e = new Expression.And(e.position, e, e2);
		}
		return e;
	}

	private Expression eq(){
		Expression e = comp();
		while(true){
			int guess = lexer.guessNext2(TokenType.EQ_EQ, TokenType.BANG_EQ);
			if(guess==0) return e;
			lexer.next();
			Expression e2 = comp();
			e = e.isConstant()&&e2.isConstant() ?
					new Expression.Bool(e.position, Wtf.equals(e.expectConstantObject(), e2.expectConstantObject())==(guess==1)) :
					guess==1 ? new Expression.Eq(e.position, e, e2) :
							new Expression.NotEq(e.position, e, e2);
		}
	}

	private Expression comp(){
		Expression e = range();
		while(true){
			int guess = lexer.guessNext4(TokenType.LT, TokenType.GT, TokenType.LT_EQ, TokenType.GT_EQ);
			if(guess==0) return e;
			lexer.next();
			Expression e2 = range();

			if(e.isConstant()&&e2.isConstant()){
				double d = e.expectConstantNumber();
				double d2 = e2.expectConstantNumber();
				e = new Expression.Bool(e.position,
						guess==1 ? d<d2 :
								guess==2 ? d>d2 :
										guess==3 ? d<=d2 :
												d>=d2);
			}else switch(guess){
				case 1:
					e = new Expression.Lt(e.position, e, e2);
					break;
				case 2:
					e = new Expression.Gt(e.position, e, e2);
					break;
				case 3:
					e = new Expression.LtEq(e.position, e, e2);
					break;
				default: // 4
					e = new Expression.GtEq(e.position, e, e2);
			}
		}
	}

	private Expression range(){
		Expression e = random();
		while(lexer.guessNext(TokenType.DOT_DOT)){
			lexer.next();
			Expression e2 = random();
			e = e.isConstant()&&e2.isConstant() ?
					new Expression.Constant(e.position, new Range(e.expectConstantInt(), e2.expectConstantInt())) :
					new Expression.RangeOperator(e.position, e, e2);
		}
		return e;
	}

	private Expression random(){
		Expression e = term();
		while(lexer.guessNext(TokenType.TILDE)){
			lexer.next();
			Expression e2 = term();
			e = new Expression.RandomInt(e.position, e, e2);
		}
		return e;
	}

	private Expression term(){
		Expression e = factor();
		while(true){
			int guess = lexer.guessNext2(TokenType.PLUS, TokenType.MINUS);
			if(guess==0) return e;
			lexer.next();
			Expression e2 = factor();
			if(e.isConstant()&&e2.isConstant()){
				Number a = e.expectConstantObject(Number.class);
				Number b = e2.expectConstantObject(Number.class);
				e = new Expression.Constant(e.position, guess==1 ? Wtf.add(a, b) : Wtf.subtract(a, b));
			}else e = guess==1 ? new Expression.Add(e.position, e, e2) :
					new Expression.Subtract(e.position, e, e2);
		}
	}

	private Expression factor(){
		Expression e = unary();
		while(true){
			int guess = lexer.guessNext2(TokenType.STAR, TokenType.SLASH);
			if(guess==0) return e;
			lexer.next();
			Expression e2 = unary();
			if(e.isConstant()&&e2.isConstant()){
				Number a = e.expectConstantObject(Number.class);
				Number b = e2.expectConstantObject(Number.class);
				try{
					e = new Expression.Constant(e.position, guess==1 ? Wtf.multiply(a, b) : Wtf.divide(a, b));
				}catch(ArithmeticException ex){
					throw new WtfCompileException(e.position, "Divide by zero");
				}
			}else e = guess==1 ? new Expression.Multiply(e.position, e, e2) :
					new Expression.Divide(e.position, e, e2);
		}
	}

	private Expression unary(){
		Token current = lexer.current();
		switch(current.type){
			case BANG:{
				lexer.next();
				Expression e = unary();
				return e.isConstant() ?
						new Expression.Bool(current.start, !e.expectConstantBool()) :
						new Expression.Not(current.start, e);
			}
			case MINUS:{
				lexer.next();
				Expression e = unary();
				return e.isConstant() ?
						new Expression.Constant(current.start, Wtf.negate(e.expectConstantObject(Number.class))) :
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
				return new Expression.Constant(current.start, Double.parseDouble(literalValue(current)));
			case INT:
				return new Expression.Constant(current.start, Integer.parseInt(literalValue(current)));
			case NAMESPACE:{
				String namespace = literalValue(current);
				String substring = namespace.substring(1, namespace.length()-1);
				try{
					return new Expression.Constant(current.start, new ResourceLocation(substring));
				}catch(ResourceLocationException ex){
					throw new WtfException("Invalid namespace '"+substring+"'", ex);
				}
			}
			case COLOR:
				return new Expression.Constant(current.start, new RGB(hex(current.start+1, 6)));
			case STRING:
				return new Expression.Constant(current.start, stringLiteral(current));
			case IDENTIFIER:{
				Address literal = complexIdentifier();
				if(lexer.guessNext(TokenType.L_BRACE)){
					if(literal.size()!=1)
						throw new WtfCompileException(current.start, "Types cannot be addressed using dot(.)");
					return new Expression.Construct(current.start, literal.get(0), block());
				}
				Definition definition = getDefinition(literal);
				if(definition!=null) return definition.constantExpression!=null&&definition.constantExpression.isConstant() ?
						new Expression.Constant(current.start, Objects.requireNonNull(definition.constantExpression.getConstantObject())) :
						new Expression.ConstantAccess(current.start, literal, definition.constantExpression);
				Object staticConstant = context.getStaticConstant(literal);
				if(staticConstant!=null) return new Expression.Constant(current.start, staticConstant);
				Class<?> dynamicConstant = context.getDynamicConstant(literal);
				if(dynamicConstant!=null) return new Expression.DynamicConstant(current.start, literal, dynamicConstant);
				return new Expression.PropertyAccess(current.start, literal);
			}
			default:
				throw new WtfCompileException(current.start, "Invalid expression, expected literal");
		}
	}

	private String identifier(){
		Token current = lexer.current();
		if(!current.is(TokenType.IDENTIFIER))
			throw new WtfCompileException(current.start, "Invalid expression, expected identifier");
		return literalValue(current);
	}

	private Address complexIdentifier(){
		List<String> identifiers = new ArrayList<>();
		identifiers.add(identifier());
		while(lexer.guessNext(TokenType.DOT)){
			lexer.next();
			identifiers.add(identifier());
		}
		return new Address(identifiers.toArray(new String[0]));
	}

	private String literalValue(Token token){
		return script.substring(token.start, token.start+token.length);
	}

	private String stringLiteral(Token token){
		StringBuilder stb = new StringBuilder();
		for(int i = token.start+1; i<token.start+token.length-1; i++){
			char c = script.charAt(i);
			if(c=='\\'){
				char c2 = script.charAt(++i);
				switch(c2){
					case 't':
						stb.append('\t');
						break;
					case 'n':
						stb.append('\n');
						break;
					case '\\':
						stb.append('\\');
						break;
					case '"':
						stb.append('"');
						break;
					case '\'':
						stb.append('\'');
						break;
					case 'u':
						stb.append(Character.toChars(hex(i+1, 4)));
						break;
					default:
						throw new WtfCompileException(i, "Invalid string literal, invalid special character '"+c2+'\'');
				}
			}else stb.append(c);
		}
		return stb.toString();
	}

	private int hex(int i, int digits){
		String substring = script.substring(i, i+digits);
		try{
			return Integer.parseInt(substring, 16);
		}catch(NumberFormatException ex){
			throw new WtfCompileException(i, "Invalid value '"+substring+"'");
		}
	}

	private void setDefinition(String name, @Nullable Expression constantValue, int position){
		Map<String, Definition> m = definitions.get(definitions.size()-1);
		if(m.put(name, new Definition(constantValue))!=null) throw new WtfCompileException(position, "");
	}

	@Nullable private Definition getDefinition(String name){
		for(int i = definitions.size()-1; i>=0; i--){
			Definition d = definitions.get(i).get(name);
			if(d!=null) return d;
		}
		return null;
	}

	private void pushBlock(){
		definitions.add(new HashMap<>());
	}

	private void popBlock(){
		definitions.remove(definitions.size()-1);
	}

	public static final class Definition{
		@Nullable public final Expression constantExpression;

		public Definition(@Nullable Expression constantExpression){
			this.constantExpression = constantExpression;
		}
	}
}
