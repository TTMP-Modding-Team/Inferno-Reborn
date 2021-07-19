package ttmp.infernoreborn.datagen.builder.book.page;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public class TemplatePage extends BookPage{
	private final ResourceLocation type;
	private final Map<String, Object> parameters = new LinkedHashMap<>();

	public TemplatePage(ResourceLocation type){
		this.type = type;
	}

	public TemplatePage param(String key, Object value){
		parameters.put(key, value);
		return this;
	}

	@Override public String type(){
		return this.type.toString();
	}
	@Override protected void doSerialize(JsonObject object){
		for(Map.Entry<String, Object> e : parameters.entrySet()){
			object.addProperty(e.getKey(), e.getValue().toString());
		}
	}
}
