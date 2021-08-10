package ttmp.cafscript.internal.compiler;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import ttmp.cafscript.exceptions.CafCompileException;
import ttmp.cafscript.exceptions.CafException;

import javax.annotation.Nullable;
import java.util.List;

public abstract class Expression{
	public final int position;

	public Expression(int position){
		this.position = position;
	}

	public abstract void visit(ExpressionVisitor visitor);

	/**
	 * @return Whether or not this expression produces known compile time constant.
	 * {@code true} implies {@code getConstantObject() != null}, {@code false} implies {@code getConstantObject() == null}.
	 */
	public boolean isConstant(){
		return false;
	}

	/**
	 * @return Constant object, {@code null} if the expression doesn't evaluate into compile-time constants.<br>
	 * {@code isConstant() == true} implies the object is compile-time constant and thus always has constant object.
	 */
	@Nullable public Object getConstantObject(){
		return null;
	}

	/**
	 * Get constant with type {@code T}. Throws compile error if the expression doesn't produce constant, or the type of constant is not {@code T}.
	 *
	 * @param classOf Class of the constant
	 * @param <T>     Type of the constant
	 * @return Constant of type {@code T}
	 * @throws CafCompileException if the expression doesn't produce constant, or the type of constant is not {@code T}
	 */
	public <T> T expectConstantObject(Class<T> classOf){
		Object o = getConstantObject();
		if(o==null) error("Expected constant");
		if(!classOf.isInstance(o)) error("Invalid expression, expected "+classOf.getSimpleName());
		//noinspection ConstantConditions,unchecked
		return (T)o;
	}

	/**
	 * Checks if the expression evaluates to specific type of object.<br>
	 * If the expression would never evaluate into given {@code expectedType}, {@link CafCompileException} will be thrown.<br>
	 * This method also propagates to child nodes to check any errors in the expression.
	 */
	public abstract void checkType(@Nullable Class<?> expectedType);

	protected void expectType(Class<?> comparingType, @Nullable Class<?> expectedType){
		if(expectedType!=null&&expectedType!=comparingType)
			error("Invalid expression, expected "+expectedType.getSimpleName()+" but provided "+comparingType.getSimpleName());
	}

	protected void error(String message){
		throw new CafCompileException(position, message);
	}

	public static class Comma extends Expression{
		public final List<Expression> expressions;

		public Comma(int position, List<Expression> expressions){
			super(position);
			this.expressions = expressions;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitComma(this);
		}
		@Override public void checkType(@Nullable Class<?> expectedType){
			expectType(Object[].class, expectedType);
		}

		@Override public String toString(){
			return "Comma{"+
					"expressions="+expressions+
					'}';
		}
	}

	public static class Not extends Expression{
		public final Expression expression;

		public Not(int position, Expression expression){
			super(position);
			this.expression = expression;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitNot(this);
		}
		@Override public void checkType(@Nullable Class<?> expectedType){
			expectType(Boolean.class, expectedType);
			expression.checkType(Boolean.class);
		}

		@Override public String toString(){
			return "!"+expression;
		}
	}

	public static class Negate extends Expression{
		public final Expression expression;

		public Negate(int position, Expression expression){
			super(position);
			this.expression = expression;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitNegate(this);
		}
		@Override public void checkType(@Nullable Class<?> expectedType){
			expectType(Double.class, expectedType);
			expression.checkType(Double.class);
		}

		@Override public String toString(){
			return "-"+expression;
		}
	}

	public static abstract class Binary extends Expression{
		public final Expression e1;
		public final Expression e2;

		public Binary(int position, Expression e1, Expression e2){
			super(position);
			this.e1 = e1;
			this.e2 = e2;
		}

		@Override public String toString(){
			return getClass().getSimpleName()+"{"+e1+", "+e2+"}";
		}
	}

	public static abstract class ComparisonOperator extends Binary{
		public ComparisonOperator(int position, Expression e1, Expression e2){
			super(position, e1, e2);
		}

		@Override public void checkType(@Nullable Class<?> expectedType){
			expectType(Boolean.class, expectedType);
			this.e1.checkType(Double.class);
			this.e2.checkType(Double.class);
		}
	}

	public static abstract class ArithmeticOperator extends Binary{
		public ArithmeticOperator(int position, Expression e1, Expression e2){
			super(position, e1, e2);
		}

		@Override public void checkType(@Nullable Class<?> expectedType){
			expectType(Double.class, expectedType);
			this.e1.checkType(Double.class);
			this.e2.checkType(Double.class);
		}
	}

	public static abstract class LogicalOperator extends Binary{
		public LogicalOperator(int position, Expression e1, Expression e2){
			super(position, e1, e2);
		}

		@Override public void checkType(@Nullable Class<?> expectedType){
			expectType(Boolean.class, expectedType);
			this.e1.checkType(Boolean.class);
			this.e2.checkType(Boolean.class);
		}
	}

	public static class Eq extends Binary{
		public Eq(int position, Expression e1, Expression e2){
			super(position, e1, e2);
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitEq(this);
		}
		@Override public void checkType(@Nullable Class<?> expectedType){
			expectType(Boolean.class, expectedType);
		}
	}

	public static class NotEq extends Binary{
		public NotEq(int position, Expression e1, Expression e2){
			super(position, e1, e2);
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitNotEq(this);
		}
		@Override public void checkType(@Nullable Class<?> expectedType){
			expectType(Boolean.class, expectedType);
		}
	}

	public static class Gt extends ComparisonOperator{
		public Gt(int position, Expression e1, Expression e2){
			super(position, e1, e2);
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitGt(this);
		}
	}

	public static class Lt extends ComparisonOperator{
		public Lt(int position, Expression e1, Expression e2){
			super(position, e1, e2);
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitLt(this);
		}
	}

	public static class GtEq extends ComparisonOperator{
		public GtEq(int position, Expression e1, Expression e2){
			super(position, e1, e2);
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitGtEq(this);
		}
	}

	public static class LtEq extends ComparisonOperator{
		public LtEq(int position, Expression e1, Expression e2){
			super(position, e1, e2);
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitLtEq(this);
		}
	}

	public static class Add extends ArithmeticOperator{
		public Add(int position, Expression e1, Expression e2){
			super(position, e1, e2);
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitAdd(this);
		}
	}

	public static class Subtract extends ArithmeticOperator{
		public Subtract(int position, Expression e1, Expression e2){
			super(position, e1, e2);
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitSubtract(this);
		}
	}

	public static class Multiply extends ArithmeticOperator{
		public Multiply(int position, Expression e1, Expression e2){
			super(position, e1, e2);
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitMultiply(this);
		}
	}

	public static class Divide extends ArithmeticOperator{
		public Divide(int position, Expression e1, Expression e2){
			super(position, e1, e2);
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitDivide(this);
		}
	}

	public static class Or extends LogicalOperator{
		public Or(int position, Expression e1, Expression e2){
			super(position, e1, e2);
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitOr(this);
		}
	}

	public static class And extends LogicalOperator{
		public And(int position, Expression e1, Expression e2){
			super(position, e1, e2);
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitAnd(this);
		}
	}

	public static class Ternary extends Expression{
		public final Expression condition;
		public final Expression ifThen;
		public final Expression elseThen;

		public Ternary(int position, Expression condition, Expression ifThen, Expression elseThen){
			super(position);
			this.condition = condition;
			this.ifThen = ifThen;
			this.elseThen = elseThen;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitTernary(this);
		}
		@Override public void checkType(@Nullable Class<?> expectedType){
			this.condition.checkType(Boolean.class);
			this.ifThen.checkType(expectedType);
			this.elseThen.checkType(expectedType);
		}

		@Override public String toString(){
			return "Ternary{"+
					"condition="+condition+
					", ifThen="+ifThen+
					", elseThen="+elseThen+
					'}';
		}
	}

	public static class Number extends Expression{
		public final double number;

		public Number(int position, String number){
			this(position, Double.parseDouble(number));
		}
		public Number(int position, double number){
			super(position);
			this.number = number;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitNumber(this);
		}
		@Override public boolean isConstant(){
			return true;
		}
		@Nullable @Override public Object getConstantObject(){
			return number;
		}
		@Override public void checkType(@Nullable Class<?> expectedType){
			expectType(Double.class, expectedType);
		}
		@Override public String toString(){
			return String.valueOf(number);
		}
	}

	public static class Namespace extends Expression{
		public final ResourceLocation namespace;

		public Namespace(int position, String namespace){
			super(position);
			String substring = namespace.substring(1, namespace.length()-1);
			try{
				this.namespace = new ResourceLocation(substring);
			}catch(ResourceLocationException ex){
				throw new CafException("Invalid namespace '"+substring+"'", ex);
			}
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitNamespace(this);
		}
		@Override public boolean isConstant(){
			return true;
		}
		@Nullable @Override public Object getConstantObject(){
			return namespace;
		}
		@Override public void checkType(@Nullable Class<?> expectedType){
			expectType(ResourceLocation.class, expectedType);
		}
		@Override public String toString(){
			return "<"+namespace+">";
		}
	}

	public static class Color extends Expression{
		public final int rgb;

		public Color(int position, String color){
			super(position);
			String substring = color.substring(1);
			if(substring.length()!=6) throw new CafException("Invalid color '"+color+"'");
			rgb = Integer.parseInt(substring, 16);
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitColor(this);
		}
		@Override public boolean isConstant(){
			return true;
		}
		@Nullable @Override public Object getConstantObject(){
			return rgb;
		}
		@Override public void checkType(@Nullable Class<?> expectedType){
			expectType(Integer.class, expectedType);
		}
		@Override public String toString(){
			return "#"+rgb;
		}
	}

	public static class Identifier extends Expression{
		public final String identifier;

		public Identifier(int position, String identifier){
			super(position);
			this.identifier = identifier;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitIdentifier(this);
		}
		@Override public void checkType(@Nullable Class<?> expectedType){}
		@Override public String toString(){
			return identifier;
		}
	}

	public static class Construct extends Expression{
		public final String identifier;
		public final List<Statement> statements;

		public Construct(int position, String identifier, List<Statement> statements){
			super(position);
			this.identifier = identifier;
			this.statements = statements;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitConstruct(this);
		}
		@Override public void checkType(@Nullable Class<?> expectedType){}
		@Override public String toString(){
			return "Construct{"+
					"identifier='"+identifier+'\''+
					", statements="+statements+
					'}';
		}
	}

	public static class Debug extends Expression{
		public final Expression expression;

		public Debug(int position, Expression expression){
			super(position);
			this.expression = expression;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitDebug(this);
		}
		@Override public void checkType(@Nullable Class<?> expectedType){
			expression.checkType(expectedType);
		}
		@Override public String toString(){
			return "debug "+expression;
		}
	}

	public static class Bool extends Expression{
		public final boolean value;

		public Bool(int position, boolean value){
			super(position);
			this.value = value;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitBool(this);
		}

		@Override public boolean isConstant(){
			return true;
		}
		@Override public Object getConstantObject(){
			return value;
		}
		@Override public void checkType(@Nullable Class<?> expectedType){
			expectType(Boolean.class, expectedType);
		}

		@Override public String toString(){
			return value ? "true" : "false";
		}
	}
}
