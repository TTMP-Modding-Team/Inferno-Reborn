package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import among.construct.ConditionedListConstructorBuilder;
import among.construct.Constructor;
import among.obj.AmongList;

import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static among.construct.ConditionedConstructor.binaryCondition;
import static among.construct.ConditionedConstructor.listConditions;

final class DynamicUtil{
	private DynamicUtil(){}

	static long nextLong(Random random, long bound){
		if(bound<0) throw new IllegalArgumentException("bound must be positive");
		return bound<=Integer.MAX_VALUE ? random.nextInt((int)bound) : random.nextLong()%bound;
	}

	static Constructor<AmongList, Dynamic> boolBiOp(
			BiFunction<DynamicBool, DynamicBool, Dynamic> boolFn
	){
		return binaryCondition((a, b, reportHandler) -> {
			DynamicBool b1 = Dynamic.DYNAMIC_BOOL.construct(a, reportHandler);
			DynamicBool b2 = Dynamic.DYNAMIC_BOOL.construct(b, reportHandler);
			if(b1==null||b2==null) return null;
			return boolFn.apply(b1, b2);
		});
	}
	static Constructor<AmongList, Dynamic> intBiOp(
			BiFunction<DynamicInt, DynamicInt, Dynamic> intFn
	){
		return binaryCondition((a, b, reportHandler) -> {
			DynamicInt i1 = Dynamic.DYNAMIC_INT.construct(a, reportHandler);
			DynamicInt i2 = Dynamic.DYNAMIC_INT.construct(b, reportHandler);
			if(i1==null||i2==null) return null;
			return intFn.apply(i1, i2);
		});
	}
	static Constructor<AmongList, Dynamic> numberBiOp(
			BiFunction<DynamicNumber, DynamicNumber, Dynamic> numFn
	){
		return binaryCondition((a, b, reportHandler) -> {
			DynamicNumber n1 = Dynamic.DYNAMIC_NUMBER.construct(a, reportHandler);
			DynamicNumber n2 = Dynamic.DYNAMIC_NUMBER.construct(b, reportHandler);
			if(n1==null||n2==null) return null;
			return numFn.apply(n1, n2);
		});
	}
	static Constructor<AmongList, Dynamic> numberBiOp(
			BiFunction<DynamicInt, DynamicInt, Dynamic> intFn,
			BiFunction<DynamicNumber, DynamicNumber, Dynamic> numFn
	){
		return binaryCondition((a, b, reportHandler) -> {
			DynamicNumber n1 = Dynamic.DYNAMIC_NUMBER.construct(a, reportHandler);
			DynamicNumber n2 = Dynamic.DYNAMIC_NUMBER.construct(b, reportHandler);
			if(n1==null||n2==null) return null;
			return n1 instanceof DynamicInt&&n2 instanceof DynamicInt ?
					intFn.apply((DynamicInt)n1, (DynamicInt)n2) :
					numFn.apply(n1, n2);
		});
	}
	static Constructor<AmongList, Dynamic> numberBiOp(
			BiFunction<DynamicInt, DynamicInt, Dynamic> intFn,
			BiFunction<DynamicNumber, DynamicNumber, Dynamic> numFn,
			Consumer<ConditionedListConstructorBuilder<Dynamic>> consumer
	){
		return listConditions(_b -> consumer.accept(_b.addBinary(
				(a, b, reportHandler) -> {
					DynamicNumber n1 = Dynamic.DYNAMIC_NUMBER.construct(a, reportHandler);
					DynamicNumber n2 = Dynamic.DYNAMIC_NUMBER.construct(b, reportHandler);
					if(n1==null||n2==null) return null;
					return n1 instanceof DynamicInt&&n2 instanceof DynamicInt ?
							intFn.apply((DynamicInt)n1, (DynamicInt)n2) :
							numFn.apply(n1, n2);
				})));
	}
	static Constructor<AmongList, Dynamic> biOp(
			BiFunction<DynamicInt, DynamicInt, Dynamic> intFn,
			BiFunction<DynamicNumber, DynamicNumber, Dynamic> numFn,
			BiFunction<DynamicBool, DynamicBool, Dynamic> boolFn,
			BiFunction<Dynamic, Dynamic, Dynamic> fn
	){
		return binaryCondition((a, b, reportHandler) -> {
			Dynamic o1 = Dynamic.DYNAMIC.construct(a, reportHandler);
			Dynamic o2 = Dynamic.DYNAMIC.construct(b, reportHandler);
			if(o1==null||o2==null) return null;
			if(o1 instanceof DynamicInt&&o2 instanceof DynamicInt)
				return intFn.apply((DynamicInt)o1, (DynamicInt)o2);
			if(o1 instanceof DynamicNumber&&o2 instanceof DynamicNumber)
				return numFn.apply((DynamicNumber)o1, (DynamicNumber)o2);
			if(o1 instanceof DynamicBool&&o2 instanceof DynamicBool)
				return boolFn.apply((DynamicBool)o1, (DynamicBool)o2);
			return fn.apply(o1, o2);
		});
	}
}
