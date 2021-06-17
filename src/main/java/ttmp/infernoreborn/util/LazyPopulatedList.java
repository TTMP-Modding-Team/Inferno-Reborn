package ttmp.infernoreborn.util;

import com.google.common.collect.ImmutableList;
import ttmp.infernoreborn.InfernoReborn;

import javax.annotation.Nullable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class LazyPopulatedList<O, E> extends AbstractList<E>{
	private final Collection<O> collection;

	@Nullable private List<E> delegate;

	public LazyPopulatedList(Collection<O> collection){
		this.collection = collection;
	}

	public void sync(){
		ImmutableList.Builder<E> b = new ImmutableList.Builder<>();
		for(O o : collection) populate(o, b);
		delegate = b.build();
		if(delegate.isEmpty()) delegate = null;
		else InfernoReborn.LOGGER.debug("List populated with "+delegate.stream().map(Object::toString).collect(Collectors.joining(", ")));
	}

	protected abstract void populate(O o, ImmutableList.Builder<E> b);

	@Override public E get(int index){
		if(delegate!=null) return delegate.get(index);
		throw new IndexOutOfBoundsException();
	}
	@Override public int size(){
		return delegate!=null ? delegate.size() : 0;
	}
}
