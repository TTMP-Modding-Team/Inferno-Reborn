package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;

public final class NumCompOp implements DynamicBool{
	private final DynamicNumber a, b;
	private final CompOps op;

	public NumCompOp(DynamicNumber a, DynamicNumber b, CompOps op){
		this.a = a;
		this.b = b;
		this.op = op;
	}

	@Override public boolean evaluateBool(@Nullable InfernalGenContext context){
		double a = this.a.evaluateNumber(context);
		double b = this.b.evaluateNumber(context);
		switch(op){
			case GT: return a>b;
			case LT: return a<b;
			case GTEQ: return a>=b;
			case LTEQ: return a<=b;
			default: throw new IllegalStateException("Invalid operation");
		}
	}

	@Override public boolean isConstant(){
		return a.isConstant()&&b.isConstant();
	}

	@Override public String toString(){
		switch(op){
			case GT: return "("+a+">"+b+")";
			case LT: return "("+a+"<"+b+")";
			case GTEQ: return "("+a+">="+b+")";
			case LTEQ: return "("+a+"<="+b+")";
			default: throw new IllegalStateException("Invalid operation");
		}
	}
}
