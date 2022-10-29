package ttmp.infernoreborn.util;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

@FunctionalInterface
public interface Weighted{
	/**
	 * Get weight of the object. Expected to be constant.
	 *
	 * @return Weight of the object
	 */
	int weight();

	@Nullable static <T> T pick(Random random, T[] objects, ToIntFunction<T> weightFunction){
		return pick(random, Arrays.asList(objects), weightFunction);
	}
	@Nullable static <T> T pick(Random random, List<T> objects, ToIntFunction<T> weightFunction){
		switch(objects.size()){
			case 0: return null;
			case 1:{
				T t = objects.get(0);
				return weightFunction.applyAsInt(t)>0 ? t : null;
			}
		}
		int i = WeightedInternal.pickIndex(random, WeightedInternal.weights(objects, weightFunction));
		return i>=0 ? objects.get(i) : null;
	}

	@Nullable static <T extends Weighted> T pick(Random random, T[] objects){
		return pick(random, Arrays.asList(objects));
	}
	@Nullable static <T extends Weighted> T pick(Random random, List<T> objects){
		switch(objects.size()){
			case 0: return null;
			case 1:{
				T t = objects.get(0);
				return t.weight()>0 ? t : null;
			}
		}
		int i = WeightedInternal.pickIndex(random, WeightedInternal.weights(objects));
		return i>=0 ? objects.get(i) : null;
	}

	static <T> List<T> pickMultiple(Random random, T[] objects, int quantity, ToIntFunction<T> weightFunction){
		return pickMultiple(random, Arrays.asList(objects), quantity, weightFunction);
	}
	static <T> List<T> pickMultiple(Random random, List<T> objects, int quantity, ToIntFunction<T> weightFunction){
		if(quantity<=0||objects.isEmpty()) return Collections.emptyList();
		return Arrays.stream(WeightedInternal.pickMultipleIndex(random,
						WeightedInternal.weights(objects, weightFunction), quantity))
				.mapToObj(objects::get).collect(Collectors.toList());
	}

	static <T extends Weighted> List<T> pickMultiple(Random random, T[] objects, int quantity){
		return pickMultiple(random, Arrays.asList(objects), quantity);
	}
	static <T extends Weighted> List<T> pickMultiple(Random random, List<T> objects, int quantity){
		if(quantity<=0||objects.isEmpty()) return Collections.emptyList();
		return Arrays.stream(WeightedInternal.pickMultipleIndex(random,
						WeightedInternal.weights(objects), quantity))
				.mapToObj(objects::get).collect(Collectors.toList());
	}
}
