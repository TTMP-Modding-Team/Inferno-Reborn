package ttmp.cafscript.internal.compiler;

public interface ExpressionVisitor{
	void visitComma(Expression.Comma comma);
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
	void visitOr(Expression.Or or);
	void visitAnd(Expression.And and);
	void visitNumber(Expression.Number number);
	void visitNamespace(Expression.Namespace namespace);
	void visitColor(Expression.Color color);
	void visitIdentifier(Expression.Identifier identifier);
	void visitConstruct(Expression.Construct construct);
	void visitBool(Expression.Bool bool);
	void visitDebug(Expression.Debug debug);
}
