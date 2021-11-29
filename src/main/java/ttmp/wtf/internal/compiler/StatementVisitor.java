package ttmp.wtf.internal.compiler;

public interface StatementVisitor{
	void visitAssign(Statement.Assign assign);
	void visitDefine(Statement.Define define);
	void visitApply(Statement.Apply apply);
	void visitIf(Statement.If apply);
	void visitStatements(Statement.StatementList statementList);
	void visitFor(Statement.For forStatement);
	void visitRepeat(Statement.Repeat repeat);
	void visitDebug(Statement.Debug debug);
	void visitExpr(Statement.Expr expr);
}
