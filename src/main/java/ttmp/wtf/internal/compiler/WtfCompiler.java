package ttmp.wtf.internal.compiler;

import ttmp.wtf.CompileContext;
import ttmp.wtf.WtfScript;
import ttmp.wtf.exceptions.WtfCompileException;
import ttmp.wtf.internal.Inst;
import ttmp.wtf.internal.Lines;
import ttmp.wtf.obj.WtfExecutable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class WtfCompiler extends InstWriter implements StatementVisitor, ExpressionVisitor{
	private final CompileContext context;

	private final List<Block> blocks = new ArrayList<>();

	private Lines.Builder lines;

	private int currentStack;
	private int maxStack;

	private int currentSourcePosition;

	public WtfCompiler(CompileContext context){
		this.context = context;
	}

	public WtfScript parseAndCompile(String script){
		this.inst.clear();
		this.blocks.clear();
		this.lines = new Lines.Builder();
		this.currentStack = 0;
		this.maxStack = 0;
		this.currentSourcePosition = 0;
		pushBlock();
		WtfParser parser = new WtfParser(script, context);
		while(true){
			Statement s = parser.parse();
			if(s==null) break;
			writeInst(s);
		}
		popBlock();
		write(Inst.END);

		Lines lines = this.lines.build(script, inst.size());
		return new WtfScript(context.getEngine(),
				getInstructions(),
				lines);
	}

	private short getJumpCoord(int from){
		return getJumpCoord(from, getNextWritePoint());
	}
	private short getJumpCoord(int from, int to){
		int incr = to-from;
		if(incr!=(short)incr) error("Jump out of range");
		return (short)incr;
	}

	public void writeInst(Statement stmt){
		lines.line(inst.size(), stmt.position);
		int c = this.currentSourcePosition;
		this.currentSourcePosition = stmt.position;
		stmt.visit(this);
		this.currentSourcePosition = c;
	}

	public void writeInst(Expression expr){
		lines.line(inst.size(), expr.position);
		int c = this.currentSourcePosition;
		this.currentSourcePosition = expr.position;
		expr.visit(this);
		this.currentSourcePosition = c;
	}

	@Override public void visitAssign(Statement.Assign assign){
		writeInst(assign.object);
		writeInst(assign.value);
		write(Inst.SET_PROPERTY);
		write(identifier(assign.property));
		removeStack(2);
	}
	@Override public void visitLocalDecl(Statement.LocalDecl localDecl){
		Expression e = localDecl.value;
		if(e.isConstant()) newConstantLocal(localDecl.name, e.getConstantObject());
		else{
			writeInst(e);
			newLocal(localDecl.name);
		}
	}
	@Override public void visitFnDecl(Statement.FnDecl fnDecl){

	}
	@Override public void visitApply(Statement.Apply apply){
		write(Inst.THIS);
		addStack();
		writeInst(apply.value);
		write(Inst.APPLY);
		removeStack(2);
	}
	@Override public void visitIf(Statement.If apply){
		writeInst(apply.condition);
		if(apply.ifThen.isEmpty()&&apply.elseThen.isEmpty()){
			write(Inst.DISCARD);
			removeStack();
			return;
		}
		write(Inst.JUMPELSE);
		removeStack();
		int goElse = getNextWritePoint();
		write2((short)0);

		pushBlock();
		for(Statement s : apply.ifThen) writeInst(s);
		popBlock();

		if(!apply.elseThen.isEmpty()){
			write(Inst.JUMP);
			int ifEnd = getNextWritePoint();
			write2((short)0);
			write2At(goElse, getJumpCoord(goElse));

			pushBlock();
			for(Statement s : apply.elseThen) writeInst(s);
			popBlock();

			write2At(ifEnd, getJumpCoord(ifEnd));
		}else{
			write2At(goElse, getJumpCoord(goElse));
		}
	}
	@Override public void visitStatements(Statement.StatementList statementList){
		for(Statement s : statementList.statements) writeInst(s);
	}
	@Override public void visitFor(Statement.For forStatement){
		writeInst(forStatement.collection);
		write(Inst.MAKE_ITERATOR);
		int p = getNextWritePoint();
		write(Inst.JUMP_OR_NEXT);
		int p2 = getNextWritePoint();
		write2((short)0);
		addStack();

		pushBlock();
		newLocal(forStatement.variable);

		for(Statement s : forStatement.statements) writeInst(s);
		popBlock();

		write(Inst.JUMP);
		write2(getJumpCoord(getNextWritePoint(), p));
		write2At(p2, getJumpCoord(p2));
		removeStack();
	}
	@Override public void visitRepeat(Statement.Repeat repeat){
		if(repeat.times.isConstant()){
			int times = repeat.times.expectConstantInt();
			if(times<5){
				for(int i = 0; i<times; i++){
					pushBlock();
					for(Statement statement : repeat.statements)
						writeInst(statement);
					popBlock();
				}
				return;
			}
		}
		writeInst(repeat.times);
		{
			int pStart = getNextWritePoint();
			write(Inst.JUMP_IF_LT1);
			int pToEnd = getNextWritePoint();
			write2((short)0);

			pushBlock();
			for(Statement statement : repeat.statements)
				writeInst(statement);
			popBlock();

			write(Inst.SUB1);
			write(Inst.JUMP);
			write2(getJumpCoord(getNextWritePoint(), pStart));

			write2At(pToEnd, getJumpCoord(pToEnd));
		}
		write(Inst.DISCARD);
		removeStack();
	}
	@Override public void visitReturn(Statement.Return aReturn){

	}
	@Override public void visitDebug(Statement.Debug debug){
		writeInst(debug.value);
		write(Inst.DEBUG);
		write(Inst.DISCARD);
		removeStack();
	}
	@Override public void visitExpr(Statement.Expr expr){
		writeInst(expr.expr);
		write(Inst.DISCARD);
	}

	@Override public void visitComma(Expression.Comma comma){
		int size = comma.expressions.size();
		for(Expression e : comma.expressions)
			writeInst(e);
		switch(size){
			case 0:
			case 1:
				error("Cannot bundle "+size+" elements");
			case 2:
				write(Inst.BUNDLE2);
				break;
			case 3:
				write(Inst.BUNDLE3);
				break;
			case 4:
				write(Inst.BUNDLE4);
				break;
			default:
				if(size>255) error("Bundle too large");
				write(Inst.BUNDLEN);
				write((byte)size);
		}
		removeStack(size-1);
	}
	@Override public void visitAppend(Expression.Append append){
		int size = append.expressions.size();
		for(Expression e : append.expressions) writeInst(e);
		switch(size){
			case 0:
			case 1:
				error("Cannot append "+size+" elements");
			case 2:
				write(Inst.APPEND2);
				break;
			case 3:
				write(Inst.APPEND3);
				break;
			case 4:
				write(Inst.APPEND4);
				break;
			default:
				if(size>255) error("Append too large");
				write(Inst.APPENDN);
				write((byte)size);
		}
		removeStack(size-1);
	}
	@Override public void visitNot(Expression.Not not){
		writeInst(not.expression);
		write(Inst.NOT);
	}
	@Override public void visitNegate(Expression.Negate negate){
		writeInst(negate.expression);
		write(Inst.NEGATE);
	}
	@Override public void visitTernary(Expression.Ternary ternary){
		writeInst(ternary.condition);
		write(Inst.JUMPELSE);
		removeStack();
		int p = getNextWritePoint();
		write2((short)0);
		writeInst(ternary.ifThen);
		write(Inst.JUMP);
		int p2 = getNextWritePoint();
		write2((short)0);
		write2At(p, getJumpCoord(p));
		removeStack();
		writeInst(ternary.elseThen);
		write2At(p2, getJumpCoord(p2));
	}
	private void writeSimpleBinary(Expression.Binary binary, byte inst){
		writeInst(binary.e1);
		writeInst(binary.e2);
		write(inst);
		removeStack();
	}
	@Override public void visitEq(Expression.Eq eq){
		writeSimpleBinary(eq, Inst.EQ);
	}
	@Override public void visitNotEq(Expression.NotEq notEq){
		writeSimpleBinary(notEq, Inst.NEQ);
	}
	@Override public void visitGt(Expression.Gt gt){
		writeSimpleBinary(gt, Inst.GT);
	}
	@Override public void visitLt(Expression.Lt lt){
		writeSimpleBinary(lt, Inst.LT);
	}
	@Override public void visitGtEq(Expression.GtEq gtEq){
		writeSimpleBinary(gtEq, Inst.GTEQ);
	}
	@Override public void visitLtEq(Expression.LtEq ltEq){
		writeSimpleBinary(ltEq, Inst.LTEQ);
	}
	@Override public void visitAdd(Expression.Add add){
		if(add.e1.isConstant()){
			double v = add.e1.expectConstantNumber();
			if(v==1){
				writeInst(add.e2);
				write(Inst.ADD1);
				return;
			}else if(v==0){
				writeInst(add.e2);
				return;
			}
		}else if(add.e2.isConstant()){
			double v = add.e2.expectConstantNumber();
			if(v==1){
				writeInst(add.e1);
				write(Inst.ADD1);
				return;
			}else if(v==0){
				writeInst(add.e1);
				return;
			}
		}
		writeSimpleBinary(add, Inst.ADD);
	}
	@Override public void visitSubtract(Expression.Subtract subtract){
		if(subtract.e2.isConstant()){
			double v = subtract.e2.expectConstantNumber();
			if(v==1){
				writeInst(subtract.e1);
				write(Inst.SUB1);
				return;
			}else if(v==0){
				writeInst(subtract.e1);
				return;
			}
		}
		writeSimpleBinary(subtract, Inst.SUBTRACT);
	}
	@Override public void visitMultiply(Expression.Multiply multiply){
		if(multiply.e1.isConstant()&&multiply.e1.expectConstantNumber()==1) writeInst(multiply.e2);
		else if(multiply.e2.isConstant()&&multiply.e2.expectConstantNumber()==1) writeInst(multiply.e1);
		else writeSimpleBinary(multiply, Inst.MULTIPLY);
	}
	@Override public void visitDivide(Expression.Divide divide){
		if(divide.e2.isConstant()&&divide.e2.expectConstantNumber()==1) writeInst(divide.e1);
		else writeSimpleBinary(divide, Inst.DIVIDE);
	}
	@Override public void visitIn(Expression.In in){
		writeInst(in.e1);
		writeInst(in.e2);
		write(Inst.IN);
		removeStack();
	}
	@Override public void visitOr(Expression.Or or){
		writeInst(or.e1);
		write(Inst.JUMPIF);
		removeStack();
		int p = getNextWritePoint();
		write2((short)0);
		writeInst(or.e2);
		write(Inst.JUMP);
		int p2 = getNextWritePoint();
		write2((short)0);
		write2At(p, getJumpCoord(p));
		write(Inst.TRUE);
		addStack();
		write2At(p2, getJumpCoord(p2));
	}
	@Override public void visitAnd(Expression.And and){
		writeInst(and.e1);
		write(Inst.JUMPELSE);
		removeStack();
		int p = getNextWritePoint();
		write2((short)0);
		writeInst(and.e2);
		write(Inst.JUMP);
		int p2 = getNextWritePoint();
		write2((short)0);
		write2At(p, getJumpCoord(p));
		write(Inst.FALSE);
		addStack();
		write2At(p2, getJumpCoord(p2));
	}
	@Override public void visitRange(Expression.RangeOperator rangeOperator){
		writeSimpleBinary(rangeOperator, Inst.RANGE);
	}
	@Override public void visitRandomInt(Expression.RandomInt randomInt){
		if(randomInt.a.isConstant()&&randomInt.b.isConstant()){
			write(Inst.RANDN);
			write4(randomInt.a.expectConstantInt());
			write4(randomInt.b.expectConstantInt());
			addStack();
		}else{
			writeInst(randomInt.a);
			writeInst(randomInt.b);
			write(Inst.RAND);
			removeStack();
		}
	}
	@Override public void visitAccess(Expression.Access access){
		write(Inst.GET_PROPERTY);
		write(identifier(access.property));
	}
	@Override public void visitDynamicAccess(Expression.DynamicAccess dynamicAccess){
		write(Inst.DYNAMIC_GET);
		write(identifier(dynamicAccess.property));
		addStack();
	}
	@Override public void visitLocalAccess(Expression.LocalAccess localAccess){
		Local local = getDefinition(localAccess.name);
		if(local!=null) local.write(this);
		else if(localAccess.isConstant()) writeConstant(localAccess.getConstantObject());
		else error("No constant defined with name '"+localAccess.name+"'");
		addStack();
	}
	@Override public void visitExecute(Expression.Execute execute){
		writeInst(execute.object);
		writeInst(execute.parameter);
		write(Inst.EXECUTE);
		removeStack();
	}
	@Override public void visitConstant(Expression.Constant constant){
		writeConstant(constant.constant);
		addStack();
	}
	@Override public void visitFunction(Expression.Function function){
		WtfExecutable exec = writeInnerScope(function.parameter, function.statements, function.scope);
		// TODo lel
	}
	@Override public void visitConstruct(Expression.Construct construct){
		// TODO suck my dick
	}
	@Override public void visitBool(Expression.Bool bool){
		write(bool.value ? Inst.TRUE : Inst.FALSE);
		addStack();
	}
	@Override public void visitThis(Expression.This thisExpr){
		write(Inst.THIS);
		addStack();
	}
	@Override public void visitNull(Expression.Null nullExpr){
		write(Inst.NULL);
		addStack();
	}
	@Override public void visitDebug(Expression.Debug debug){
		writeInst(debug.expression);
		write(Inst.DEBUG);
	}

	private void writeConstant(@Nullable Object object){
		writeConstant(this, this.context, object);
	}

	private static void writeConstant(InstWriter inst, CompileContext context, @Nullable Object object){
		if(object==null) inst.write(Inst.NULL);
		else if(object instanceof Integer){
			int i = (int)object;
			switch(i){
				case 0:
					inst.write(Inst.I0);
					break;
				case 1:
					inst.write(Inst.I1);
					break;
				case -1:
					inst.write(Inst.IM1);
					break;
				default:
					inst.write(Inst.I);
					inst.write4(i);
			}
		}else if(object instanceof Double){
			double d = (double)object;
			if(d==0.0) inst.write(Inst.D0);
			else if(d==1.0) inst.write(Inst.D1);
			else if(d==-1.0) inst.write(Inst.DM1);
			else{
				inst.write(Inst.D);
				inst.write8(Double.doubleToRawLongBits(d));
			}
		}else if(object instanceof Boolean){
			inst.write((boolean)object ? Inst.TRUE : Inst.FALSE);
		}else{
			inst.write(Inst.CONST);
			inst.write(context.getEngine().getConstantPool().mapObject(object));
		}
	}

	private WtfExecutable writeInnerScope(List<String> parameter, List<Statement> statements, Scope scope){
		pushBlock();
		// TODO bingus
		popBlock();
	}

	private byte identifier(String identifier){
		return context.getEngine().getConstantPool().mapIdentifier(identifier);
	}

	private void error(String message){
		throw new WtfCompileException(this.currentSourcePosition, message);
	}

	private void pushBlock(){
		blocks.add(new Block(currentStack));
	}
	private void popBlock(){
		if(blocks.isEmpty()) error("Internal definition error");
		Block block = blocks.remove(blocks.size()-1);
		int locals = block.calculateLocalsInStack();
		if(currentStack-locals!=block.startingStack)
			error("Stack does not match between start and end of the block");
		for(int i = 0; i<locals; i++)
			write(Inst.DISCARD);
	}

	private void setDefinition(String name, Local local){
		if(blocks.get(blocks.size()-1).locals.putIfAbsent(name, local)!=null)
			error("Property with name '"+name+"' is already defined");
	}

	@Nullable private Local getDefinition(String name){
		for(int i = blocks.size()-1; i>=0; i--){
			Block m = blocks.get(i);
			Local local = m.locals.get(name);
			if(local!=null) return local;
		}
		return null;
	}

	private void addStack(){
		addStack(1);
	}
	private void addStack(int amount){
		currentStack += amount;
		if(maxStack<currentStack) maxStack = currentStack;
	}

	private void removeStack(){
		removeStack(1);
	}
	private void removeStack(int amount){
		currentStack -= amount;
		if(currentStack<0) error("Internal stack count error");
	}

	private void newConstantLocal(String name, @Nullable Object constant){
		InstWriter inst = new InstWriter();
		writeConstant(inst, this.context, constant);
		setDefinition(name, new Local.Constant(inst.getInstructions()));
	}
	private void newLocal(String name){
		setDefinition(name, new Local.StackLocal((byte)currentStack));
	}

	private static final class Block{
		public final Map<String, Local> locals = new HashMap<>();
		public final int startingStack;

		private Block(int startingStack){
			this.startingStack = startingStack;
		}

		public int calculateLocalsInStack(){
			int localsInStack = 0;
			for(Entry<String, Local> e : locals.entrySet())
				if(e.getValue() instanceof Local.StackLocal)
					localsInStack++;
			return localsInStack;
		}
	}

	private static abstract class Local{
		protected abstract void write(InstWriter writer);

		private static final class Constant extends Local{
			public final byte[] constantInst;

			private Constant(byte[] constantInst){
				this.constantInst = constantInst;
			}

			@Override protected void write(InstWriter writer){
				writer.writeAll(constantInst);
			}
		}

		private static final class StackLocal extends Local{
			public final byte stackIndex;

			private StackLocal(byte stackIndex){
				this.stackIndex = stackIndex;
			}

			@Override protected void write(InstWriter writer){
				writer.write(Inst.DUP_AT);
				writer.write(stackIndex);
			}
		}

		private static final class InternalConstantLocal extends Local{
			public final byte internalConstantIndex;

			private InternalConstantLocal(byte internalConstantIndex){
				this.internalConstantIndex = internalConstantIndex;
			}

			@Override protected void write(InstWriter writer){
				writer.write(Inst.INTERNAL_CONST);
				writer.write(internalConstantIndex);
			}
		}

		private static final class ArgLocal extends Local{
			public final byte argIndex;

			private ArgLocal(byte argIndex){
				this.argIndex = argIndex;
			}

			@Override protected void write(InstWriter writer){
				writer.write(Inst.ARG);
				writer.write(argIndex);
			}
		}
	}
}
