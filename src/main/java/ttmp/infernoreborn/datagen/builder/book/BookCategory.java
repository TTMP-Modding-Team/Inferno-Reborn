package ttmp.infernoreborn.datagen.builder.book;

import com.google.gson.JsonObject;

public class BookCategory{
	public final String id;
	public final String name;
	public final String description;
	public final Stack icon;

	public int sortnum;

	public BookCategory(String id, String name, Stack icon){
		this(id, name, name+".desc", icon);
	}
	public BookCategory(String id, String name, String description, Stack icon){
		this.id = id;
		this.name = name;
		this.description = description;
		this.icon = icon;
	}

	public BookCategory sortnum(int sortnum){
		this.sortnum = sortnum;
		return this;
	}

	public JsonObject serialize(){
		JsonObject o = new JsonObject();
		o.addProperty("name", name);
		o.addProperty("description", description);
		o.addProperty("icon", icon.toString());
		if(sortnum!=0) o.addProperty("sortnum", sortnum);
		return o;
	}
}
