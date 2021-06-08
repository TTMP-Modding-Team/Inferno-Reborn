package ttmp.infernoreborn.ability.generator.parser;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public class ObjectParser<T>{
	private final Map<String, Function<JsonObject, T>> hintToParser = new HashMap<>();

	public ObjectParser<T> add(String hint, Function<JsonObject, T> parser){
		this.hintToParser.put(hint, parser);
		return this;
	}

	public T parseOrThrow(JsonObject object){
		T t = parseOrNull(object);
		if(t==null) throw new JsonParseException("No hint provided for parsing object");
		return t;
	}

	@Nullable public T parseOrNull(JsonObject object){
		Function<JsonObject, T> selected = null;

		for(Entry<String, Function<JsonObject, T>> e : hintToParser.entrySet()){
			if(object.has(e.getKey())){
				if(selected!=null) throw new JsonParseException("Multiple hints are present for parsing object");
				selected = e.getValue();
			}
		}
		return selected!=null ? selected.apply(object) : null;
	}
}
