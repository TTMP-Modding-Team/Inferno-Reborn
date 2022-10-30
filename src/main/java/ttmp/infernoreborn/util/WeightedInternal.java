package ttmp.infernoreborn.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.List;
import java.util.Random;
import java.util.function.ToIntFunction;

final class WeightedInternal{
	static <T> int[] weights(List<T> objects, ToIntFunction<T> weightFunction){
		return objects.stream().mapToInt(weightFunction).toArray();
	}
	static <T extends Weighted> int[] weights(List<T> objects){
		return objects.stream().mapToInt(Weighted::weight).toArray();
	}

	static long nextLong(Random random, long bound){
		if(bound<0) throw new IllegalArgumentException("bound must be positive");
		return bound<=Integer.MAX_VALUE ? random.nextInt((int)bound) : random.nextLong()%bound;
	}

	static int pickIndex(Random random, int[] weights){
		long wgtSum = 0;
		for(int w : weights) if(w>0) wgtSum += w;
		if(wgtSum==0) return -1;
		long wgt = nextLong(random, wgtSum);
		for(int i = 0; i<weights.length; i++){
			if(wgt<weights[i]) return i;
			if(weights[i]>0) wgt -= weights[i];
		}
		return -1;
	}

	static int[] pickMultipleIndex(Random random, int[] weights, int quantity){
		if(quantity<=0) return new int[0];

		long wgtSum = 0;
		for(int w : weights) if(w>0) wgtSum += w;
		if(wgtSum==0) return new int[0];

		IntList indices = new IntArrayList();

		while(quantity>0){
			long wgt = nextLong(random, wgtSum);
			for(int i = 0; i<weights.length; i++){
				if(wgt<weights[i]){
					indices.add(i);
					wgtSum -= weights[i];
					weights[i] = 0;
					break;
				}
				if(weights[i]>0) wgt -= weights[i];
			}
			quantity--;
		}

		return indices.toIntArray();
	}
}
