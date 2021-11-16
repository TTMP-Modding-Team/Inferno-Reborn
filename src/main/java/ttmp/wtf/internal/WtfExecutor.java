package ttmp.wtf.internal;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import ttmp.wtf.EvalContext;
import ttmp.wtf.Wtf;
import ttmp.wtf.WtfScript;
import ttmp.wtf.WtfScriptEngine;
import ttmp.wtf.definitions.InitDefinition;
import ttmp.wtf.definitions.initializer.Initializer;
import ttmp.wtf.exceptions.WtfEvalException;
import ttmp.wtf.exceptions.WtfException;
import ttmp.wtf.exceptions.WtfNoFunctionException;
import ttmp.wtf.exceptions.WtfNoPropertyException;
import ttmp.wtf.obj.Bundle;
import ttmp.wtf.obj.Range;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WtfExecutor{
	private final WtfScriptEngine engine;
	private final WtfScript script;
	private final EvalContext context;

	private final Object[] stack;
	private int stackSize;
	private final Object[] variable;

	private int ip;

	public WtfExecutor(WtfScriptEngine engine, WtfScript script, EvalContext context){
		this.engine = Objects.requireNonNull(engine);
		this.script = Objects.requireNonNull(script);
		this.context = Objects.requireNonNull(context);

		this.stack = new Object[script.getMaxStack()];
		this.variable = new Object[script.getVariables()];

		setDynamicConstants();
	}

	public void setDynamicConstants(){
		if(!script.getDynamicConstants().isEmpty()){
			List<String> errors = null;
			for(Map.Entry<String, DynamicConstantInfo> e : script.getDynamicConstants().entrySet()){
				Object o = context.getDynamicConstant(e.getKey());
				if(o==null){
					if(errors==null) errors = new ArrayList<>();
					errors.add("Dynamic constant '"+e.getKey()+"' is not provided");
				}else if(!e.getValue().getConstantType().isInstance(o)){
					if(errors==null) errors = new ArrayList<>();
					errors.add("Dynamic constant '"+e.getKey()+"' doesn't match type specified at compilation (expected "+
							e.getValue().getConstantType().getSimpleName()+", provided "+o.getClass().getSimpleName()+")");
				}else variable[e.getValue().getVarId()] = o;
			}
			if(errors!=null){
				if(errors.size()==1){
					throw new WtfException(errors.get(0));
				}
				throw new WtfException(errors.size()+" errors setting dynamic constants:\n"+String.join("\n", errors));
			}
		}
	}

	public WtfScriptEngine getEngine(){
		return engine;
	}
	public WtfScript getScript(){
		return script;
	}

	public EvalContext getContext(){
		return context;
	}

	public int getCurrentLine(){
		return script.getLines().getLine(ip-1);
	}

	/**
	 * @throws WtfEvalException on evaluation error
	 */
	public <T> T execute(Initializer<T> initializer){
		return execute(initializer, 0);
	}

	/**
	 * @throws WtfEvalException on evaluation error
	 */
	public <T> T execute(Initializer<T> initializer, int start){
		try{
			ip = start;
			int startingStack = 0;
			push(initializer);
			LOOP:
			while(true){
				switch(next()){
					case Inst.PUSH:{
						int i = nextUnsigned();
						if(script.getObjectSize()<=i) error("Object index out of bounds ("+i+")");
						push(script.getObject(i));
						break;
					}
					case Inst.DISCARD:
						pop();
						break;
					case Inst.DUP:
						push(peek());
						break;
					case Inst.TRUE:
						push(true);
						break;
					case Inst.FALSE:
						push(false);
						break;
					case Inst.I:
						push(next4());
						break;
					case Inst.I0:
						push(0);
						break;
					case Inst.I1:
						push(1);
						break;
					case Inst.IM1:
						push(-1);
						break;
					case Inst.D:
						push(Double.longBitsToDouble(next8()));
						break;
					case Inst.D0:
						push(0.0);
						break;
					case Inst.D1:
						push(1.0);
						break;
					case Inst.DM1:
						push(-1.0);
						break;
					case Inst.ADD:
						setAndDiscard(1, Wtf.add(expectNumberObject(peek(1)), expectNumberObject(peek())));
						break;
					case Inst.SUBTRACT:
						setAndDiscard(1, Wtf.subtract(expectNumberObject(peek(1)), expectNumberObject(peek())));
						break;
					case Inst.MULTIPLY:
						setAndDiscard(1, Wtf.multiply(expectNumberObject(peek(1)), expectNumberObject(peek())));
						break;
					case Inst.DIVIDE:
						setAndDiscard(1, Wtf.divide(expectNumberObject(peek(1)), expectNumberObject(peek())));
						break;
					case Inst.NEGATE:
						push(Wtf.negate(expectNumberObject(pop())));
						break;
					case Inst.NOT:
						push(!expectBoolean(pop()));
						break;
					case Inst.EQ:
						push(pop().equals(pop()));
						break;
					case Inst.NEQ:
						push(!pop().equals(pop()));
						break;
					case Inst.LT:
						setAndDiscard(1, expectNumber(peek(1))<expectNumber(peek()));
						break;
					case Inst.GT:
						setAndDiscard(1, expectNumber(peek(1))>expectNumber(peek()));
						break;
					case Inst.LTEQ:
						setAndDiscard(1, expectNumber(peek(1))<=expectNumber(peek()));
						break;
					case Inst.GTEQ:
						setAndDiscard(1, expectNumber(peek(1))>=expectNumber(peek()));
						break;
					case Inst.ADD1:{
						Number n = expectNumberObject(pop());
						if(n instanceof Integer) push(n.intValue()+1);
						else push(n.doubleValue()+1);
						break;
					}
					case Inst.SUB1:{
						Number n = expectNumberObject(pop());
						if(n instanceof Integer) push(n.intValue()-1);
						else push(n.doubleValue()-1);
						break;
					}
					case Inst.BUNDLE2:
						setAndDiscard(1, new Bundle(peek(1), peek()));
						break;
					case Inst.BUNDLE3:
						setAndDiscard(2, new Bundle(peek(2), peek(1), peek()));
						break;
					case Inst.BUNDLE4:
						setAndDiscard(3, new Bundle(peek(3), peek(2), peek(1), peek()));
						break;
					case Inst.BUNDLEN:{
						int size = nextUnsigned();
						Object[] bundle = new Object[size];
						for(int i = size-1; i>=0; i--)
							bundle[i] = pop();
						push(new Bundle(bundle));
						break;
					}
					case Inst.APPEND2:
						setAndDiscard(1, String.valueOf(peek(1))+peek());
						break;
					case Inst.APPEND3:
						setAndDiscard(2, String.valueOf(peek(2))+peek(1)+peek());
						break;
					case Inst.APPEND4:
						setAndDiscard(3, String.valueOf(peek(3))+peek(2)+peek(1)+peek());
						break;
					case Inst.APPENDN:{
						int size = nextUnsigned();
						StringBuilder stb = new StringBuilder();
						for(int i = size-1; i>=0; i--)
							stb.append(peek(i));
						discard(size);
						push(stb.toString());
						break;
					}
					case Inst.GET_PROPERTY:
						push(expectInitializer(peek(nextUnsigned())).getPropertyValue(this, identifier()));
						break;
					case Inst.SET_PROPERTY:
						expectInitializer(peek(nextUnsigned())).setPropertyValue(this, identifier(), pop());
						break;
					case Inst.SET_PROPERTY_LAZY:{
						Initializer<?> i2 = expectInitializer(peek(nextUnsigned()))
								.setPropertyValueLazy(this, identifier(), ip+2);
						if(i2!=null){
							ip += 2;
							push(i2);
						}else ip += next2();
						break;
					}
					case Inst.APPLY:
						expectInitializer(peek(nextUnsigned())).apply(this, pop());
						break;
					case Inst.GET_VARIABLE:
						push(variable[nextUnsigned()]);
						break;
					case Inst.SET_VARIABLE:
						variable[nextUnsigned()] = pop();
						break;
					case Inst.RANGE:
						setAndDiscard(1, new Range(expectInt(peek(1)), expectInt(peek())));
						break;
					case Inst.RAND:
						setAndDiscard(1, Wtf.randomInt(engine.getRandom(), expectInt(peek(1)), expectInt(peek())));
						break;
					case Inst.RANDN:
						push(Wtf.randomInt(engine.getRandom(), next4(), next4()));
						break;
					case Inst.NEW:{
						String identifier = identifier();
						InitDefinition<?> def = engine.getType(identifier);
						if(def==null) error("Unknown type '"+identifier+"'");
						push(def.createInitializer(this.context));
						break;
					}
					case Inst.MAKE:
						push(expectInitializer(pop()).finish(this));
						break;
					case Inst.MAKE_ITERATOR:
						push(expectIterable(pop()).iterator());
						break;
					case Inst.IN:
						setAndDiscard(1, Wtf.isIn(peek(1), expectIterable(peek())));
						break;
					case Inst.JUMP:
						ip += next2();
						break;
					case Inst.JUMPIF:
						ip += expectBoolean(pop()) ? next2() : 2;
						break;
					case Inst.JUMPELSE:
						ip += expectBoolean(pop()) ? 2 : next2();
						break;
					case Inst.JUMP_IF_LT1:
						ip += expectInt(peek())<1 ? next2() : 2;
						break;
					case Inst.JUMP_OR_NEXT:{
						Iterator<?> it = expectIterator(peek());
						if(it.hasNext()){
							push(it.next());
							ip += 2;
						}else{
							discard(1);
							ip += next2();
						}
						break;
					}
					case Inst.DEBUG:
						engine.debug(peek());
						break;
					case Inst.FINISH_PROPERTY_INIT:
						if(stackSize==startingStack+1) break LOOP;
						expectInitializer(peek(nextUnsigned()))
								.setPropertyValue(this, identifier(), expectInitializer(pop()).finish(this));
						break;
					case Inst.END:
						break LOOP;
					default:
						error("Unknown bytecode '"+script.getInst(ip-1)+"'");
				}
			}
			if(peek()!=initializer) error("Expected root initializer");
			return initializer.finish(this);
		}catch(WtfException ex){
			throw ex;
		}catch(Exception ex){
			throw new WtfEvalException(getCurrentLine(), "Encountered error during evaluation", ex);
		}finally{
			stackSize = 0;
		}
	}

	private byte next(){
		return script.getInst(ip++);
	}
	private int nextUnsigned(){
		return Byte.toUnsignedInt(next());
	}
	private short next2(){
		return Shorts.fromBytes(next(), next());
	}
	private int nextUnsigned2(){
		return Short.toUnsignedInt(next2());
	}
	private int next4(){
		return Ints.fromBytes(next(), next(), next(), next());
	}
	private long next8(){
		return Longs.fromBytes(next(), next(), next(), next(), next(), next(), next(), next());
	}

	private void push(Object o){
		if(stackSize==stack.length) error("Stack overflow");
		stack[stackSize++] = o;
	}

	private Object pop(){
		if(stackSize==0) error("Stack underflow");
		return stack[--stackSize];
	}

	private Object peek(){
		if(stackSize==0) error("Stack underflow");
		return stack[stackSize-1];
	}

	/**
	 * Peek stack {@code i} below top; {@code peek(0)} is equivalent to {@code peek()}.
	 */
	private Object peek(int i){
		if(i>=stackSize) error("Kinda hard to get object "+i+" below top... when the stack size is "+stackSize);
		return stack[stackSize-1-i];
	}

	/**
	 * Set object to stack at {@code i} below top. Does not modify stack size.
	 */
	private void set(int i, Object o){
		if(i>=stackSize) error("Kinda hard to set object "+i+" below top... when the stack size is "+stackSize);
		stack[stackSize-1-i] = o;
	}

	/**
	 * Set object to stack at {@code i}, and discard everything on top of it.
	 */
	private void setAndDiscard(int i, Object o){
		if(i>=stackSize) error("Kinda hard to set object "+i+" below top... when the stack size is "+stackSize);
		stack[(stackSize -= i)-1] = o;
	}

	private void discard(int amount){
		if(amount>stackSize) error("Stack underflow");
		stackSize -= amount;
	}

	private Initializer<?> expectInitializer(Object o){
		if(!(o instanceof Initializer)) error("Not an initializer");
		return (Initializer<?>)o;
	}

	private Iterable<?> expectIterable(Object o){
		if(!(o instanceof Iterable<?>)) error("Expected Iterable but provided "+o.getClass().getSimpleName());
		return (Iterable<?>)o;
	}

	private Iterator<?> expectIterator(Object o){
		if(!(o instanceof Iterator<?>)) error("Expected Iterator but provided "+o.getClass().getSimpleName());
		return (Iterator<?>)o;
	}

	private String identifier(){
		int i = nextUnsigned();
		if(script.getIdentifierSize()<=i) error("Identifier index out of bounds ("+i+")");
		return script.getIdentifier(i);
	}

	public final void error(String message){
		throw new WtfEvalException(getCurrentLine(), message);
	}

	public final double expectNumber(Object o){
		if(!(o instanceof Number)) error("Expected number but provided "+o.getClass().getSimpleName());
		return (double)o;
	}
	public final int expectInt(Object o){
		if(!(o instanceof Integer)) error("Expected integer but provided "+o.getClass().getSimpleName());
		return (int)o;
	}
	public final Number expectNumberObject(Object o){
		if(!(o instanceof Number)) error("Expected number but provided "+o.getClass().getSimpleName());
		return (Number)o;
	}
	public final boolean expectBoolean(Object o){
		if(!(o instanceof Boolean)) error("Expected boolean but provided "+o.getClass().getSimpleName());
		return (boolean)o;
	}
	public final <T> T expectType(Class<T> expectedType, Object o){
		if(!expectedType.isInstance(o)) error("Expected "+expectedType.getSimpleName()+" but provided "+o.getClass().getSimpleName());
		return expectedType.cast(o);
	}

	public final <T> T noPropertyError(String property){
		throw new WtfNoPropertyException(getCurrentLine(), property);
	}
	public final void noApplyFunctionError(Class<?> aClass){
		throw new WtfNoFunctionException(getCurrentLine(), "Apply function not defined for "+aClass.getSimpleName());
	}
}
