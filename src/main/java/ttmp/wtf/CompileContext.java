package ttmp.wtf;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ttmp.wtf.WtfScript.NAME_PATTERN;

public final class CompileContext{
	private final WtfScriptEngine engine;
	private final Map<String, Object> staticConstants;

	public CompileContext(WtfScriptEngine engine, Map<String, Object> staticConstants){
		this.engine = engine;
		this.staticConstants = ImmutableMap.copyOf(staticConstants);
	}

	public WtfScriptEngine getEngine(){
		return engine;
	}

	public Map<String, Object> getStaticConstants(){
		return staticConstants;
	}
	@Nullable public Object getStaticConstant(String literal){
		return staticConstants.get(literal);
	}

	public static Builder builder(){
		return new Builder();
	}
	public static CompileContext createDefault(WtfScriptEngine engine){
		return builder().build(engine);
	}

	public static final class Builder{
		private final Map<String, Object> staticConstants = new HashMap<>();

		public Builder addStaticConstant(String id, Object object){
			Objects.requireNonNull(object);
			if(!NAME_PATTERN.matcher(id).matches())
				throw new IllegalArgumentException("Invalid constant ID '"+id+"'");
			if(staticConstants.put(id, object)!=null)
				throw new IllegalStateException("Duplicated registration of constant with ID '"+id+"'");
			return this;
		}

		public CompileContext build(WtfScriptEngine engine){
			return new CompileContext(engine, staticConstants);
		}
	}
}
