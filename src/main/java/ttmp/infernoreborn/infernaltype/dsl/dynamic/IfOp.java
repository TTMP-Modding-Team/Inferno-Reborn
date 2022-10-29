package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;

public final class IfOp implements Dynamic{
	private final DynamicBool condition;
	private final Dynamic ifThen, elseThen;

	public IfOp(DynamicBool condition, Dynamic ifThen, Dynamic elseThen){
		this.condition = condition;
		this.ifThen = ifThen;
		this.elseThen = elseThen;
	}

	@Override public Object evaluate(@Nullable InfernalGenContext context){
		return condition.evaluate(context) ? ifThen.evaluate(context) : elseThen.evaluate(context);
	}

	@Override public boolean matches(Class<?> type){
		return ifThen.matches(type)&&elseThen.matches(type);
	}

	@Override public boolean isConstant(){
		return condition.isConstant()&&(condition.evaluate(null) ? ifThen : elseThen).isConstant();
	}

	@Override public String toString(){
		return "if("+condition+", "+ifThen+", "+elseThen+")";
	}
}
