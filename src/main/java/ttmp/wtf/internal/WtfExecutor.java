package ttmp.wtf.internal;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class WtfExecutor{ // TODO needs global constants, probably needs to be put on bottom of the stack before root init
	private final WtfScriptEngine engine;
	private final WtfScript script;

	private final Object[] stack;
	private int stackSize;
	private final Object[] variable;

	private int ip;

	public WtfExecutor(WtfScriptEngine engine, WtfScript script, @Nullable Function<String, Object> dynamicConstantProvider){
		this.engine = engine;
		this.script = script;

		this.stack = new Object[script.getMaxStack()];
		this.variable = new Object[script.getVariables()];

		if(!script.getDynamicConstants().isEmpty()){
			if(dynamicConstantProvider==null) throwDynamicConstantError(script.getDynamicConstants().keySet().toArray(new String[0]), new String[0]);
			else{
				List<String> missing = null, wrongType = null;
				for(Map.Entry<String, DynamicConstantInfo> e : script.getDynamicConstants().entrySet()){
					Object o = dynamicConstantProvider.apply(e.getKey());
					if(o==null){
						if(missing==null) missing = new ArrayList<>();
						missing.add(e.getKey());
					}else if(!e.getValue().getConstantType().isInstance(o)){
						if(wrongType==null) wrongType = new ArrayList<>();
						wrongType.add(e.getKey());
					}else variable[e.getValue().getVarId()] = o;
				}
				if(missing!=null||wrongType!=null) throwDynamicConstantError(
						missing!=null ? missing.toArray(new String[0]) : new String[0],
						wrongType!=null ? wrongType.toArray(new String[0]) : new String[0]);
			}
		}
	}

	private void throwDynamicConstantError(String[] missing, String[] wrongType){
		if(missing.length==0){
			if(wrongType.length==0) throw new RuntimeException("I mean... Nothing wrong with it??");
			else throw new WtfException(wrongType.length+" provided dynamic constant doesn't match type specified at compile ("+String.join(", ", wrongType)+")");
		}else if(wrongType.length==0) throw new WtfException(missing.length+" constants are not provided ("+String.join(", ", missing)+")");
		else throw new WtfException(missing.length+" constants are not provided ("+String.join(", ", missing)+"), "+
					wrongType.length+" provided dynamic constant doesn't match type specified at compile ("+String.join(", ", wrongType)+")");
	}

	public WtfScriptEngine getEngine(){
		return engine;
	}
	public WtfScript getScript(){
		return script;
	}

	public int getCurrentLine(){
		return script.getLines().getLine(ip-1);
	}

	public Object execute(Initializer<?> initializer){
		return execute(initializer, 0);
	}

	public Object execute(Initializer<?> initializer, int start){
		ip = start;
		int startingStack = 0;
		push(initializer);
		while(true){
			switch(next()){
				case Inst.PUSH:
					pushObj();
					break;
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
				case Inst.N0:
					push(0.0);
					break;
				case Inst.N1:
					push(1.0);
					break;
				case Inst.N2:
					push(2.0);
					break;
				case Inst.N3:
					push(3.0);
					break;
				case Inst.N4:
					push(4.0);
					break;
				case Inst.N5:
					push(5.0);
					break;
				case Inst.NM1:
					push(-1.0);
					break;
				case Inst.ADD:
					push(popNumber()+popNumber());
					break;
				case Inst.SUBTRACT:{
					double d1 = popNumber(), d2 = popNumber();
					push(d2-d1);
					break;
				}
				case Inst.MULTIPLY:
					push(popNumber()*popNumber());
					break;
				case Inst.DIVIDE:{
					double d1 = popNumber(), d2 = popNumber();
					push(d2/d1);
					break;
				}
				case Inst.NEGATE:
					push(-popNumber());
					break;
				case Inst.NOT:
					push(!popBoolean());
					break;
				case Inst.EQ:
					push(pop().equals(pop()));
					break;
				case Inst.NEQ:
					push(!pop().equals(pop()));
					break;
				case Inst.LT:{
					double d1 = popNumber(), d2 = popNumber();
					push(d2<d1);
					break;
				}
				case Inst.GT:{
					double d1 = popNumber(), d2 = popNumber();
					push(d2>d1);
					break;
				}
				case Inst.LTEQ:{
					double d1 = popNumber(), d2 = popNumber();
					push(d2<=d1);
					break;
				}
				case Inst.GTEQ:{
					double d1 = popNumber(), d2 = popNumber();
					push(d2>=d1);
					break;
				}
				case Inst.ADD1:
					push(popNumber()+1);
					break;
				case Inst.SUB1:
					push(popNumber()-1);
					break;
				case Inst.BUNDLE2:{
					Object o1 = pop(), o2 = pop();
					push(new Bundle(o2, o1));
					break;
				}
				case Inst.BUNDLE3:{
					Object o1 = pop(), o2 = pop(), o3 = pop();
					push(new Bundle(o3, o2, o1));
					break;
				}
				case Inst.BUNDLE4:{
					Object o1 = pop(), o2 = pop(), o3 = pop(), o4 = pop();
					push(new Bundle(o4, o3, o2, o1));
					break;
				}
				case Inst.BUNDLEN:{
					int size = nextUnsigned();
					Object[] bundle = new Object[size];
					for(int i = size-1; i>=0; i--)
						bundle[i] = pop();
					push(new Bundle(bundle));
					break;
				}
				case Inst.APPEND2:{
					Object o1 = pop(), o2 = pop();
					push(String.valueOf(o2)+o1);
					break;
				}
				case Inst.APPEND3:{
					Object o1 = pop(), o2 = pop(), o3 = pop();
					push(String.valueOf(o3)+o2+o1);
					break;
				}
				case Inst.APPEND4:{
					Object o1 = pop(), o2 = pop(), o3 = pop(), o4 = pop();
					push(String.valueOf(o4)+o3+o2+o1);
					break;
				}
				case Inst.APPENDN:{
					int size = nextUnsigned();
					StringBuilder stb = new StringBuilder();
					for(int i = size-1; i>=0; i--)
						stb.append(getNBelowTop(i));
					discard(size);
					push(stb.toString());
					break;
				}
				case Inst.GET_PROPERTY:{
					String identifier = identifier();
					push(expectInitializer(getNBelowTop(nextUnsigned())).getPropertyValue(this, identifier));
					break;
				}
				case Inst.SET_PROPERTY:{
					Object o = pop();
					expectInitializer(peek()).setPropertyValue(this, identifier(), o);
					break;
				}
				case Inst.SET_PROPERTY_LAZY:{
					Initializer<?> i2 = expectInitializer(peek()).setPropertyValueLazy(this, identifier(), ip+2);
					if(i2!=null){
						ip += 2;
						push(i2);
					}else ip += next2();
					break;
				}
				case Inst.APPLY:{
					Object o = pop();
					expectInitializer(peek()).apply(this, o);
					break;
				}
				case Inst.GET_VARIABLE:
					push(variable[nextUnsigned()]);
					break;
				case Inst.SET_VARIABLE:
					variable[nextUnsigned()] = pop();
					break;
				case Inst.RANGE:{
					double d1 = popNumber(), d2 = popNumber();
					push(new Range(d2, d1));
					break;
				}
				case Inst.NEW:{
					String identifier = identifier();
					InitDefinition<?> def = engine.getKnownTypes().get(identifier);
					if(def==null) error("Unknown type '"+identifier+"'");
					push(def.createInitializer());
					break;
				}
				case Inst.MAKE:
					push(expectInitializer(pop()).finish(this));
					break;
				case Inst.MAKE_ITERATOR:
					push(expectIterable(pop()).iterator());
					break;
				case Inst.JUMP:
					ip += next2();
					break;
				case Inst.JUMPIF:
					ip += popBoolean() ? next2() : 2;
					break;
				case Inst.JUMPELSE:
					ip += popBoolean() ? 2 : next2();
					break;
				case Inst.JUMP_IF_LT1:
					ip += peekNumber()<1 ? next2() : 2;
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
					if(stackSize==startingStack+1)
						return expectInitializer(pop()).finish(this);
					else{
						Object o = expectInitializer(pop()).finish(this);
						expectInitializer(peek()).setPropertyValue(this, identifier(), o);
						break;
					}
				case Inst.END:
					return expectInitializer(pop()).finish(this);
				default:
					error("Unknown bytecode '"+script.getInst(ip-1)+"'");
			}
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

	private void pushObj(){
		int i = nextUnsigned();
		if(script.getObjectSize()<=i) error("Object index out of bounds ("+i+")");
		push(script.getObject(i));
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

	private Object getNBelowTop(int i){
		if(i>=stackSize) error("Kinda hard to get object "+i+" below top from stack... when the stack size is "+stackSize);
		return stack[stackSize-1-i];
	}

	private void discard(int amount){
		if(amount>stackSize) error("Stack underflow");
		stackSize -= amount;
	}

	private double popNumber(){
		Object o = pop();
		if(!(o instanceof Double)) error("Not a number");
		return (double)o;
	}

	private double peekNumber(){
		Object o = peek();
		if(!(o instanceof Double)) error("Not a number");
		return (double)o;
	}

	private boolean popBoolean(){
		Object o = pop();
		if(!(o instanceof Boolean)) error("Not a boolean");
		return (boolean)o;
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
		if(!(o instanceof Double)) error("Expected number but provided "+o.getClass().getSimpleName());
		return (double)o;
	}
	public final boolean expectBoolean(Object o){
		if(!(o instanceof Boolean)) error("Expected number but provided "+o.getClass().getSimpleName());
		return (boolean)o;
	}
	public final <T> T expectType(Class<T> expectedType, Object o){
		if(!expectedType.isInstance(o)) error("Expected "+expectedType.getSimpleName()+" but provided "+o.getClass().getSimpleName());
		return expectedType.cast(o);
	}

	public final <T> T noPropertyError(String property){
		throw new WtfNoPropertyException(getCurrentLine(), property);
	}
	public final void noApplyFunctionError(){
		throw new WtfNoFunctionException(getCurrentLine(), "Apply function is not defined");
	}
}
