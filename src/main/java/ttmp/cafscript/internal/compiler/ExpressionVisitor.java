package ttmp.cafscript.internal.compiler;

public interface ExpressionVisitor{
	void visitComma(Expression.Comma comma);
	void visitNot(Expression.Not not);
	void visitNegate(Expression.Negate negate);
	void visitBinary(Expression.Binary binary);
	void visitTernary(Expression.Ternary ternary);
	void visitNumber(Expression.Number number);
	void visitNamespace(Expression.Namespace namespace);
	void visitColor(Expression.Color color);
	void visitIdentifier(Expression.Identifier identifier);
	void visitConstruct(Expression.Construct construct);
	void visitConstant(Expression.Constant constant);
	void visitDebug(Expression.Debug debug);
}
