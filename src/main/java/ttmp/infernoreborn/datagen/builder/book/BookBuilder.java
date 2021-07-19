package ttmp.infernoreborn.datagen.builder.book;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

public final class BookBuilder{
	public final ResourceLocation id;
	public final String name;
	public final String landingText;

	public final Map<ResourceLocation, BookCategory> categories = new HashMap<>();
	public final ListMultimap<String, BookComponent> templates = ArrayListMultimap.create();

	@Nullable public String fillerTexture;
	@Nullable public String version;
	@Nullable public String subtitle;
	public boolean dontGenerateBook;
	@Nullable public Stack customBookItem;
	public boolean i18n;

	public BookBuilder(ResourceLocation id, String name, String landingText){
		this.id = id;
		this.name = name;
		this.landingText = landingText;
	}

	public BookBuilder fillerTexture(String fillerTexture){
		this.fillerTexture = fillerTexture;
		return this;
	}
	public BookBuilder version(String version){
		this.version = version;
		return this;
	}
	public BookBuilder subtitle(String subtitle){
		this.subtitle = subtitle;
		return this;
	}
	public BookBuilder dontGenerateBook(boolean dontGenerateBook){
		this.dontGenerateBook = dontGenerateBook;
		return this;
	}
	public BookBuilder customBookItem(Stack customBookItem){
		this.customBookItem = customBookItem;
		return this;
	}
	public BookBuilder i18n(boolean i18n){
		this.i18n = i18n;
		return this;
	}

	public BookBuilder category(BookCategory category){
		if(this.categories.put(new ResourceLocation(id.getNamespace(), category.id), category)!=null)
			throw new IllegalStateException("Duplicated category "+category.id);
		return this;
	}

	public BookBuilder template(String id, BookComponent... components){
		if(components.length==0) throw new IllegalArgumentException("No components");
		if(this.templates.containsKey(id)) throw new IllegalStateException("Template with id '"+id+"' already exists");
		this.templates.putAll(id, Arrays.asList(components));
		return this;
	}

	public JsonObject serialize(){
		JsonObject object = new JsonObject();
		object.addProperty("name", name);
		object.addProperty("landing_text", landingText);
		if(fillerTexture!=null) object.addProperty("filler_texture", fillerTexture);
		if(version!=null) object.addProperty("version", version);
		if(subtitle!=null) object.addProperty("subtitle", subtitle);
		if(dontGenerateBook) object.addProperty("dont_generate_book", true);
		if(customBookItem!=null) object.addProperty("custom_book_item", customBookItem.toString());
		if(i18n) object.addProperty("i18n", true);
		return object;
	}

	public void makeEntryAndSave(Consumer<Consumer<BookEntry>> entryFactory, BookFileGenerator generator){
		Set<String> entryNames = new HashSet<>();
		List<BookEntry> bookEntries = new ArrayList<>();
		entryFactory.accept(bookEntry -> {
			if(!entryNames.add(bookEntry.id))
				throw new IllegalStateException("Duplicated entry with id "+bookEntry.id);
			if(categories.get(bookEntry.category)==null)
				throw new IllegalStateException("Invalid category "+bookEntry.category);
			bookEntries.add(bookEntry);
		});
		generator.saveBook(id, this.serialize());
		for(BookCategory bookCategory : categories.values())
			generator.saveCategory(id, bookCategory.id, bookCategory.serialize());
		for(Entry<String, Collection<BookComponent>> e : templates.asMap().entrySet()){
			JsonObject o = new JsonObject();
			JsonArray a = new JsonArray();
			for(BookComponent c : e.getValue()) a.add(c.serialize());
			o.add("components", a);
			generator.saveTemplate(id, e.getKey(), o);
		}
		for(BookEntry bookEntry : bookEntries)
			generator.saveEntry(id, bookEntry.category.getPath(), bookEntry.id, bookEntry.serialize());
	}
}
