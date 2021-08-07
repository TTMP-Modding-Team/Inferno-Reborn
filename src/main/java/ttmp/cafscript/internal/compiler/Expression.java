package ttmp.cafscript.internal.compiler;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import ttmp.cafscript.exceptions.CafException;

import java.util.List;

public interface Expression{
	void visit(ExpressionVisitor visitor);

	class Comma implements Expression{
		private final List<Expression> expressions;

		public Comma(List<Expression> expressions){
			this.expressions = expressions;
		}

		public List<Expression> getExpressions(){
			return expressions;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitComma(this);
		}

		@Override public String toString(){
			return "Comma{"+
					"expressions="+expressions+
					'}';
		}
	}

	class Not implements Expression{
		private final Expression expression;

		public Not(Expression expression){
			this.expression = expression;
		}

		public Expression getExpression(){
			return expression;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitNot(this);
		}

		@Override public String toString(){
			return "!"+expression;
		}
	}

	class Negate implements Expression{
		private final Expression expression;

		public Negate(Expression expression){
			this.expression = expression;
		}

		public Expression getExpression(){
			return expression;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitNegate(this);
		}

		@Override public String toString(){
			return "-"+expression;
		}
	}

	class Binary implements Expression{
		private final TokenType operator;
		private final Expression e1;
		private final Expression e2;

		public Binary(TokenType operator, Expression e1, Expression e2){
			this.operator = operator;
			this.e1 = e1;
			this.e2 = e2;
		}

		public TokenType getOperator(){
			return operator;
		}
		public Expression getE1(){
			return e1;
		}
		public Expression getE2(){
			return e2;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitBinary(this);
		}

		@Override public String toString(){
			return operator+"{"+e1+", "+e2+"}";
		}
	}

	class Ternary implements Expression{
		private final Expression condition;
		private final Expression ifThen;
		private final Expression elseThen;

		public Ternary(Expression condition, Expression ifThen, Expression elseThen){
			this.condition = condition;
			this.ifThen = ifThen;
			this.elseThen = elseThen;
		}

		public Expression getCondition(){
			return condition;
		}
		public Expression getIfThen(){
			return ifThen;
		}
		public Expression getElseThen(){
			return elseThen;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitTernary(this);
		}

		@Override public String toString(){
			return "Ternary{"+
					"condition="+condition+
					", ifThen="+ifThen+
					", elseThen="+elseThen+
					'}';
		}
	}

	class Number implements Expression{
		private final double number;

		public Number(String number){
			this.number = Double.parseDouble(number);
		}

		public double getNumber(){
			return number;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitNumber(this);
		}

		@Override public String toString(){
			return String.valueOf(number);
		}
	}

	class Namespace implements Expression{
		private final ResourceLocation namespace;

		public Namespace(String namespace){
			String substring = namespace.substring(1, namespace.length()-1);
			try{
				this.namespace = new ResourceLocation(substring);
			}catch(ResourceLocationException ex){
				throw new CafException("Invalid namespace '"+substring+"'", ex);
			}
		}

		public ResourceLocation getNamespace(){
			return namespace;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitNamespace(this);
		}

		@Override public String toString(){
			return "<"+namespace+">";
		}
	}

	class Color implements Expression{
		private final int rgb;

		public Color(String color){
			String substring = color.substring(1);
			if(substring.length()!=6) throw new CafException("Invalid color '"+color+"'");
			rgb = Integer.parseInt(substring, 16);
		}

		public int getRgb(){
			return rgb;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitColor(this);
		}

		@Override public String toString(){
			return "#"+rgb;
		}
	}

	class Identifier implements Expression{
		private final String identifier;

		public Identifier(String identifier){
			this.identifier = identifier;
		}

		public String getIdentifier(){
			return identifier;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitIdentifier(this);
		}

		@Override public String toString(){
			return identifier;
		}
	}

	class Construct implements Expression{
		private final String identifier;
		private final List<Statement> statements;

		public Construct(String identifier, List<Statement> statements){
			this.identifier = identifier;
			this.statements = statements;
		}

		public String getIdentifier(){
			return identifier;
		}
		public List<Statement> getStatements(){
			return statements;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitConstruct(this);
		}

		@Override public String toString(){
			return "Construct{"+
					"identifier='"+identifier+'\''+
					", statements="+statements+
					'}';
		}
	}

	class Debug implements Expression{
		private final Expression expression;

		public Debug(Expression expression){
			this.expression = expression;
		}

		public Expression getExpression(){
			return expression;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitDebug(this);
		}

		@Override public String toString(){
			return "debug "+expression;
		}
	}

	enum Constant implements Expression{
		TRUE,
		FALSE;

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitConstant(this);
		}
	}
}
