package ttmp.infernoreborn.infernaltype.dsl.dynamic;

import among.construct.ConditionedConstructorBuilder.ListConstructorBuilder;
import among.construct.Constructor;
import among.obj.AmongList;

import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static among.construct.ConditionedConstructor.listCondition;
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
		return listCondition(c -> c.minSize(2), (list, reportHandler) -> {
			DynamicBool a = Dynamic.DYNAMIC_BOOL.construct(list.get(0), reportHandler);
			DynamicBool b = Dynamic.DYNAMIC_BOOL.construct(list.get(1), reportHandler);
			if(a==null||b==null) return null;
			return boolFn.apply(a, b);
		});
	}
	static Constructor<AmongList, Dynamic> intBiOp(
			BiFunction<DynamicInt, DynamicInt, Dynamic> intFn
	){
		return listCondition(c -> c.minSize(2), (list, reportHandler) -> {
			DynamicInt a = Dynamic.DYNAMIC_INT.construct(list.get(0), reportHandler);
			DynamicInt b = Dynamic.DYNAMIC_INT.construct(list.get(1), reportHandler);
			if(a==null||b==null) return null;
			return intFn.apply(a, b);
		});
	}
	static Constructor<AmongList, Dynamic> numberBiOp(
			BiFunction<DynamicNumber, DynamicNumber, Dynamic> numFn
	){
		return listCondition(c -> c.minSize(2), (list, reportHandler) -> {
			DynamicNumber a = Dynamic.DYNAMIC_NUMBER.construct(list.get(0), reportHandler);
			DynamicNumber b = Dynamic.DYNAMIC_NUMBER.construct(list.get(1), reportHandler);
			if(a==null||b==null) return null;
			return numFn.apply(a, b);
		});
	}
	static Constructor<AmongList, Dynamic> numberBiOp(
			BiFunction<DynamicInt, DynamicInt, Dynamic> intFn,
			BiFunction<DynamicNumber, DynamicNumber, Dynamic> numFn
	){
		return listCondition(c -> c.minSize(2), (list, reportHandler) -> {
			DynamicNumber a = Dynamic.DYNAMIC_NUMBER.construct(list.get(0), reportHandler);
			DynamicNumber b = Dynamic.DYNAMIC_NUMBER.construct(list.get(1), reportHandler);
			if(a==null||b==null) return null;
			return a instanceof DynamicInt&&b instanceof DynamicInt ?
					intFn.apply((DynamicInt)a, (DynamicInt)b) :
					numFn.apply(a, b);
		});
	}
	static Constructor<AmongList, Dynamic> numberBiOp(
			BiFunction<DynamicInt, DynamicInt, Dynamic> intFn,
			BiFunction<DynamicNumber, DynamicNumber, Dynamic> numFn,
			Consumer<ListConstructorBuilder<Dynamic>> consumer
	){
		return listConditions(_b -> consumer.accept(_b.add(c -> c.minSize(2),
				(list, reportHandler) -> {
					DynamicNumber a = Dynamic.DYNAMIC_NUMBER.construct(list.get(0), reportHandler);
					DynamicNumber b = Dynamic.DYNAMIC_NUMBER.construct(list.get(1), reportHandler);
					if(a==null||b==null) return null;
					return a instanceof DynamicInt&&b instanceof DynamicInt ?
							intFn.apply((DynamicInt)a, (DynamicInt)b) :
							numFn.apply(a, b);
				})));
	}
	static Constructor<AmongList, Dynamic> biOp(
			BiFunction<DynamicInt, DynamicInt, Dynamic> intFn,
			BiFunction<DynamicNumber, DynamicNumber, Dynamic> numFn,
			BiFunction<DynamicBool, DynamicBool, Dynamic> boolFn,
			BiFunction<Dynamic, Dynamic, Dynamic> fn
	){
		return listCondition(c -> c.minSize(2), (list, reportHandler) -> {
			Dynamic a = Dynamic.DYNAMIC.construct(list.get(0), reportHandler);
			Dynamic b = Dynamic.DYNAMIC.construct(list.get(1), reportHandler);
			if(a==null||b==null) return null;
			if(a instanceof DynamicInt&&b instanceof DynamicInt)
				return intFn.apply((DynamicInt)a, (DynamicInt)b);
			if(a instanceof DynamicNumber&&b instanceof DynamicNumber)
				return numFn.apply((DynamicNumber)a, (DynamicNumber)b);
			if(a instanceof DynamicBool&&b instanceof DynamicBool)
				return boolFn.apply((DynamicBool)a, (DynamicBool)b);
			return fn.apply(a, b);
		});
	}
}
