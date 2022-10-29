package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;

public final class BoolIfOp implements DynamicBool{
	private final DynamicBool condition, ifThen, elseThen;

	public BoolIfOp(DynamicBool condition, DynamicBool ifThen, DynamicBool elseThen){
		this.condition = condition;
		this.ifThen = ifThen;
		this.elseThen = elseThen;
	}

	@Override public boolean evaluateBool(@Nullable InfernalGenContext context){
		return condition.evaluate(context) ? ifThen.evaluateBool(context) : elseThen.evaluateBool(context);
	}

	@Override public boolean isConstant(){
		return condition.isConstant()&&(condition.evaluate(null) ? ifThen : elseThen).isConstant();
	}

	@Override public String toString(){
		return "if("+condition+", "+ifThen+", "+elseThen+")";
	}
}
