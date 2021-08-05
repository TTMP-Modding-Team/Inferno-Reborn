package ttmp.cafscript.internal.compiler;

import java.util.Collections;
import java.util.List;

public abstract class Statement{
	private final int position;

	public Statement(int position){
		this.position = position;
	}

	public int getPosition(){
		return position;
	}

	public abstract void visit(StatementVisitor visitor);

	public static final class Assign extends Statement{
		private final String property;
		private final Expression value;

		public Assign(int position, String property, Expression value){
			super(position);
			this.property = property;
			this.value = value;
		}

		public String getProperty(){
			return property;
		}
		public Expression getValue(){
			return value;
		}

		@Override public void visit(StatementVisitor visitor){
			visitor.visitAssign(this);
		}

		@Override public String toString(){
			return "Assign{"+
					"property='"+property+'\''+
					", value="+value+
					'}';
		}
	}

	public static final class AssignLazy extends Statement{
		private final String property;
		private final List<Statement> statements;

		public AssignLazy(int position, String property, List<Statement> statements){
			super(position);
			this.property = property;
			this.statements = statements;
		}

		public String getProperty(){
			return property;
		}
		public List<Statement> getStatements(){
			return statements;
		}

		@Override public void visit(StatementVisitor visitor){
			visitor.visitAssignLazy(this);
		}

		@Override public String toString(){
			return "AssignLazy{"+
					"property='"+property+'\''+
					", value="+statements+
					'}';
		}
	}

	public static final class Define extends Statement{
		private final String property;
		private final Expression value;

		public Define(int position, String property, Expression value){
			super(position);
			this.property = property;
			this.value = value;
		}

		public String getProperty(){
			return property;
		}
		public Expression getValue(){
			return value;
		}

		@Override public void visit(StatementVisitor visitor){
			visitor.visitDefine(this);
		}

		@Override public String toString(){
			return "Define{"+
					"property='"+property+'\''+
					", value="+value+
					'}';
		}
	}

	public static final class Apply extends Statement{
		private final Expression value;

		public Apply(int position, Expression value){
			super(position);
			this.value = value;
		}

		public Expression getValue(){
			return value;
		}

		@Override public void visit(StatementVisitor visitor){
			visitor.visitApply(this);
		}

		@Override public String toString(){
			return "Apply{"+
					"value="+value+
					'}';
		}
	}

	public static final class If extends Statement{
		private final Expression condition;
		private final List<Statement> ifThen;
		private final List<Statement> elseThen;

		public If(int position, Expression condition, List<Statement> ifThen){
			this(position, condition, ifThen, Collections.emptyList());
		}
		public If(int position, Expression condition, List<Statement> ifThen, List<Statement> elseThen){
			super(position);
			this.condition = condition;
			this.ifThen = ifThen;
			this.elseThen = elseThen;
		}

		public Expression getCondition(){
			return condition;
		}
		public List<Statement> getIfThen(){
			return ifThen;
		}
		public List<Statement> getElseThen(){
			return elseThen;
		}

		@Override public void visit(StatementVisitor visitor){
			visitor.visitIf(this);
		}

		@Override public String toString(){
			return "If{"+
					"condition="+condition+
					", ifThen="+ifThen+
					", elseThen="+elseThen+
					'}';
		}
	}
}
