package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;

public final class NumArithmeticOp implements DynamicNumber{
	private final DynamicNumber a, b;
	private final ArithmeticOps op;

	public NumArithmeticOp(DynamicNumber a, DynamicNumber b, ArithmeticOps op){
		this.a = a;
		this.b = b;
		this.op = op;
	}

	@Override public double evaluateNumber(@Nullable InfernalGenContext context){
		double a = this.a.evaluateNumber(context);
		double b = this.b.evaluateNumber(context);
		switch(op){
			case ADD: return a+b;
			case SUB: return a-b;
			case MUL: return a*b;
			case DIV: return a/b;
			case POW: return Math.pow(a, b);
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
