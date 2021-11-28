package ttmp.wtf;

import ttmp.wtf.operation.WtfApply;
import ttmp.wtf.operation.WtfConstructor;
import ttmp.wtf.operation.WtfPropertyGet;
import ttmp.wtf.operation.WtfPropertySet;
import ttmp.wtf.operation.impl.SetConstructor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import static ttmp.wtf.WtfScript.NAME_PATTERN;

public class EvalContext{
	private static final EvalContext DEFAULT = new Builder()
			.constructor("Set", new SetConstructor())
			.build();

	private final Map<String, WtfConstructor> constructor = new HashMap<>();
	private final Map<Class<?>, Map<String, WtfPropertyGet>> propertyGet = new HashMap<>();
	private final Map<Class<?>, Map<String, WtfPropertySet>> propertySet = new HashMap<>();
	private final Map<Class<?>, WtfApply> apply = new HashMap<>();
	private final Map<String, Object> dynamicConstant = new HashMap<>();

	private EvalContext(Map<String, WtfConstructor> constructor,
	                   Map<Class<?>, Map<String, WtfPropertyGet>> propertyGet,
	                   Map<Class<?>, Map<String, WtfPropertySet>> propertySet,
	                   Map<Class<?>, WtfApply> apply,
	                   Map<String, Object> dynamicConstant){
		this.constructor.putAll(constructor);
		this.propertyGet.putAll(propertyGet);
		this.propertySet.putAll(propertySet);
		this.apply.putAll(apply);
		this.dynamicConstant.putAll(dynamicConstant);
	}

	@Nullable WtfConstructor getConstructor(String name){
		return constructor.get(name);
	}

	@Nullable WtfPropertyGet getProperty(List<Class<?>> containingTypes, String name){
		for(Class<?> t : containingTypes){
			Map<String, WtfPropertyGet> m = propertyGet.get(t);
			if(m==null) continue;
			WtfPropertyGet get = m.get(name);
			if(get!=null) return get;
		}
		return null;
	}

	@Nullable WtfPropertySet setProperty(List<Class<?>> containingTypes, String name){
		for(Class<?> t : containingTypes){
			Map<String, WtfPropertySet> m = propertySet.get(t);
			if(m==null) continue;
			WtfPropertySet set = m.get(name);
			if(set!=null) return set;
		}
		return null;
	}

	@Nullable WtfApply getApply(List<Class<?>> containingTypes){
		for(Class<?> t : containingTypes){
			WtfApply m = apply.get(t);
			if(m!=null) return m;
		}
		return null;
	}

	@Nullable Object getDynamicConstant(String name){
		return dynamicConstant.get(name);
	}

	/**
	 * @return Evaluation context with predefined standard properties and constructors.
	 */
	public static EvalContext getDefault(){
		return DEFAULT;
	}

	/**
	 * Build a context with contents of default evaluation context included.
	 *
	 * @return Builder
	 */
	public static Builder builder(){
		return builder(getDefault());
	}

	/**
	 * Build a context.
	 *
	 * @return An empty builder
	 */
	public static Builder emptyBuilder(){
		return builder(null);
	}

	/**
	 * Build a context.
	 *
	 * @return Builder with contents of parent context included, or empty builder if parent is {@code null}
	 */
	public static Builder builder(@Nullable EvalContext parent){
		return new Builder(parent);
	}

	public static final class Builder{
		private final Map<String, WtfConstructor> constructor = new HashMap<>();
		private final Map<Class<?>, Map<String, WtfPropertyGet>> propertyGet = new HashMap<>();
		private final Map<Class<?>, Map<String, WtfPropertySet>> propertySet = new HashMap<>();
		private final Map<Class<?>, WtfApply> apply = new HashMap<>();
		private final Map<String, Object> dynamicConstant = new HashMap<>();

		@Nullable private final EvalContext parent;

		public Builder(){
			this(null);
		}
		public Builder(@Nullable EvalContext parent){
			this.parent = parent;
		}

		public Builder constructor(String name, WtfConstructor constructor){
			Objects.requireNonNull(name);
			Objects.requireNonNull(constructor);
			if(!NAME_PATTERN.matcher(name).matches())
				throw new IllegalArgumentException("Invalid constructor name '"+name+"'");
			if(this.constructor.putIfAbsent(name, constructor)!=null)
				throw new IllegalStateException("Duplicated registration of constructor with name '"+name+"'");
			return this;
		}

		public Builder propertyGet(Class<?> type, String name, WtfPropertyGet get){
			Objects.requireNonNull(type);
			Objects.requireNonNull(name);
			Objects.requireNonNull(get);
			if(!NAME_PATTERN.matcher(name).matches())
				throw new IllegalArgumentException("Invalid property getter name '"+name+"'");
			if(this.propertyGet.computeIfAbsent(type, c -> new HashMap<>()).putIfAbsent(name, get)!=null)
				throw new IllegalStateException("Duplicated registration of property getter '"+type.getName()+"#"+name+"'");
			return this;
		}

		public Builder propertySet(Class<?> type, String name, WtfPropertySet set){
			Objects.requireNonNull(type);
			Objects.requireNonNull(name);
			Objects.requireNonNull(set);
			if(!NAME_PATTERN.matcher(name).matches())
				throw new IllegalArgumentException("Invalid constructor name '"+name+"'");
			if(this.propertySet.computeIfAbsent(type, c -> new HashMap<>()).putIfAbsent(name, set)!=null)
				throw new IllegalStateException("Duplicated registration of property setter '"+type.getName()+"#"+name+"'");
			return this;
		}

		public Builder apply(Class<?> type, WtfApply apply){
			Objects.requireNonNull(type);
			Objects.requireNonNull(apply);
			if(this.apply.putIfAbsent(type, apply)!=null)
				throw new IllegalStateException("Duplicated registration of apply function with type '"+type.getName()+"'");
			return this;
		}

		public Builder dynamicConstant(String name, Object dynamicConstant){
			Objects.requireNonNull(name);
			Objects.requireNonNull(dynamicConstant);
			if(!NAME_PATTERN.matcher(name).matches())
				throw new IllegalArgumentException("Invalid constant name '"+name+"'");
			if(this.dynamicConstant.putIfAbsent(name, dynamicConstant)!=null)
				throw new IllegalStateException("Duplicated registration of dynamic constant with name '"+name+"'");
			return this;
		}

		public EvalContext build(){
			if(parent!=null) {
				for(Entry<String, WtfConstructor> e : parent.constructor.entrySet())
					this.constructor.putIfAbsent(e.getKey(), e.getValue());
				for(Entry<Class<?>, Map<String, WtfPropertyGet>> e : parent.propertyGet.entrySet())
					this.propertyGet.putIfAbsent(e.getKey(), e.getValue());
				for(Entry<Class<?>, Map<String, WtfPropertySet>> e : parent.propertySet.entrySet())
					this.propertySet.putIfAbsent(e.getKey(), e.getValue());
				for(Entry<Class<?>, WtfApply> e : parent.apply.entrySet())
					this.apply.putIfAbsent(e.getKey(), e.getValue());
				for(Entry<String, Object> e : parent.dynamicConstant.entrySet())
					this.dynamicConstant.putIfAbsent(e.getKey(), e.getValue());
			}
			return new EvalContext(constructor, propertyGet, propertySet, apply, dynamicConstant);
		}
	}
}
