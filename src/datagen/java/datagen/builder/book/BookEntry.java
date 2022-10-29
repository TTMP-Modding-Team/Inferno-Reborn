package datagen.builder.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import datagen.builder.book.page.BookPage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BookEntry{
	public final String id;
	public final String name;
	public final ResourceLocation category;
	public final Stack icon;

	public final List<BookPage> pages = new ArrayList<>();

	@Nullable private ResourceLocation advancement;
	private boolean priority;
	private boolean secret;
	private boolean readByDefault;

	public BookEntry(String id, String name, ResourceLocation category, Stack icon){
		this.id = id;
		this.name = name;
		this.category = category;
		this.icon = icon;
	}

	public BookEntry advancement(ResourceLocation advancement){
		this.advancement = advancement;
		return this;
	}

	public BookEntry priority(boolean priority){
		this.priority = priority;
		return this;
	}

	public BookEntry secret(boolean secret){
		this.secret = secret;
		return this;
	}

	public BookEntry readByDefault(boolean readByDefault){
		this.readByDefault = readByDefault;
		return this;
	}

	public BookEntry page(BookPage page){
		this.pages.add(page);
		return this;
	}

	public JsonObject serialize(){
		JsonObject o = new JsonObject();
		o.addProperty("name", this.name);
		o.addProperty("category", this.category.toString());
		o.addProperty("icon", this.icon.toString());
		o.add("pages", pages.stream().map(BookPage::serialize).collect(JsonArray::new, JsonArray::add, (a1, a2) -> {}));
		if(advancement!=null) o.addProperty("advancement", advancement.toString());
		if(priority) o.addProperty("priority", true);
		if(secret) o.addProperty("secret", true);
		if(readByDefault) o.addProperty("read_by_default", true);
		return o;
	}
}
