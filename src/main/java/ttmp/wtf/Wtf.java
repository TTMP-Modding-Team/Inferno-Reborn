package ttmp.wtf;

import java.util.Random;

/**
 * Bunch of utility methods for WtfScript.
 */
public final class Wtf{
	private Wtf(){}

	/**
	 * Equality check used in WtfScript.
	 *
	 * @return If both object is {@link Number}, comparison between two double values. Otherwise, result of {@code equals()} call.
	 */
	public static boolean equals(Object o1, Object o2){
		if(!(o1 instanceof Number)) return o1.equals(o2);
		if(!(o2 instanceof Number)) return false;
		return Double.compare(((Number)o1).doubleValue(), ((Number)o2).doubleValue())==0;
	}

	/**
	 * Addition method used in WtfScript.
	 *
	 * @return {@code a+b}. The result is {@link Integer} if both {@code a} and {@code b} are {@link Integer}.
	 */
	public static Number add(Number a, Number b){
		if(a instanceof Integer&&b instanceof Integer) return a.intValue()+b.intValue();
		else return a.doubleValue()+b.doubleValue();
	}

	/**
	 * Subtraction method used in WtfScript.
	 *
	 * @return {@code a-b}. The result is {@link Integer} if both {@code a} and {@code b} are {@link Integer}.
	 */
	public static Number subtract(Number a, Number b){
		if(a instanceof Integer&&b instanceof Integer) return a.intValue()-b.intValue();
		else return a.doubleValue()-b.doubleValue();
	}

	/**
	 * Multiplication method used in WtfScript.
	 *
	 * @return {@code a*b}. The result is {@link Integer} if both {@code a} and {@code b} are {@link Integer}.
	 */
	public static Number multiply(Number a, Number b){
		if(a instanceof Integer&&b instanceof Integer) return a.intValue()*b.intValue();
		else return a.doubleValue()*b.doubleValue();
	}

	/**
	 * Division method used in WtfScript.
	 *
	 * @return {@code a/b}. The result is {@link Integer} if both {@code a} and {@code b} are {@link Integer}.
	 * @throws ArithmeticException If {@code a} and {@code b} is both {@link Integer} and {@code b} is 0
	 */
	public static Number divide(Number a, Number b){
		if(a instanceof Integer&&b instanceof Integer) return a.intValue()/b.intValue();
		else return a.doubleValue()/b.doubleValue();
	}

	/**
	 * Negation method used in WtfScript.
	 *
	 * @return {@code -n}. The result is {@link Integer} if {@code n} is {@link Integer}.
	 */
	public static Number negate(Number n){
		if(n instanceof Integer) return -n.intValue();
		else return -n.doubleValue();
	}

	/**
	 * @return Random int between {@code a} and {@code b}, both inclusive
	 */
	public static int randomInt(Random random, int a, int b){
		if(a==b) return a;
		int min = Math.min(a, b), max = Math.max(a, b);
		return random.nextInt(max-min+1)+min;
	}

	public static boolean isIn(Object o, Iterable<?> iterable){
		for(Object o2 : iterable)
			if(equals(o2, o)) return true;
		return false;
	}
}
