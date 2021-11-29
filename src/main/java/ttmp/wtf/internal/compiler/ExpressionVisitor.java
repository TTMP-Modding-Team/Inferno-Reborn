package ttmp.wtf.internal.compiler;

public interface ExpressionVisitor{
	void visitComma(Expression.Comma comma);
	void visitAppend(Expression.Append append);
	void visitNot(Expression.Not not);
	void visitNegate(Expression.Negate negate);
	void visitTernary(Expression.Ternary ternary);
	void visitEq(Expression.Eq eq);
	void visitNotEq(Expression.NotEq notEq);
	void visitGt(Expression.Gt gt);
	void visitLt(Expression.Lt lt);
	void visitGtEq(Expression.GtEq gtEq);
	void visitLtEq(Expression.LtEq ltEq);
	void visitAdd(Expression.Add add);
	void visitSubtract(Expression.Subtract subtract);
	void visitMultiply(Expression.Multiply multiply);
	void visitDivide(Expression.Divide divide);
	void visitIn(Expression.In in);
	void visitOr(Expression.Or or);
	void visitAnd(Expression.And and);
	void visitRange(Expression.RangeOperator rangeOperator);
	void visitRandomInt(Expression.RandomInt randomInt);
	void visitDynamicAccess(Expression.DynamicAccess dynamicAccess);
	void visitConstantAccess(Expression.ConstantAccess constantAccess);
	void visitConstant(Expression.Constant constant);
	void visitFunction(Expression.Function function);
	void visitConstruct(Expression.Construct construct);
	void visitBool(Expression.Bool bool);
	void visitDebug(Expression.Debug debug);
}
