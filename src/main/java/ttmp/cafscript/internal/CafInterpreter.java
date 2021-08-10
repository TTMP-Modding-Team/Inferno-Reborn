package ttmp.cafscript.internal;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;
import ttmp.cafscript.CafScript;
import ttmp.cafscript.CafScriptEngine;
import ttmp.cafscript.definitions.InitDefinition;
import ttmp.cafscript.definitions.initializer.Initializer;
import ttmp.cafscript.exceptions.CafException;

public class CafInterpreter{ // TODO needs global constants, probably needs to be put on bottom of the stack before root init
	private final CafScriptEngine engine;
	private final CafScript script;

	private final Object[] stack;
	private int stackSize;
	private final Object[] variable;

	private int ip;

	public CafInterpreter(CafScriptEngine engine, CafScript script){
		this.engine = engine;
		this.script = script;

		this.stack = new Object[script.getMaxStack()];
		this.variable = new Object[script.getVariables()];
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
				case Inst.BUNDLE2:{
					Object o1 = pop(), o2 = pop();
					push(new Object[]{o2, o1});
					break;
				}
				case Inst.BUNDLE3:{
					Object o1 = pop(), o2 = pop(), o3 = pop();
					push(new Object[]{o3, o2, o1});
					break;
				}
				case Inst.BUNDLE4:{
					Object o1 = pop(), o2 = pop(), o3 = pop(), o4 = pop();
					push(new Object[]{o4, o3, o2, o1});
					break;
				}
				case Inst.BUNDLEN:{
					int fuck = nextUnsigned();
					Object[] bundle = new Object[fuck];
					for(int i = fuck; i>0; i--)
						bundle[i-1] = pop();
					push(bundle);
					break;
				}
				case Inst.GET_PROPERTY:{
					String identifier = identifier();
					push(expectInitializer(getNBelowTop(nextUnsigned())).getPropertyValue(identifier));
					break;
				}
				case Inst.SET_PROPERTY:{
					Object o = pop();
					expectInitializer(peek()).setPropertyValue(identifier(), o);
				}
					break;
				case Inst.SET_PROPERTY_LAZY:{
					Initializer<?> i2 = expectInitializer(peek()).setPropertyValueLazy(identifier(), ip+2);
					if(i2!=null){
						ip += 2;
						push(i2);
					}else ip += next2();
					break;
				}
				case Inst.APPLY:{
					Object o = pop();
					expectInitializer(peek()).apply(o);
					break;
				}
				case Inst.GET_VARIABLE:
					push(variable[nextUnsigned()]);
					break;
				case Inst.SET_VARIABLE:
					variable[nextUnsigned()] = pop();
					break;
				case Inst.NEW:{
					String identifier = identifier();
					InitDefinition<?> def = engine.getKnownTypes().get(identifier);
					if(def==null) throw new CafException("Unknown type '"+identifier+"'");
					push(def.createInitializer());
					break;
				}
				case Inst.MAKE:{
					Object o = pop();
					if(!(o instanceof Initializer<?>))
						throw new CafException("Expected initializer");
					push(((Initializer<?>)o).finish());
					break;
				}
				case Inst.JUMP:
					ip += next2();
					break;
				case Inst.JUMPIF:
					ip += popBoolean() ? next2() : 2;
					break;
				case Inst.JUMPELSE:
					ip += popBoolean() ? 2 : next2();
					break;
				case Inst.DEBUG:
					engine.debug(peek());
					break;
				case Inst.FINISH_PROPERTY_INIT:
					if(stackSize==startingStack+1)
						return expectInitializer(pop()).finish();
					else{
						Object o = expectInitializer(pop()).finish();
						expectInitializer(peek()).setPropertyValue(identifier(), o);
						break;
					}
				case Inst.END:
					return expectInitializer(pop()).finish();
				default:
					throw new CafException("Unknown bytecode '"+script.getInst(ip-1)+"'");
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
		if(script.getObjectSize()>i)
			push(script.getObject(i));
		else throw new CafException("Object index out of bounds ("+i+")");
	}

	private void push(Object o){
		if(stackSize==stack.length)
			throw new CafException("StackOverflow.com");
		stack[stackSize++] = o;
	}

	private Object pop(){
		if(stackSize==0)
			throw new CafException("StackUnderflow.com");
		return stack[--stackSize];
	}

	private Object peek(){
		if(stackSize==0)
			throw new CafException("StackUnderflow.com");
		return stack[stackSize-1];
	}

	private Object getNBelowTop(int i){
		if(i<stackSize){
			return stack[stackSize-1-i];
		}else throw new CafException("Kinda hard to get object "+i+" below top from stack... when the stack size is "+stackSize);
	}

	private double popNumber(){
		Object o = pop();
		if(!(o instanceof Double)) throw new CafException("Not a number");
		else return (double)o;
	}

	private boolean popBoolean(){
		Object o = pop();
		if(!(o instanceof Boolean)) throw new CafException("Not a boolean");
		else return (boolean)o;
	}

	private static Initializer<?> expectInitializer(Object o){
		if(!(o instanceof Initializer)) throw new CafException("Not an initializer");
		else return (Initializer<?>)o;
	}

	private String identifier(){
		int i = nextUnsigned();
		if(script.getIdentifierSize()>i) return script.getIdentifier(i);
		else throw new CafException("Identifier index out of bounds ("+i+")");
	}
}
