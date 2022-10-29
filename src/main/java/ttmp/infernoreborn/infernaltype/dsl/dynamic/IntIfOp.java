package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;

public final class IntIfOp implements DynamicInt{
	private final DynamicBool condition;
	private final DynamicInt ifThen, elseThen;

	public IntIfOp(DynamicBool condition, DynamicInt ifThen, DynamicInt elseThen){
		this.condition = condition;
		this.ifThen = ifThen;
		this.elseThen = elseThen;
	}

	@Override public int evaluateInt(@Nullable InfernalGenContext context){
		return condition.evaluate(context) ? ifThen.evaluateInt(context) : elseThen.evaluateInt(context);
	}

	@Override public boolean isConstant(){
		return condition.isConstant()&&(condition.evaluate(null) ? ifThen : elseThen).isConstant();
	}

	@Override public String toString(){
		return "if("+condition+", "+ifThen+", "+elseThen+")";
	}
}
