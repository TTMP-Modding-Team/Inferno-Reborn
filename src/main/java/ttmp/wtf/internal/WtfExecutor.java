package ttmp.wtf.internal;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import com.sun.org.omg.CORBA.Initializer;
import ttmp.wtf.EvalContext;
import ttmp.wtf.Wtf;
import ttmp.wtf.WtfScript;
import ttmp.wtf.exceptions.WtfEvalException;
import ttmp.wtf.exceptions.WtfException;
import ttmp.wtf.obj.Bundle;
import ttmp.wtf.obj.Range;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;

public class WtfExecutor{
	public final EvalContext context;

	private final Object[] stack;
	private int stackIndex;

	private int ip;

	public WtfExecutor(EvalContext context){
		this.stack = new Object[64];
		this.context = context;
	}

	@Nullable public EvalContext getContext(){
		return context;
	}

	public void reset(){
		Arrays.fill(this.stack, null);
		this.stackIndex = 0;
	}

	public void execute(WtfScript script, int start, Object[] args){
		new Unit(script).execute(start, args);
	}

	public final class Unit{
		private final WtfScript script;

		public Unit(WtfScript script){
			this.script = script;
		}

		/**
		 * @throws WtfEvalException      on evaluation error
		 * @throws IllegalStateException if executor is not initialized yet
		 */
		public void execute(int start, Object[] args){
			try{
				ip = start;
				int startingStack = stackIndex;
				for(Object arg : args)
					push(arg);
				LOOP:
				while(true){
					switch(next()){
						case Inst.PUSH:
							push(script.getEngine().getConstantPool().getObject(nextUnsigned()));
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
							setAndDiscard(1, Wtf.randomInt(script.getEngine().getRandom(), expectInt(peek(1)), expectInt(peek())));
							break;
						case Inst.RANDN:
							push(Wtf.randomInt(script.getEngine().getRandom(), next4(), next4()));
							break;
						case Inst.NEW:{
							String identifier = identifier();
							InitDefinition<?> def = script.getEngine().getType(identifier);
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
							script.getEngine().debug(peek());
							break;
						case Inst.FINISH_PROPERTY_INIT:
							if(stackIndex==startingStack+1) break LOOP;
							expectInitializer(peek(nextUnsigned()))
									.setPropertyValue(this, identifier(), expectInitializer(pop()).finish(this));
							break;
						case Inst.END:
							break LOOP;
						default:
							error("Unknown bytecode '"+script.getInst(ip-1)+"'");
					}
				}
				discard(args.length);
				if(stackIndex!=startingStack) error("Stack size doesn't match ");
			}catch(WtfException ex){
				throw ex;
			}catch(Exception ex){
				throw new WtfEvalException(getCurrentLine(), "Encountered error during evaluation", ex);
			}
		}

		public int getCurrentLine(){
			return script.getLines().getLine(ip-1);
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
			if(stackIndex==stack.length) error("Stack overflow");
			stack[stackIndex++] = o;
		}

		private Object pop(){
			if(stackIndex==0) error("Stack underflow");
			return stack[--stackIndex];
		}

		private Object peek(){
			if(stackIndex==0) error("Stack underflow");
			return stack[stackIndex-1];
		}

		/**
		 * Peek stack {@code i} below top; {@code peek(0)} is equivalent to {@code peek()}.
		 */
		private Object peek(int i){
			if(i>=stackIndex) error("Kinda hard to get object "+i+" below top... when the stack size is "+stackIndex);
			return stack[stackIndex-1-i];
		}

		/**
		 * Set object to stack at {@code i} below top. Does not modify stack size.
		 */
		private void set(int i, Object o){
			if(i>=stackIndex) error("Kinda hard to set object "+i+" below top... when the stack size is "+stackIndex);
			stack[stackIndex-1-i] = o;
		}

		/**
		 * Set object to stack at {@code i}, and discard everything on top of it.
		 */
		private void setAndDiscard(int i, Object o){
			if(i>=stackIndex) error("Kinda hard to set object "+i+" below top... when the stack size is "+stackIndex);
			stack[(stackIndex -= i)-1] = o;
		}

		private void discard(int amount){
			if(amount>stackIndex) error("Stack underflow");
			stackIndex -= amount;
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
			return script.getEngine().getConstantPool().getIdentifier(nextUnsigned());
		}

		public void error(String message){
			throw new WtfEvalException(getCurrentLine(), message);
		}

		public double expectNumber(Object o){
			if(!(o instanceof Number)) error("Expected number but provided "+o.getClass().getSimpleName());
			return (double)o;
		}
		public int expectInt(Object o){
			if(!(o instanceof Integer)) error("Expected integer but provided "+o.getClass().getSimpleName());
			return (int)o;
		}
		public Number expectNumberObject(Object o){
			if(!(o instanceof Number)) error("Expected number but provided "+o.getClass().getSimpleName());
			return (Number)o;
		}
		public boolean expectBoolean(Object o){
			if(!(o instanceof Boolean)) error("Expected boolean but provided "+o.getClass().getSimpleName());
			return (boolean)o;
		}
		public <T> T expectType(Class<T> expectedType, Object o){
			if(!expectedType.isInstance(o)) error("Expected "+expectedType.getSimpleName()+" but provided "+o.getClass().getSimpleName());
			return expectedType.cast(o);
		}
	}
}
