package ttmp.infernoreborn.ability.generator.pool;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class WeightedPool<T>{
	private final Object2IntMap<T> items = new Object2IntOpenHashMap<>();
	private final Object2IntMap<T> itemsView = Object2IntMaps.unmodifiable(items);

	private final int nullWeight;
	private final int totalWeight;

	public WeightedPool(Object2IntMap<T> items){
		this(items, 0);
	}
	public WeightedPool(Object2IntMap<T> items, int nullWeight){
		for(Entry<T> e : items.object2IntEntrySet())
			if(e.getIntValue()>0) this.items.put(e.getKey(), e.getIntValue());

		this.nullWeight = Math.max(0, nullWeight);
		int wgt = nullWeight;
		for(Entry<T> e : items.object2IntEntrySet()){
			wgt += e.getIntValue();
		}
		this.totalWeight = wgt;
	}

	public Object2IntMap<T> getItems(){
		return itemsView;
	}
	public int getNullWeight(){
		return nullWeight;
	}
	public int getTotalWeight(){
		return totalWeight;
	}

	public boolean isEmpty(){
		return items.isEmpty();
	}

	public T expectNextItem(Random random){
		T t = nextItem(random);
		return Objects.requireNonNull(t);
	}
	@Nullable public T nextItem(Random random){
		if(this.isEmpty()) return null;
		int r = random.nextInt(totalWeight);
		for(Entry<T> e : items.object2IntEntrySet()){
			r -= e.getIntValue();
			if(r<=0) return e.getKey();
		}
		return null;
	}

	@Override public String toString(){
		return "WeightedPool{"+items.object2IntEntrySet().stream().map(e -> e.getKey()+":"+e.getIntValue()).collect(Collectors.joining(","))+'}';
	}
}
