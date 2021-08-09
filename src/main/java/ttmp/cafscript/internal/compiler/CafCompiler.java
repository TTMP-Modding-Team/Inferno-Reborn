package ttmp.cafscript.internal.compiler;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import ttmp.cafscript.CafScript;
import ttmp.cafscript.exceptions.CafCompileException;
import ttmp.cafscript.exceptions.CafException;
import ttmp.cafscript.internal.Inst;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CafCompiler implements StatementVisitor, ExpressionVisitor{
	private final String script;

	public final ByteList inst = new ByteArrayList();
	public final Object2ByteMap<Object> objs = new Object2ByteOpenHashMap<>();
	public final Object2ByteMap<String> identifiers = new Object2ByteOpenHashMap<>();

	private final List<Block> blocks = new ArrayList<>();

	public int variables;

	private int currentStack = 1; // Root initializer is always in stack
	private int maxStack = 1;

	private Statement stmt;

	private boolean finished;

	public CafCompiler(String script){
		this.script = script;
		this.identifiers.defaultReturnValue((byte)-1);
		this.pushBlock();
	}

	public CafScript parseAndCompile(){
		if(!finished){
			CafParser parser = new CafParser(script);
			for(Statement s; (s = parser.parse())!=null; )
				writeInst(s);
		}
		return finish();
	}

	public CafScript finish(){
		if(!finished){
			finished = true;
			write(Inst.END);
		}
		return new CafScript(inst.toByteArray(),
				populate(objs, new Object[objs.size()]),
				populate(identifiers, new String[identifiers.size()]),
				variables,
				maxStack);
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
		this.stmt = stmt;
		stmt.visit(this);
	}

	public void writeInst(Expression expr){
		expr.visit(this);
	}

	@Override public void visitAssign(Statement.Assign assign){
		writeInst(assign.getValue());
		write(Inst.SET_PROPERTY);
		write(identifier(assign.getProperty()));
		removeStack();
	}
	@Override public void visitAssignLazy(Statement.AssignLazy assignLazy){
		write(Inst.SET_PROPERTY_LAZY);
		byte identifier = identifier(assignLazy.getProperty());
		write(identifier);
		int p = getNextWritePoint();
		write2((short)0);
		addStack();
		pushBlock();
		for(Statement s : assignLazy.getStatements())
			writeInst(s);
		popBlock();
		write(Inst.FINISH_PROPERTY_INIT);
		write(identifier);
		write2At(p, getJumpCoord(p));
	}
	@Override public void visitDefine(Statement.Define define){
		Expression e = define.getValue();
		if(e.isConstant()){
			setDefinition(define.getProperty(),
					Definition.constant(obj(Objects.requireNonNull(
							e.getConstantObject()))));
		}else{
			Definition definition = Definition.variable(newVariableId());
			setDefinition(define.getProperty(), definition);
			writeInst(e);
			write(Inst.SET_VARIABLE);
			write(definition.varId);
			removeStack();
		}
	}
	@Override public void visitApply(Statement.Apply apply){
		writeInst(apply.getValue());
		write(Inst.APPLY);
		removeStack();
	}
	@Override public void visitIf(Statement.If apply){
		writeInst(apply.getCondition());
		if(apply.getIfThen().isEmpty()&&apply.getElseThen().isEmpty()){
			write(Inst.DISCARD);
			removeStack();
			return;
		}
		write(Inst.JUMPELSE);
		removeStack();
		int goElse = getNextWritePoint();
		write2((short)0);

		for(Statement s : apply.getIfThen()) writeInst(s);

		if(!apply.getElseThen().isEmpty()){
			write(Inst.JUMP);
			int ifEnd = getNextWritePoint();
			write2((short)0);
			write2At(goElse, getJumpCoord(goElse));

			for(Statement s : apply.getElseThen()) writeInst(s);
			write2At(ifEnd, getJumpCoord(ifEnd));
		}else{
			write2At(goElse, getJumpCoord(goElse));
		}
	}
	@Override public void visitDebug(Statement.Debug debug){
		writeInst(debug.getExpr());
		write(Inst.DEBUG);
		write(Inst.DISCARD);
		removeStack();
	}

	@Override public void visitComma(Expression.Comma comma){
		int size = comma.getExpressions().size();
		for(Expression e : comma.getExpressions())
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
	@Override public void visitNot(Expression.Not not){
		writeInst(not.getExpression());
		write(Inst.NOT);
	}
	@Override public void visitNegate(Expression.Negate negate){
		writeInst(negate.getExpression());
		write(Inst.NEGATE);
	}
	@Override public void visitBinary(Expression.Binary binary){
		writeInst(binary.getE1());
		switch(binary.getOperator()){
			case AND_AND:{
				write(Inst.JUMPELSE);
				removeStack();
				int p = getNextWritePoint();
				write2((short)0);
				writeInst(binary.getE2());
				write(Inst.JUMP);
				int p2 = getNextWritePoint();
				write2((short)0);
				write2At(p, getJumpCoord(p));
				write(Inst.FALSE);
				addStack();
				write2At(p2, getJumpCoord(p2));
				return;
			}
			case OR_OR:{
				write(Inst.JUMPIF);
				removeStack();
				int p = getNextWritePoint();
				write2((short)0);
				writeInst(binary.getE2());
				write(Inst.JUMP);
				int p2 = getNextWritePoint();
				write2((short)0);
				write2At(p, getJumpCoord(p));
				write(Inst.TRUE);
				addStack();
				write2At(p2, getJumpCoord(p2));
				return;
			}
			default:
				writeInst(binary.getE2());
				switch(binary.getOperator()){
					case PLUS:
						write(Inst.ADD);
						break;
					case MINUS:
						write(Inst.SUBTRACT);
						break;
					case STAR:
						write(Inst.MULTIPLY);
						break;
					case SLASH:
						write(Inst.DIVIDE);
						break;
					case EQ_EQ:
						write(Inst.EQ);
						break;
					case BANG_EQ:
						write(Inst.NEQ);
						break;
					case LT:
						write(Inst.LT);
						break;
					case GT:
						write(Inst.GT);
						break;
					case LT_EQ:
						write(Inst.LTEQ);
						break;
					case GT_EQ:
						write(Inst.GTEQ);
						break;
					default:
						error("Unknown binary operator '"+binary.getOperator()+"'");
				}
				removeStack();
		}
	}
	@Override public void visitTernary(Expression.Ternary ternary){
		writeInst(ternary.getCondition());
		write(Inst.JUMPELSE);
		removeStack();
		int p = getNextWritePoint();
		write2((short)0);
		writeInst(ternary.getIfThen());
		write(Inst.JUMP);
		int p2 = getNextWritePoint();
		write2((short)0);
		write2At(p, getJumpCoord(p));
		writeInst(ternary.getElseThen());
		write2At(p2, getJumpCoord(p2));
	}
	@Override public void visitNumber(Expression.Number number){
		if(0==number.getNumber()) write(Inst.N0);
		else if(1==number.getNumber()) write(Inst.N1);
		else if(2==number.getNumber()) write(Inst.N2);
		else if(3==number.getNumber()) write(Inst.N3);
		else if(4==number.getNumber()) write(Inst.N4);
		else if(5==number.getNumber()) write(Inst.N5);
		else if(-1==number.getNumber()) write(Inst.NM1);
		else{
			write(Inst.PUSH);
			write(obj(number.getNumber()));
		}
		addStack();
	}
	@Override public void visitNamespace(Expression.Namespace namespace){
		write(Inst.PUSH);
		write(obj(namespace.getNamespace()));
		addStack();
	}
	@Override public void visitColor(Expression.Color color){
		write(Inst.PUSH);
		write(obj(color.getRgb()));
		addStack();
	}
	@Override public void visitIdentifier(Expression.Identifier identifier){
		Definition definition = getDefinition(identifier.getIdentifier());
		if(definition!=null){
			if(definition.constant){
				write(Inst.PUSH);
				write(definition.constantId);
			}else{
				write(Inst.GET_VARIABLE);
				write(definition.varId);
			}
		}else{
			write(Inst.GET_PROPERTY);
			write(identifier(identifier.getIdentifier()));
			write(getStackPoint(getBlock().initializerStackPosition));
		}
		addStack();
	}
	@Override public void visitConstruct(Expression.Construct construct){
		write(Inst.NEW);
		write(identifier(construct.getIdentifier()));
		addStack();
		pushBlock();
		for(Statement s : construct.getStatements()) writeInst(s);
		popBlock();
		write(Inst.MAKE);
		removeStack();
	}
	@Override public void visitConstant(Expression.Constant constant){
		switch(constant){
			case TRUE:
				write(Inst.TRUE);
				break;
			case FALSE:
				write(Inst.FALSE);
				break;
			default:
				error("Unknown constant '"+constant+"'");
		}
		addStack();
	}
	@Override public void visitDebug(Expression.Debug debug){
		writeInst(debug.getExpression());
		write(Inst.DEBUG);
	}

	private byte obj(Object o){
		int size = objs.size();
		byte prevIndex = objs.putIfAbsent(o, (byte)size);
		if(objs.size()==size) return prevIndex;
		if(size>255) error("Too many objects");
		return (byte)size;
	}

	private byte identifier(String identifier){
		int size = identifiers.size();
		byte prevIndex = identifiers.putIfAbsent(identifier, (byte)size);
		if(identifiers.size()==size) return prevIndex;
		if(size>255) error("Too many identifiers");
		return (byte)size;
	}

	private void error(String message){
		throw CafCompileException.create(script, stmt!=null ? stmt.getPosition() : 0, message);
	}

	private byte newVariableId(){
		if(variables==256) throw new CafException("Too many variables");
		return (byte)variables++;
	}

	private void pushBlock(){
		blocks.add(new Block(currentStack));
	}
	private void popBlock(){
		if(blocks.isEmpty())
			throw new CafException("Internal definition error");
		blocks.remove(blocks.size()-1);
	}

	private Block getBlock(){
		return blocks.get(blocks.size()-1);
	}

	private void setDefinition(String name, Definition definition){
		if(blocks.get(blocks.size()-1).definitions.putIfAbsent(name, definition)!=null)
			throw new CafException("Property with name '"+name+"' is already defined");
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
		if(currentStack<0) throw new CafException("Internal stack count error");
	}

	private static <T> T[] populate(Object2ByteMap<T> map, T[] array){
		for(Object2ByteMap.Entry<T> e : map.object2ByteEntrySet())
			array[Byte.toUnsignedInt(e.getByteValue())] = e.getKey();
		return array;
	}

	protected static final class Block {
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
