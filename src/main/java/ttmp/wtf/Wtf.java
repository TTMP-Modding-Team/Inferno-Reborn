package ttmp.wtf;

import java.util.Random;

public final class Wtf{
	private Wtf(){}

	public static boolean equals(Object o1, Object o2){
		if(!(o1 instanceof Number)) return o1.equals(o2);
		if(!(o2 instanceof Number)) return false;
		return Double.compare(((Number)o1).doubleValue(), ((Number)o2).doubleValue())==0;
	}

	public static Number add(Number n1, Number n2){
		if(n1 instanceof Integer&&n2 instanceof Integer) return n1.intValue()+n2.intValue();
		else return n1.doubleValue()+n2.doubleValue();
	}

	public static Number subtract(Number n1, Number n2){
		if(n1 instanceof Integer&&n2 instanceof Integer) return n1.intValue()-n2.intValue();
		else return n1.doubleValue()-n2.doubleValue();
	}

	public static Number subtractr(Number n1, Number n2){
		if(n1 instanceof Integer&&n2 instanceof Integer) return n2.intValue()-n1.intValue();
		else return n2.doubleValue()-n1.doubleValue();
	}

	public static Number multiply(Number n1, Number n2){
		if(n1 instanceof Integer&&n2 instanceof Integer) return n1.intValue()*n2.intValue();
		else return n1.doubleValue()*n2.doubleValue();
	}

	/**
	 * @throws ArithmeticException If {@code n1} and {@code n2} is both integers and {@code n2} is 0
	 */
	public static Number divide(Number n1, Number n2){
		if(n1 instanceof Integer&&n2 instanceof Integer) return n1.intValue()/n2.intValue();
		else return n1.doubleValue()/n2.doubleValue();
	}

	/**
	 * {@link Wtf#divide(Number, Number)} but parameter is flipped, TODO remove it lmao
	 * @throws ArithmeticException If {@code n1} and {@code n2} is both integers and {@code n1} is 0
	 */
	public static Number divider(Number n1, Number n2){
		if(n1 instanceof Integer&&n2 instanceof Integer) return n2.intValue()/n1.intValue();
		else return n2.doubleValue()/n1.doubleValue();
	}

	public static Number negate(Number n){
		return n instanceof Integer ? -n.intValue() : -n.doubleValue();
	}

	/**
	 * @return Random int between {@code a} and {@code b}, both inclusive
	 */
	public static int randomInt(Random random, int a, int b){
		if(a==b) return a;
		int min = Math.min(a, b), max = Math.max(a, b);
		return random.nextInt(max-min+1)+min;
	}
}
