package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;

public final class IntCompOp implements DynamicBool{
	private final DynamicInt a, b;
	private final CompOps op;

	public IntCompOp(DynamicInt a, DynamicInt b, CompOps op){
		this.a = a;
		this.b = b;
		this.op = op;
	}

	@Override public boolean evaluateBool(@Nullable InfernalGenContext context){
		int a = this.a.evaluateInt(context);
		int b = this.b.evaluateInt(context);
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
