package ttmp.wtf;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ttmp.wtf.WtfScript.NAME_PATTERN;

public final class CompileContext{
	public static final CompileContext DEFAULT = builder().build();

	private final Map<String, Object> staticConstants;
	private final Map<String, Class<?>> dynamicConstants;

	public CompileContext(Map<String, Object> staticConstants, Map<String, Class<?>> dynamicConstants){
		this.staticConstants = ImmutableMap.copyOf(staticConstants);
		this.dynamicConstants = ImmutableMap.copyOf(dynamicConstants);
	}

	public Map<String, Object> getStaticConstants(){
		return staticConstants;
	}
	public Map<String, Class<?>> getDynamicConstants(){
		return dynamicConstants;
	}
	@Nullable public Object getStaticConstant(String literal){
		return staticConstants.get(literal);
	}
	@Nullable public Class<?> getDynamicConstant(String literal){
		return dynamicConstants.get(literal);
	}

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private final Map<String, Object> staticConstants = new HashMap<>();
		private final Map<String, Class<?>> dynamicConstants = new HashMap<>();

		public Builder addStaticConstant(String id, Object object){
			Objects.requireNonNull(object);
			if(!NAME_PATTERN.matcher(id).matches())
				throw new IllegalArgumentException("Invalid constant ID '"+id+"'");
			if(dynamicConstants.containsKey(id)||staticConstants.put(id, object)!=null)
				throw new IllegalStateException("Duplicated registration of constant with ID '"+id+"'");
			return this;
		}

		public Builder addDynamicConstant(String id, Class<?> type){
			Objects.requireNonNull(type);
			if(!NAME_PATTERN.matcher(id).matches())
				throw new IllegalArgumentException("Invalid constant ID '"+id+"'");
			if(staticConstants.containsKey(id)||dynamicConstants.put(id, type)!=null)
				throw new IllegalStateException("Duplicated registration of constant with ID '"+id+"'");
			return this;
		}

		public CompileContext build(){
			return new CompileContext(staticConstants, dynamicConstants);
		}
	}
}