package ttmp.wtf.internal.compiler;

import ttmp.wtf.exceptions.WtfCompileException;
import ttmp.wtf.obj.Bundle;
import ttmp.wtf.obj.Range;
import ttmp.wtf.obj.WtfExecutable;

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

	public Object expectConstantObject(){
		Object o = getConstantObject();
		if(o==null) error("Expected constant");
		return o;
	}

	/**
	 * Get constant with type {@code T}. Throws compile error if the expression doesn't produce constant, or the type of constant is not {@code T}.
	 *
	 * @param classOf Class of the constant
	 * @param <T>     Type of the constant
	 * @return Constant of type {@code T}
	 * @throws WtfCompileException if the expression doesn't produce constant, or the type of constant is not {@code T}
	 */
	public <T> T expectConstantObject(Class<T> classOf){
		Object o = expectConstantObject(); // TODO all of type checks are fucked after adding wtfObjects
		if(!classOf.isInstance(o)) error("Invalid expression, expected "+classOf.getSimpleName());
		// noinspection unchecked
		return (T)o;
	}

	/**
	 * Get numeric constant. This is mostly equivalent to {@code expectConstantObject(Number.class)}.
	 */
	public double expectConstantNumber(){
		Object o = expectConstantObject();
		if(!(o instanceof Number)) error("Invalid expression, expected number but provided with "+o.getClass().getSimpleName());
		return ((Number)o).doubleValue();
	}

	/**
	 * Get integer constant. This is mostly equivalent to {@code expectConstantObject(Integer.class)}.
	 */
	public int expectConstantInt(){
		Object o = expectConstantObject();
		if(!(o instanceof Integer)) error("Invalid expression, expected integer but provided with "+o.getClass().getSimpleName());
		return (int)o;
	}

	/**
	 * Get boolean constant. This is mostly equivalent to {@code expectConstantObject(Boolean.class)}.
	 */
	public boolean expectConstantBool(){
		Object o = expectConstantObject();
		if(!(o instanceof Boolean)) error("Invalid expression, expected boolean but provided with "+o.getClass().getSimpleName());
		return (boolean)o;
	}

	/**
	 * Checks if the expression evaluates to specific type of object.<br>
	 * If the expression would never evaluate into given {@code expectedType}, {@link WtfCompileException} will be thrown.<br>
	 * This method also propagates to child nodes to check any errors in the expression.
	 */
	public abstract void checkType(@Nullable Class<?> expectedType);

	protected void expectType(Class<?> comparingType, @Nullable Class<?> expectedType){
		if(expectedType!=null&&!expectedType.isAssignableFrom(comparingType)&&!comparingType.isAssignableFrom(expectedType)) // Check mutually exclusive case
			error("Invalid expression, expected "+expectedType.getSimpleName()+" but provided with "+comparingType.getSimpleName());
	}

	protected final void error(String message){
		throw new WtfCompileException(position, message);
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
			expectType(Bundle.class, expectedType);
		}

		@Override public String toString(){
			return position+":Comma{"+
					"expressions="+expressions+
					'}';
		}
	}

	public static class Append extends Expression{
		public final List<Expression> expressions;

		public Append(int position, List<Expression> expressions){
			super(position);
			this.expressions = expressions;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitAppend(this);
		}
		@Override public void checkType(@Nullable Class<?> expectedType){
			expectType(String.class, expectedType);
		}

		@Override public String toString(){
			return position+":Append{"+
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
			return position+":!"+expression;
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
			expectType(Number.class, expectedType);
			expression.checkType(Number.class);
		}

		@Override public String toString(){
			return position+":-"+expression;
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
			return position+":"+getClass().getSimpleName()+"{"+e1+", "+e2+"}";
		}
	}

	public static abstract class ComparisonOperator extends Binary{
		public ComparisonOperator(int position, Expression e1, Expression e2){
			super(position, e1, e2);
		}

		@Override public void checkType(@Nullable Class<?> expectedType){
			expectType(Boolean.class, expectedType);
			this.e1.checkType(java.lang.Number.class);
			this.e2.checkType(java.lang.Number.class);
		}
	}

	public static abstract class ArithmeticOperator extends Binary{
		public ArithmeticOperator(int position, Expression e1, Expression e2){
			super(position, e1, e2);
		}

		@Override public void checkType(@Nullable Class<?> expectedType){
			expectType(java.lang.Number.class, expectedType);
			this.e1.checkType(java.lang.Number.class);
			this.e2.checkType(java.lang.Number.class);
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

	public static class In extends Binary{
		public In(int position, Expression e1, Expression e2){
			super(position, e1, e2);
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitIn(this);
		}
		@Override public void checkType(@Nullable Class<?> expectedType){
			expectType(Boolean.class, expectedType);
			this.e2.checkType(Iterable.class);
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

	public static class RangeOperator extends Binary{
		public RangeOperator(int position, Expression e1, Expression e2){
			super(position, e1, e2);
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitRange(this);
		}
		@Override public void checkType(@Nullable Class<?> expectedType){
			expectType(Range.class, expectedType);
		}
	}

	public static class RandomInt extends Expression{
		public final Expression a;
		public final Expression b;

		public RandomInt(int position, Expression a, Expression b){
			super(position);
			this.a = a;
			this.b = b;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitRandomInt(this);
		}
		@Override public void checkType(@Nullable Class<?> expectedType){
			expectType(Integer.class, expectedType);
			a.checkType(Integer.class);
			b.checkType(Integer.class);
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
			return position+":Ternary{"+
					"condition="+condition+
					", ifThen="+ifThen+
					", elseThen="+elseThen+
					'}';
		}
	}

	public static class DynamicAccess extends Expression{
		public final String property;

		public DynamicAccess(int position, String property){
			super(position);
			this.property = property;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitDynamicAccess(this);
		}
		@Override public void checkType(@Nullable Class<?> expectedType){}
		@Override public String toString(){
			return position+":DynamicAccess{"+property+'}';
		}
	}

	public static class ConstantAccess extends Expression{
		public final String name;
		@Nullable public final Expression constantExpression;

		public ConstantAccess(int position, String name, @Nullable Expression constantExpression){
			super(position);
			this.name = name;
			this.constantExpression = constantExpression;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitConstantAccess(this);
		}
		@Override public boolean isConstant(){
			return constantExpression!=null&&constantExpression.isConstant();
		}
		@Nullable @Override public Object getConstantObject(){
			return constantExpression!=null ? constantExpression.getConstantObject() : null;
		}
		@Override public void checkType(@Nullable Class<?> expectedType){
			if(constantExpression!=null) constantExpression.checkType(expectedType);
		}
		@Override public String toString(){
			return position+":ConstantAccess{"+name+'}';
		}
	}

	public static class Constant extends Expression{
		public final Object constant;

		public Constant(int position, Object constant){
			super(position);
			this.constant = constant;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitConstant(this);
		}
		@Override public boolean isConstant(){
			return true;
		}
		@Nullable @Override public Object getConstantObject(){
			return constant;
		}
		@Override public void checkType(@Nullable Class<?> expectedType){
			expectType(constant.getClass(), expectedType);
		}
		@Override public String toString(){
			return position+":Constant{"+constant+'}';
		}
	}

	public static class Function extends Expression{
		public final List<String> parameters;
		public final List<Statement> statements;

		public Function(int position, List<String> parameters, List<Statement> statements){
			super(position);
			this.parameters = parameters;
			this.statements = statements;
		}

		@Override public void visit(ExpressionVisitor visitor){
			visitor.visitFunction(this);
		}
		@Override public void checkType(@Nullable Class<?> expectedType){
			expectType(WtfExecutable.class, expectedType);
		}
		@Override public String toString(){
			return position+":Function{"+
					"parameters="+parameters+
					", statements="+statements+
					'}';
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
			return position+":Construct{"+
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
			return position+":debug "+expression;
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
			return position+(value ? ":true" : ":false");
		}
	}
}
