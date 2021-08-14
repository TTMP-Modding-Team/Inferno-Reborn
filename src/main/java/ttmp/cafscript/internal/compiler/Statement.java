package ttmp.cafscript.internal.compiler;

import java.util.List;

public abstract class Statement{
	public final int position;

	public Statement(int position){
		this.position = position;
	}

	public abstract void visit(StatementVisitor visitor);

	public static final class Assign extends Statement{
		public final String property;
		public final Expression value;

		public Assign(int position, String property, Expression value){
			super(position);
			this.property = property;
			this.value = value;
		}

		@Override public void visit(StatementVisitor visitor){
			visitor.visitAssign(this);
		}

		@Override public String toString(){
			return position+":Assign{"+
					"property='"+property+'\''+
					", value="+value+
					'}';
		}
	}

	public static final class AssignLazy extends Statement{
		public final String property;
		public final List<Statement> statements;

		public AssignLazy(int position, String property, List<Statement> statements){
			super(position);
			this.property = property;
			this.statements = statements;
		}

		@Override public void visit(StatementVisitor visitor){
			visitor.visitAssignLazy(this);
		}

		@Override public String toString(){
			return position+":AssignLazy{"+
					"property='"+property+'\''+
					", statements="+statements+
					'}';
		}
	}

	public static final class Define extends Statement{
		public final String property;
		public final Expression value;

		public Define(int position, String property, Expression value){
			super(position);
			this.property = property;
			this.value = value;
		}

		@Override public void visit(StatementVisitor visitor){
			visitor.visitDefine(this);
		}

		@Override public String toString(){
			return position+":Define{"+
					"property='"+property+'\''+
					", value="+value+
					'}';
		}
	}

	public static final class Apply extends Statement{
		public final Expression value;

		public Apply(int position, Expression value){
			super(position);
			this.value = value;
		}

		@Override public void visit(StatementVisitor visitor){
			visitor.visitApply(this);
		}

		@Override public String toString(){
			return position+":Apply{"+
					"value="+value+
					'}';
		}
	}

	public static final class If extends Statement{
		public final Expression condition;
		public final List<Statement> ifThen;
		public final List<Statement> elseThen;

		public If(int position, Expression condition, List<Statement> ifThen, List<Statement> elseThen){
			super(position);
			this.condition = condition;
			this.ifThen = ifThen;
			this.elseThen = elseThen;
		}

		@Override public void visit(StatementVisitor visitor){
			visitor.visitIf(this);
		}

		@Override public String toString(){
			return position+":If{"+
					"condition="+condition+
					", ifThen="+ifThen+
					", elseThen="+elseThen+
					'}';
		}
	}

	public static final class StatementList extends Statement{
		public final List<Statement> statements;

		public StatementList(int position, List<Statement> statements){
			super(position);
			this.statements = statements;
		}

		@Override public void visit(StatementVisitor visitor){
			visitor.visitStatements(this);
		}

		@Override public String toString(){
			return position+":StatementList{"+
					"statements="+statements+
					'}';
		}
	}

	public static final class For extends Statement{
		public final String variable;
		public final Expression collection;
		public final List<Statement> statements;

		public For(int position, String variable, Expression collection, List<Statement> statements){
			super(position);
			this.variable = variable;
			this.collection = collection;
			this.statements = statements;
		}

		@Override public void visit(StatementVisitor visitor){
			visitor.visitFor(this);
		}

		@Override public String toString(){
			return position+":For{"+
					"variable='"+variable+'\''+
					", collection="+collection+
					", statements="+statements+
					'}';
		}
	}

	public static final class Repeat extends Statement{
		public final Expression times;
		public final List<Statement> statements;

		public Repeat(int position, Expression times, List<Statement> statements){
			super(position);
			this.times = times;
			this.statements = statements;
		}

		@Override public void visit(StatementVisitor visitor){
			visitor.visitRepeat(this);
		}

		@Override public String toString(){
			return position+":Repeat{"+
					"times="+times+
					", statements="+statements+
					'}';
		}
	}

	public static final class Debug extends Statement{
		public final Expression value;

		public Debug(int position, Expression value){
			super(position);
			this.value = value;
		}

		@Override public void visit(StatementVisitor visitor){
			visitor.visitDebug(this);
		}

		@Override public String toString(){
			return position+":Debug{"+
					"value="+value+
					'}';
		}
	}
}
