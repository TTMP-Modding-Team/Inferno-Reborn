package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import com.google.common.math.IntMath;
import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;

public final class IntArithmeticOp implements DynamicInt{
	private final DynamicInt a, b;
	private final ArithmeticOps op;

	public IntArithmeticOp(DynamicInt a, DynamicInt b, ArithmeticOps op){
		this.a = a;
		this.b = b;
		this.op = op;
	}

	@Override public int evaluateInt(@Nullable InfernalGenContext context){
		int a = this.a.evaluateInt(context);
		int b = this.b.evaluateInt(context);
		switch(op){
			case ADD: return a+b;
			case SUB: return a-b;
			case MUL: return a*b;
			case DIV: return a/b;
			case POW: return IntMath.pow(a, b);
			default: throw new IllegalStateException("Invalid operation");
		}
	}

	@Override public boolean isConstant(){
		return a.isConstant()&&b.isConstant();
	}

	@Override public String toString(){
		switch(op){
			case ADD: return "("+a+"+"+b+")";
			case SUB: return "("+a+"-"+b+")";
			case MUL: return "("+a+"*"+b+")";
			case DIV: return "("+a+"/"+b+")";
			case POW: return "("+a+"^"+b+")";
			default: throw new IllegalStateException("Invalid operation");
		}
	}
}
