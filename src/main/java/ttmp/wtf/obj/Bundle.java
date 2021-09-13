package ttmp.wtf.obj;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Bundle implements Iterable<Object>{
	private final Object[] objects;

	public Bundle(Object... objects){
		this.objects = Objects.requireNonNull(objects);
	}

	public int size(){
		return objects.length;
	}

	public Object get(int i){
		return objects[i];
	}

	@Override public boolean equals(Object o){
		if(this==o) return true;
		if(o==null||getClass()!=o.getClass()) return false;
		Bundle bundle = (Bundle)o;
		return Arrays.equals(objects, bundle.objects);
	}

	@Override public int hashCode(){
		return Arrays.hashCode(objects);
	}

	@Override public String toString(){
		return Arrays.stream(objects)
				.map(Objects::toString)
				.collect(Collectors.joining(", "));
	}

	@Override public Iterator<Object> iterator(){
		return Arrays.asList(objects).iterator();
	}
}
