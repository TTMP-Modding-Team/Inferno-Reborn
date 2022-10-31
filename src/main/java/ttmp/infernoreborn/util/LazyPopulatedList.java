package ttmp.infernoreborn.util;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

public abstract class LazyPopulatedList<O, E> extends AbstractList<E> implements RandomAccess{
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
