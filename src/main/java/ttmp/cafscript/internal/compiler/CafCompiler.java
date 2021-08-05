package ttmp.cafscript.internal.compiler;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import ttmp.cafscript.CafScript;
import ttmp.cafscript.exceptions.CafCompileException;
import ttmp.cafscript.internal.Inst;

public class CafCompiler implements StatementVisitor, ExpressionVisitor{
	private final String script;

	private final ByteList inst = new ByteArrayList();
	private final Object2ByteMap<Object> objs = new Object2ByteOpenHashMap<>();
	private final Object2ByteMap<String> identifiers = new Object2ByteOpenHashMap<>();

	private Statement stmt;

	private boolean finished;

	public CafCompiler(String script){
		this.script = script;

		this.identifiers.defaultReturnValue((byte)-1);
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
				populate(identifiers, new String[identifiers.size()]));
	}

	private static <T> T[] populate(Object2ByteMap<T> map, T[] array){
		for(Object2ByteMap.Entry<T> e : map.object2ByteEntrySet())
			array[e.getByteValue()] = e.getKey();
		return array;
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
	}
	@Override public void visitAssignLazy(Statement.AssignLazy assignLazy){
		// TODO Is it really necessary to make another set of possibly redundant list of constant tables?
		//      We can jam two set of bytecodes into one instruction array with END as barrier, but it means CafScript now needs to know about starting point
		//      No screw that, I can just give starting point right HERE, as parameter of SET_PROPERTY_LAZY.
		//      It would complicate CafCompiler design though because . But who tf cares, it runs one time anyway.
		//      Maybe fancy nested CafCompiler instance to keep track of it idk
		//      Rn i'm sticking to "yeah compile them separately, make possibly redundant set of instance and pass that to interpreter" approach because it's much easier
		CafCompiler compiler = new CafCompiler(script);
		for(Statement s : assignLazy.getStatements())
			compiler.writeInst(s);
		write(Inst.PUSH);
		write(obj(compiler.finish()));
		write(Inst.SET_PROPERTY_LAZY);
		write(identifier(assignLazy.getProperty()));
	}
	@Override public void visitDefine(Statement.Define define){
		writeInst(define.getValue());
		write(Inst.SET_PROPERTY);
		write(identifier(define.getProperty()));
	}
	@Override public void visitApply(Statement.Apply apply){
		writeInst(apply.getValue());
		write(Inst.APPLY);
	}
	@Override public void visitIf(Statement.If apply){
		if(apply.getIfThen().isEmpty()&&apply.getElseThen().isEmpty()) return;
		write(Inst.JUMPELSE);
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
				return;
			case 3:
				write(Inst.BUNDLE3);
				return;
			case 4:
				write(Inst.BUNDLE4);
				return;
			default:
				if(size>255) error("Bundle too large");
				write(Inst.BUNDLEN);
				write((byte)size);
		}
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
				int p = getNextWritePoint();
				write2((short)0);
				writeInst(binary.getE2());
				write(Inst.JUMP);
				int p2 = getNextWritePoint();
				write2((short)0);
				write2At(p, getJumpCoord(p));
				write(Inst.FALSE);
				write2At(p2, getJumpCoord(p2));
				return;
			}
			case OR_OR:{
				write(Inst.JUMPIF);
				int p = getNextWritePoint();
				write2((short)0);
				writeInst(binary.getE2());
				write(Inst.JUMP);
				int p2 = getNextWritePoint();
				write2((short)0);
				write2At(p, getJumpCoord(p));
				write(Inst.TRUE);
				write2At(p2, getJumpCoord(p2));
				return;
			}
			default:
				writeInst(binary.getE2());
				switch(binary.getOperator()){
					case PLUS:
						write(Inst.ADD);
						return;
					case MINUS:
						write(Inst.SUBTRACT);
						return;
					case STAR:
						write(Inst.MULTIPLY);
						return;
					case SLASH:
						write(Inst.DIVIDE);
						return;
					case EQ_EQ:
						write(Inst.EQ);
						return;
					case BANG_EQ:
						write(Inst.NEQ);
						return;
					case LT:
						write(Inst.LT);
						return;
					case GT:
						write(Inst.GT);
						return;
					case LT_EQ:
						write(Inst.LTEQ);
						return;
					case GT_EQ:
						write(Inst.GTEQ);
						return;
					default:
						error("Unknown binary operator '"+binary.getOperator()+"'");
				}
		}
	}
	@Override public void visitTernary(Expression.Ternary ternary){
		writeInst(ternary.getCondition());
		write(Inst.JUMPELSE);
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
	}
	@Override public void visitNamespace(Expression.Namespace namespace){
		write(Inst.PUSH);
		write(obj(namespace.getNamespace()));
	}
	@Override public void visitColor(Expression.Color color){
		write(Inst.PUSH);
		write(obj(color.getRgb()));
	}
	@Override public void visitIdentifier(Expression.Identifier identifier){
		write(Inst.GET_PROPERTY);
		write(identifier(identifier.getIdentifier()));
	}
	@Override public void visitConstruct(Expression.Construct construct){
		write(Inst.NEW);
		write(identifier(construct.getIdentifier()));
		for(Statement s : construct.getStatements()) writeInst(s);
		write(Inst.MAKE);
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
}
