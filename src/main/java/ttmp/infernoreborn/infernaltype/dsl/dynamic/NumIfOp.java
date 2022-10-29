package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;

public final class NumIfOp implements DynamicNumber{
	private final DynamicBool condition;
	private final DynamicNumber ifThen, elseThen;

	public NumIfOp(DynamicBool condition, DynamicNumber ifThen, DynamicNumber elseThen){
		this.condition = condition;
		this.ifThen = ifThen;
		this.elseThen = elseThen;
	}

	@Override public double evaluateNumber(@Nullable InfernalGenContext context){
		return condition.evaluate(context) ? ifThen.evaluateNumber(context) : elseThen.evaluateNumber(context);
	}

	@Override public boolean isConstant(){
		return condition.isConstant()&&(condition.evaluate(null) ? ifThen : elseThen).isConstant();
	}

	@Override public String toString(){
		return "if("+condition+", "+ifThen+", "+elseThen+")";
	}
}
