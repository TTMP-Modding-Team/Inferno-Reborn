package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import ttmp.infernoreborn.infernaltype.InfernalGenContext;

import javax.annotation.Nullable;

public final class IntRange implements DynamicInt{
	private final DynamicInt a, b;

	public IntRange(DynamicInt a, DynamicInt b){
		this.a = a;
		this.b = b;
	}

	@Override public int evaluateInt(@Nullable InfernalGenContext context){
		Dynamic.expectContext(context);
		int a = this.a.evaluateInt(context);
		int b = this.b.evaluateInt(context);
		if(a==b) return a;
		long min = Math.min(a, b), max = Math.max(a, b);
		return (int)(DynamicUtil.nextLong(context.getRandom(), max-min)+min);
	}

	@Override public String toString(){
		return "("+a+"~"+b+")";
	}
}
