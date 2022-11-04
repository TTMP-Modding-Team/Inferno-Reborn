package ttmp.infernoreborn.api;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Object representing result for simulated actions.<br>
 * Simulate results can either be failure or success. Succeed actions have an action associated that applies the
 * simulated action to actual state.
 *
 * @param <T> Result of applying succeeded result
 */
public final class Simulation<T>{
	private static final Simulation<?> FAIL = new Simulation<>(null);

	@SuppressWarnings("unchecked")
	public static <T> Simulation<T> fail(){
		return (Simulation<T>)FAIL;
	}
	public static Simulation<Void> success(){
		return new Simulation<>(() -> null);
	}
	public static <T> Simulation<T> success(Supplier<T> applyAction){
		return new Simulation<>(Objects.requireNonNull(applyAction));
	}
	@SafeVarargs public static <T> Simulation<List<T>> combineAsList(Simulation<T> delegate, Simulation<T>... otherDelegates){
		if(!delegate.isSuccess()) return fail();
		for(Simulation<T> d : otherDelegates)
			if(!d.isSuccess()) return fail();
		return new Simulation<>(() -> {
			List<T> results = new ArrayList<>(otherDelegates.length+1);
			results.add(delegate.apply());
			for(Simulation<T> d : otherDelegates) results.add(d.apply());
			return results;
		});
	}
	public static Simulation<Void> combineWithoutResult(Simulation<?> delegate, Simulation<?>... otherDelegates){
		if(!delegate.isSuccess()) return fail();
		for(Simulation<?> d : otherDelegates)
			if(!d.isSuccess()) return fail();
		return new Simulation<>(() -> {
			delegate.apply();
			for(Simulation<?> d : otherDelegates) d.apply();
			return null;
		});
	}

	@Nullable private final Supplier<T> applyAction;

	private Simulation(@Nullable Supplier<T> applyAction){
		this.applyAction = applyAction;
	}

	public <R> Simulation<R> ifThen(Function<T, R> function){
		if(this.isSuccess()) return new Simulation<>(() -> function.apply(this.apply()));
		else return fail();
	}

	public boolean isSuccess(){
		return applyAction!=null;
	}

	public T apply(){
		if(applyAction!=null) return applyAction.get();
		else throw new IllegalStateException("Cannot apply failure");
	}
	public T applyOr(T fallback){
		return applyAction!=null ? applyAction.get() : fallback;
	}
	public T applyOrGet(Supplier<T> fallback){
		return applyAction!=null ? applyAction.get() : fallback.get();
	}
	@Nullable public T applyOrNull(){
		return applyAction!=null ? applyAction.get() : null;
	}
}
