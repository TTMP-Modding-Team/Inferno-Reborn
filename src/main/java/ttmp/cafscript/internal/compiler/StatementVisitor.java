package ttmp.cafscript.internal.compiler;

public interface StatementVisitor{
	void visitAssign(Statement.Assign assign);
	void visitAssignLazy(Statement.AssignLazy assignLazy);
	void visitDefine(Statement.Define define);
	void visitApply(Statement.Apply apply);
	void visitIf(Statement.If apply);
}
