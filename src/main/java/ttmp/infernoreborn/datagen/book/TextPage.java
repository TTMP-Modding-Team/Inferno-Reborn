package ttmp.infernoreborn.datagen.book;

import com.google.gson.JsonObject;

import javax.annotation.Nullable;

public class TextPage extends BookPage{
	public final String text;
	@Nullable public final String title;

	public TextPage(String text){
		this(text, null);
	}
	public TextPage(String text, @Nullable String title){
		this.text = text;
		this.title = title;
	}

	@Override public String type(){
		return "text";
	}
	@Override protected void doSerialize(JsonObject object){
		object.addProperty("text", text);
		if(title!=null) object.addProperty("title", title);
	}
}
