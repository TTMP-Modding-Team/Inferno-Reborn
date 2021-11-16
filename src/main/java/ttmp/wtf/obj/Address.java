package ttmp.wtf.obj;

import java.util.Arrays;

public final class Address{
	public static final Address EMPTY = new Address();

	private final String[] identifiers;

	public Address(String... identifiers){
		this.identifiers = Arrays.copyOf(identifiers, identifiers.length);
	}

	public int size(){
		return identifiers.length;
	}

	public String get(int index){
		return identifiers[index];
	}

	@Override public boolean equals(Object o){
		if(this==o) return true;
		if(o==null||getClass()!=o.getClass()) return false;
		Address address = (Address)o;
		return Arrays.equals(identifiers, address.identifiers);
	}
	@Override public int hashCode(){
		return Arrays.hashCode(identifiers);
	}

	@Override public String toString(){
		return String.join(".", identifiers);
	}
}
