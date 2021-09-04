package ttmp.wtf.obj;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class Range implements Iterable<Integer>{
	private final int from;
	private final int to;

	public Range(int from, int to){
		this.from = from;
		this.to = to;
	}

	public int getFrom(){
		return from;
	}
	public int getTo(){
		return to;
	}

	@Override public Iterator<Integer> iterator(){
		return new RangeIterator(from, to);
	}

	@Override public boolean equals(Object o){
		if(this==o) return true;
		if(o==null||getClass()!=o.getClass()) return false;
		Range range = (Range)o;
		return Double.compare(range.from, from)==0&&Double.compare(range.to, to)==0;
	}
	@Override public int hashCode(){
		return Objects.hash(from, to);
	}

	@Override public String toString(){
		return from+".."+to;
	}

	private static final class RangeIterator implements Iterator<Integer>{
		private final int to;
		private int next;
		private final boolean reverse;

		public RangeIterator(int from, int to){
			this.next = from;
			this.to = to;
			this.reverse = from>to;
		}

		@Override public boolean hasNext(){
			return reverse ? next>=to : next<=to;
		}
		@Override public Integer next(){
			if(!hasNext()) throw new NoSuchElementException();
			int n = next;
			if(reverse) next--;
			else next++;
			return n;
		}
	}
}
