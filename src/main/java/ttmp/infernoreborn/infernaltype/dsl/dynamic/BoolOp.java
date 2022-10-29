package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;

public final class BoolOp implements DynamicBool{
	private final DynamicBool a, b;
	private final BoolOps op;

	public BoolOp(DynamicBool a, DynamicBool b, BoolOps op){
		this.a = a;
		this.b = b;
		this.op = op;
	}

	@Override public boolean evaluateBool(@Nullable InfernalGenContext context){
		boolean a = this.a.evaluateBool(context);
		switch(op){
			case OR_SS: if(a) return true; break;
			case AND_SS: if(!a) return false;
		}
		boolean b = this.b.evaluateBool(context);
		switch(op){
			case AND_SS: case OR_SS: return b;
			case AND: return a&&b;
			case OR: return a||b;
			case EQ: return a==b;
			case NEQ: return a!=b;
			default: throw new IllegalStateException("Illegal operation");
		}
	}

	@Override public boolean isConstant(){
		return a.isConstant()&&b.isConstant();
	}

	@Override public String toString(){
		switch(op){
			case AND_SS: return "("+a+"&&"+b+")";
			case OR_SS: return "("+a+"||"+b+")";
			case AND: return "("+a+"&"+b+")";
			case OR: return "("+a+"|"+b+")";
			case EQ: return "("+a+"=="+b+")";
			case NEQ: return "("+a+"!="+b+")";
			default: throw new IllegalStateException("Illegal operation");
		}
	}
}
