package ttmp.wtf.internal.compiler;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import ttmp.wtf.CompileContext;
import ttmp.wtf.WtfScript;
import ttmp.wtf.exceptions.WtfCompileException;
import ttmp.wtf.internal.Inst;
import ttmp.wtf.internal.Lines;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WtfCompiler implements StatementVisitor, ExpressionVisitor{
	private final String script;
	private final CompileContext context;

	public final ByteList inst = new ByteArrayList();

	private final List<Block> blocks = new ArrayList<>();

	private final Lines.Builder lines = new Lines.Builder();

	public int variables;

	private int currentStack = 1; // Root initializer is always in stack
	private int maxStack = 1;

	private int currentSourcePosition;

	private boolean finished;

	public WtfCompiler(String script, CompileContext context){
		this.script = script;
		this.context = context;
		this.pushBlock(true);
	}

	public WtfScript parseAndCompile(){
		if(!finished){
			WtfParser parser = new WtfParser(script, context);
			for(Statement s; (s = parser.parse())!=null; )
				writeInst(s);
		}
		return finish();
	}

	public WtfScript finish(){
		if(!finished){
			finished = true;
			write(Inst.END);
		}
		return new WtfScript(context.getEngine(),
				inst.toByteArray(),
				lines.build(script, inst.size()));
	}

	private void write(byte inst){
		this.inst.add(inst);
	}
	private void write2(short inst){
		this.inst.add((byte)(inst >> 8));
		this.inst.add((byte)inst);
	}
	private void write4(int inst){
		this.inst.add((byte)(inst >> 24));
		this.inst.add((byte)(inst >> 16));
		this.inst.add((byte)(inst >> 8));
		this.inst.add((byte)inst);
	}
	private void write8(long inst){
		this.inst.add((byte)(inst >> 56));
		this.inst.add((byte)(inst >> 48));
		this.inst.add((byte)(inst >> 40));
		this.inst.add((byte)(inst >> 32));
		this.inst.add((byte)(inst >> 24));
		this.inst.add((byte)(inst >> 16));
		this.inst.add((byte)(inst >> 8));
		this.inst.add((byte)inst);
	}

	private int getNextWritePoint(){
		return this.inst.size();
	}
	private void writeAt(int writePoint, byte inst){
		this.inst.set(writePoint, inst);
	}
	private void write2At(int writePoint, short inst){
		this.inst.set(writePoint, (byte)(inst >> 8));
		this.inst.set(writePoint+1, (byte)inst);
	}
	private void write4At(int writePoint, int inst){
		this.inst.set(writePoint, (byte)(inst >> 24));
		this.inst.set(writePoint+1, (byte)(inst >> 16));
		this.inst.set(writePoint+2, (byte)(inst >> 8));
		this.inst.set(writePoint+3, (byte)inst);
	}

	private short getJumpCoord(int from){
		return getJumpCoord(from, getNextWritePoint());
	}
	private short getJumpCoord(int from, int to){
		int incr = to-from;
		if(incr!=(short)incr) error("Jump out of range");
		return (short)incr;
	}

	private byte getStackPoint(int dest){
		int i = currentStack-dest;
		if(i>255) error("Stack point out of range");
		return (byte)i;
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
		writeInst(assign.value);
		write(Inst.SET_PROPERTY);
		write(getStackPoint(getBlock().initializerStackPosition));
		write(identifier(assign.property));
		removeStack();
	}
	@Override public void visitAssignLazy(Statement.AssignLazy assignLazy){
		write(Inst.SET_PROPERTY_LAZY);
		int initializerStackPosition = getBlock().initializerStackPosition;
		write(getStackPoint(initializerStackPosition));
		byte identifier = identifier(assignLazy.property);
		write(identifier);
		int p = getNextWritePoint();
		write2((short)0);
		addStack();
		pushBlock(true);
		for(Statement s : assignLazy.statements)
			writeInst(s);
		popBlock();
		write(Inst.FINISH_PROPERTY_INIT);
		write(getStackPoint(initializerStackPosition));
		write(identifier);
		write2At(p, getJumpCoord(p));
		removeStack();
	}
	@Override public void visitDefine(Statement.Define define){
		Expression e = define.value;
		if(e.isConstant()){
			setDefinition(define.property,
					Definition.constant(obj(Objects.requireNonNull(
							e.getConstantObject()))));
		}else{
			Definition definition = Definition.variable(newVariableId());
			setDefinition(define.property, definition);
			writeInst(e);
			write(Inst.SET_VARIABLE);
			write(definition.varId);
			removeStack();
		}
	}
	@Override public void visitApply(Statement.Apply apply){
		writeInst(apply.value);
		write(Inst.APPLY);
		write(getStackPoint(getBlock().initializerStackPosition));
		removeStack();
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
		Definition definition = Definition.variable(newVariableId());
		setDefinition(forStatement.variable, definition);
		write(Inst.SET_VARIABLE);
		write(definition.varId);
		removeStack();

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
	@Override public void visitDebug(Statement.Debug debug){
		writeInst(debug.value);
		write(Inst.DEBUG);
		write(Inst.DISCARD);
		removeStack();
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
	@Override public void visitDynamicAccess(Expression.DynamicAccess dynamicAccess){
		write(Inst.GET_PROPERTY);
		write(getStackPoint(getBlock().initializerStackPosition));
		write(identifier(dynamicAccess.property));
		addStack();
	}
	@Override public void visitConstantAccess(Expression.ConstantAccess constantAccess){
		Definition definition = getDefinition(constantAccess.name);
		if(definition==null) error("No constant defined with name '"+constantAccess.name+"'");
		if(definition.constant){
			write(Inst.PUSH);
			write(definition.constantId);
		}else{
			write(Inst.GET_VARIABLE);
			write(definition.varId);
		}
		addStack();
	}
	@Override public void visitConstant(Expression.Constant constant){
		if(constant.constant instanceof Integer){
			int i = (int)constant.constant;
			switch(i){
				case 0:
					write(Inst.I0);
					break;
				case 1:
					write(Inst.I1);
					break;
				case -1:
					write(Inst.IM1);
					break;
				default:
					write(Inst.I);
					write4(i);
			}
		}else if(constant.constant instanceof Double){
			double d = (double)constant.constant;
			if(d==0.0) write(Inst.D0);
			else if(d==1.0) write(Inst.D1);
			else if(d==-1.0) write(Inst.DM1);
			else{
				write(Inst.D);
				write8(Double.doubleToRawLongBits(d));
			}
		}else if(constant.constant instanceof Boolean){
			write((boolean)constant.constant ? Inst.TRUE : Inst.FALSE);
		}else{
			write(Inst.PUSH);
			write(obj(constant.constant));
		}
		addStack();
	}
	@Override public void visitConstruct(Expression.Construct construct){
		write(Inst.NEW);
		write(identifier(construct.identifier));
		addStack();
		pushBlock(true);
		for(Statement s : construct.statements) writeInst(s);
		popBlock();
		write(Inst.MAKE);
	}
	@Override public void visitBool(Expression.Bool bool){
		write(bool.value ? Inst.TRUE : Inst.FALSE);
		addStack();
	}
	@Override public void visitDebug(Expression.Debug debug){
		writeInst(debug.expression);
		write(Inst.DEBUG);
	}

	private byte obj(Object o){
		return context.getEngine().getConstantPool().mapObject(o);
	}

	private byte identifier(String identifier){
		return context.getEngine().getConstantPool().mapIdentifier(identifier);
	}

	private void error(String message){
		throw new WtfCompileException(this.currentSourcePosition, message);
	}

	private byte newVariableId(){
		if(variables==256) error("Too many variables");
		return (byte)variables++;
	}

	private void pushBlock(){
		pushBlock(false);
	}
	private void pushBlock(boolean newInitializerPosition){
		blocks.add(new Block(newInitializerPosition ?
				currentStack :
				blocks.get(blocks.size()-1).initializerStackPosition));
	}
	private void popBlock(){
		if(blocks.isEmpty()) error("Internal definition error");
		blocks.remove(blocks.size()-1);
	}

	private Block getBlock(){
		return blocks.get(blocks.size()-1);
	}

	private void setDefinition(String name, Definition definition){
		if(blocks.get(blocks.size()-1).definitions.putIfAbsent(name, definition)!=null)
			error("Property with name '"+name+"' is already defined");
	}

	@Nullable private Definition getDefinition(String name){
		for(int i = blocks.size()-1; i>=0; i--){
			Block m = blocks.get(i);
			Definition definition = m.definitions.get(name);
			if(definition!=null) return definition;
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

	private static <T> T[] populate(Object2ByteMap<T> map, T[] array){
		for(Object2ByteMap.Entry<T> e : map.object2ByteEntrySet())
			array[Byte.toUnsignedInt(e.getByteValue())] = e.getKey();
		return array;
	}

	protected static final class Block{
		public final int initializerStackPosition;
		public final Map<String, Definition> definitions = new HashMap<>();

		public Block(int initializerStackPosition){
			this.initializerStackPosition = initializerStackPosition;
		}
	}

	public static final class Definition{
		public final boolean constant;
		public final byte constantId;
		public final byte varId;

		private Definition(boolean constant, byte constantId, byte varId){
			this.constant = constant;
			this.constantId = constantId;
			this.varId = varId;
		}

		public static Definition constant(byte constantId){
			return new Definition(true, constantId, (byte)0);
		}
		public static Definition variable(byte varId){
			return new Definition(false, (byte)0, varId);
		}
	}
}
