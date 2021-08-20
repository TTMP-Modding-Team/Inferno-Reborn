package ttmp.wtf.obj;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class Range implements Iterable<Double>{
	private final double from;
	private final double to;

	public Range(double from, double to){
		this.from = from;
		this.to = to;
	}

	public double getFrom(){
		return from;
	}
	public double getTo(){
		return to;
	}

	@Override public Iterator<Double> iterator(){
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

	public static final class RangeIterator implements Iterator<Double>{
		private final double from, to;
		private int nextIndex;

		public RangeIterator(double from, double to){
			this.from = from;
			this.to = to;
		}

		@Override public boolean hasNext(){
			return (to>from ? to-from : from-to)<nextIndex;
		}
		@Override public Double next(){
			if(!hasNext()) throw new NoSuchElementException();
			nextIndex++;
			return from<to ?
					Math.min(to, from+nextIndex) :
					Math.max(to, from-nextIndex);
		}
	}
}
