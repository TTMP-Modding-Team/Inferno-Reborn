package ttmp.infernoreborn.util;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WeightedPool<T>{
	private final Object2IntMap<T> items;
	private final int nullWeight;
	private final int totalWeight;

	public WeightedPool(Object2IntMap<T> items){
		this(items, 0);
	}
	public WeightedPool(Object2IntMap<T> items, int nullWeight){
		Object2IntMap<T> items2 = new Object2IntOpenHashMap<>();
		for(Entry<T> e : items.object2IntEntrySet())
			if(e.getIntValue()>0) items2.put(e.getKey(), e.getIntValue());
		this.items = Object2IntMaps.unmodifiable(items2);
		this.nullWeight = Math.max(0, nullWeight);
		int wgt = nullWeight;
		for(Entry<T> e : items.object2IntEntrySet()){
			wgt += e.getIntValue();
		}
		this.totalWeight = wgt;
	}

	public Object2IntMap<T> getItems(){
		return items;
	}
	public int getNullWeight(){
		return nullWeight;
	}

	public boolean isEmpty(){
		return items.isEmpty();
	}

	@Nullable public T nextItem(Random random){
		return weightedRandomItem(items, totalWeight, random);
	}
	@Nullable public T nextItem(Random random, @Nullable Collection<T> exclusions){
		if(exclusions==null||exclusions.isEmpty()) return nextItem(random);
		int wgt = totalWeight;
		Object2IntMap<T> m = new Object2IntOpenHashMap<>(items);
		for(T exclusion : exclusions) wgt -= m.removeInt(exclusion);
		return weightedRandomItem(m, wgt, random);
	}
	@Nullable public T nextItem(Random random, @Nullable Predicate<T> filter){
		if(filter==null) return nextItem(random);
		Object2IntMap<T> m = new Object2IntOpenHashMap<>(items);
		int wgt = totalWeight;
		for(ObjectIterator<Entry<T>> it = m.object2IntEntrySet().iterator(); it.hasNext(); ){
			Entry<T> exclusion = it.next();
			if(!filter.test(exclusion.getKey())){
				wgt -= exclusion.getIntValue();
				it.remove();
			}
		}
		return weightedRandomItem(m, wgt, random);
	}

	@Nullable protected static <T> T weightedRandomItem(Object2IntMap<T> pool, int totalWeight, Random random){
		if(totalWeight<=0||pool.isEmpty()) return null;
		int r = random.nextInt(totalWeight);
		for(Entry<T> e : pool.object2IntEntrySet()){
			r -= e.getIntValue();
			if(r<0) return e.getKey();
		}
		return null;
	}

	@Override public String toString(){
		return "WeightedPool{"+items.object2IntEntrySet().stream().map(e -> e.getKey()+":"+e.getIntValue()).collect(Collectors.joining(","))+'}';
	}

	public static <T> Builder<T> builder(){
		return new Builder<>();
	}

	public static final class Builder<T>{
		private final Object2IntMap<T> pool = new Object2IntArrayMap<>();
		private int nullWeight = 0;

		public Builder<T> add(@Nullable T t, int weight){
			if(weight<0) throw new IllegalArgumentException("weight");
			if(t==null) nullWeight = weight;
			else pool.put(t, weight);
			return this;
		}

		public Builder<T> addAll(WeightedPool<? extends T> pool){
			for(Entry<? extends T> e : pool.items.object2IntEntrySet())
				add(e.getKey(), e.getIntValue());
			if(pool.getNullWeight()>0)
				add(null, pool.getNullWeight());
			return this;
		}

		public WeightedPool<T> build(){
			return new WeightedPool<>(pool, nullWeight);
		}
	}
}
