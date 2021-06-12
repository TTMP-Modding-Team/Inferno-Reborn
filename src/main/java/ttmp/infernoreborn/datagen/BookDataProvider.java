package ttmp.infernoreborn.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.ability.Ability;
import ttmp.infernoreborn.contents.Abilities;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.item.FixedAbilityItem;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import static ttmp.infernoreborn.InfernoReborn.MODID;
import static ttmp.infernoreborn.InfernoReborn.VERSION;

public class BookDataProvider implements IDataProvider{
	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.disableHtmlEscaping()
			.create();

	private final DataGenerator generator;

	public BookDataProvider(DataGenerator generator){
		this.generator = generator;
	}

	@Override public void run(DirectoryCache directoryCache){
		makeBook(directoryCache, getUnlocalizedName("text", "landingText"), VERSION);
		makeCategory(directoryCache, "quickstart", "minecraft:cobblestone");
		makeCategory(directoryCache, "abilities", ModItems.PRIMAL_INFERNO_SPARK.getId().toString());
		makeCategory(directoryCache, "items", ModItems.ESSENCE_HOLDER.getId().toString());
		for(Ability ability : Abilities.getRegistry()){
			new BookEntry(ability, directoryCache, this.generator).makeTextPage().makeEmptyPage().makeTextPage().build();
		}
		//makeAbilityEntry(directoryCache, ability);
	}

	public void makeBook(DirectoryCache directoryCache, String landingText, String version){
		JsonObject json = new JsonObject();
		json.addProperty("name", ModItems.BOOK_OF_THE_UNSPEAKABLE.get().getDescriptionId());
		json.addProperty("landing_text", landingText);
		json.addProperty("version", version);
		json.addProperty("dont_generate_book", true);
		json.addProperty("custom_book_item", ModItems.BOOK_OF_THE_UNSPEAKABLE.getId().toString());
		json.addProperty("i18n", true);
		Path path = createPath(this.generator.getOutputFolder(), "/book.json");
		trySave(directoryCache, json, path);
	}

	public void makeCategory(DirectoryCache directoryCache, String name, String icon){
		JsonObject json = new JsonObject();
		json.addProperty("name", getUnlocalizedName("category", name));
		json.addProperty("description", getUnlocalizedName("category", name+".desc"));
		json.addProperty("icon", icon);
		Path path = createPath(this.generator.getOutputFolder(), "/en_us/categories/"+name+".json");
		trySave(directoryCache, json, path);
	}

	public void makeAbilityEntry(DirectoryCache directoryCache, Ability ability){
		ResourceLocation id = Objects.requireNonNull(ability.getRegistryName());
		Path path = createPath(this.generator.getOutputFolder(), "/en_us/entries/abilities/"+id.getPath()+".json");

		ItemStack stack = new ItemStack(ModItems.INFERNO_SPARK.get());
		FixedAbilityItem.setAbilities(stack, new Ability[]{ability});

		JsonObject o = new JsonObject();

		o.addProperty("name", ability.getUnlocalizedName());
		o.addProperty("icon", ModItems.INFERNO_SPARK.getId()+""+stack.getTag());
		o.addProperty("category", MODID+":abilities");
		JsonArray pages = new JsonArray();
		JsonObject page = new JsonObject();
		page.addProperty("type", "text");
		page.addProperty("text", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce et tortor porttitor odio ultrices consequat. Etiam lacinia tempus nulla eu convallis. Nullam ultricies euismod felis, at vestibulum nulla tincidunt ac. Sed eu lacus ante. Nunc diam lorem, luctus vel pellentesque vitae, dapibus eget massa. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. In interdum laoreet lacinia. Mauris eget nulla massa. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nunc ultricies vulputate nibh, sed commodo odio gravida id.\n"+
				"\n"+
				"Nulla ligula elit, egestas non lacus eu, imperdiet interdum magna. Nunc ullamcorper lacus a turpis auctor sodales. Donec lectus nisl, tempus nec tellus posuere, accumsan tempor elit. Nunc sed dictum tellus, ut vehicula est. Aliquam non dolor eget augue hendrerit tristique. Nunc sed lacinia justo, efficitur rhoncus nunc. Proin bibendum, tortor id aliquam fringilla, mi magna consectetur massa, nec malesuada dui dolor sed nisi. Suspendisse finibus nibh lacinia pulvinar ornare. Donec vitae rutrum massa.\n"+
				"\n"+
				"Sed tincidunt vestibulum mauris a elementum. Praesent efficitur, nunc ut eleifend rhoncus, quam dui volutpat nisi, vel viverra sem purus at leo. In maximus neque quis lectus mattis egestas. Ut at interdum lorem. Sed eleifend commodo vestibulum. Aenean lectus nisi, efficitur non neque quis, euismod cursus mi. Nullam laoreet, nisl non pretium bibendum, arcu mi consequat libero, ac consequat elit velit id massa. Etiam blandit nunc turpis, ut condimentum eros ultricies eu. Maecenas mauris lorem, eleifend et ultrices in, mattis a arcu. Nulla maximus dolor id dolor tempor, sed rhoncus lectus euismod. Nulla id felis ac tortor rhoncus bibendum ut non risus. Donec varius risus odio, vitae viverra erat convallis eu. Sed non feugiat ante. Quisque ac condimentum libero, eget porttitor dui. Donec a leo scelerisque, facilisis lorem et, consequat dui.\n"+
				"\n"+
				"Nullam malesuada a dolor vel volutpat. Vivamus nisi velit, tincidunt in quam et, varius ultrices lacus. Praesent sagittis sem odio, vitae varius nisi tempor ut. Duis luctus porta euismod. Interdum et malesuada fames ac ante ipsum primis in faucibus. Integer ultrices mi ac est sodales, ut accumsan magna rhoncus. Donec facilisis nisi quis tincidunt suscipit. Aliquam ut diam nisi. Curabitur posuere sagittis nisi, at laoreet nisl. Nulla est elit, varius eu enim vel, aliquam volutpat tellus. Morbi eleifend lectus justo, eu varius eros tincidunt quis. Integer quis libero ac justo molestie posuere.\n"+
				"\n"+
				"Sed sagittis id dolor at tempor. Donec blandit diam sit amet facilisis porttitor. Suspendisse lectus justo, efficitur vel nisi non, luctus vehicula nisi. Nam mauris justo, porttitor ut dolor nec, euismod mattis diam. Proin orci sapien, ullamcorper id arcu sed, posuere mollis massa. Ut ultrices bibendum tincidunt. Nunc luctus pellentesque mauris, non pretium ligula convallis a. Nulla vitae felis lobortis, eleifend dolor a, semper turpis.");
		pages.add(page);
		o.add("pages", pages);
		trySave(directoryCache, o, path);
	}

	private void trySave(DirectoryCache directoryCache, JsonObject json, Path path){
		try{
			IDataProvider.save(GSON, directoryCache, json, path);
		}catch(IOException ex){
			InfernoReborn.LOGGER.error("Couldn't save patchouli book {}", path, ex);
		}
	}

	@Override public String getName(){
		return "The Book";
	}

	private static Path createPath(Path output, String filename){
		return output.resolve("data/"+MODID+"/patchouli_books/book_of_the_unspeakable"+filename);
	}

	private static String getUnlocalizedName(String type, String s){
		return type+"."+MODID+"."+s;
	}

	class BookEntry{
		private JsonObject contents;

		private Path path;
		private JsonArray pages;
		private String name;
		private String advancement;
		private String flag;
		private boolean priority = false;
		private boolean secret = false;
		private boolean read_by_default = false;
		private String turnin;

		private DirectoryCache directoryCache;
		public BookEntry(Ability ability, DirectoryCache directoryCache, DataGenerator generator){
			ResourceLocation id = Objects.requireNonNull(ability.getRegistryName());
			this.path = createPath(generator.getOutputFolder(), "/en_us/entries/abilities/"+id.getPath()+".json");
			this.name = id.getPath();
			this.contents = new JsonObject();
			this.directoryCache = directoryCache;
			this.pages = new JsonArray();

			ItemStack stack = new ItemStack(ModItems.INFERNO_SPARK.get());
			FixedAbilityItem.setAbilities(stack, new Ability[]{ability});

			contents.addProperty("name", ability.getUnlocalizedName());
			contents.addProperty("category", MODID+":abilities");
			contents.addProperty("icon", ModItems.INFERNO_SPARK.getId()+""+stack.getTag());
		}
		public void build(){
			contents.add("pages", pages);
			trySave(directoryCache, contents, path);
		}

		public BookEntry makeTextPage(){
			return makeTextPage(null);
		}
		public BookEntry makeImagePage(String images, boolean text){
			return makeImagePage(images, null, null, text);
		}
		public BookEntry makeCraftingPage(String recipe, boolean text){
			return makeCraftingPage(recipe, null, null, text);
		}
		public BookEntry makeSmeltingPage(String recipe, boolean text){
			return makeSmeltingPage(recipe, null, null, text);
		}
		public BookEntry makeMultiblockPage(String name, boolean text){
			return makeMultiblockPage(name, null, null, null, text);
		}
		public BookEntry makeEntityPage(String entity, boolean text){
			return makeEntityPage(entity, null, null, null, null, null, text);
		}
		public BookEntry makeSpotlightPage(String item){
			return makeSpotlightPage(item, null, null, false);
		}
		public BookEntry makeLinkPage(String url, String link_text){
			return makeLinkPage(null, url, link_text);
		}
		public BookEntry makeEmptyPage(){
			return makeEmptyPage(null);
		}
		public BookEntry makeTextPage(@Nullable String title){
			JsonObject o = new JsonObject();
			o.addProperty("type", "text");
			o.addProperty("text", getUnlocalizedText());
			if(title!=null) o.addProperty("title", title);
			this.pages.add(o);
			return this;
		}
		public BookEntry makeImagePage(String images, @Nullable String title, @Nullable Boolean border, boolean text){
			JsonObject o = new JsonObject();
			o.addProperty("type", "image");
			o.addProperty("images", images);
			if(title!=null) o.addProperty("title", title);
			if(border!=null) o.addProperty("border", border);
			if(text) o.addProperty("text", getUnlocalizedText());
			this.pages.add(o);
			return this;
		}
		public BookEntry makeCraftingPage(String recipe, @Nullable String recipe2, @Nullable String title, boolean text){
			JsonObject o = new JsonObject();
			o.addProperty("type", "crafting");
			o.addProperty("recipe", recipe);
			if(recipe2!=null) o.addProperty("recipe2", recipe2);
			if(title!=null) o.addProperty("title", title);
			if(text) o.addProperty("text", getUnlocalizedText());
			this.pages.add(o);
			return this;
		}
		public BookEntry makeSmeltingPage(String recipe, @Nullable String recipe2, @Nullable String title, boolean text){
			JsonObject o = new JsonObject();
			o.addProperty("type", "smelting");
			o.addProperty("recipe", recipe);
			if(recipe2!=null) o.addProperty("recipe2", recipe2);
			if(title!=null) o.addProperty("title", title);
			if(text) o.addProperty("text", getUnlocalizedText());
			this.pages.add(o);
			return this;
		}
		public BookEntry makeMultiblockPage(String name, @Nullable String multiblock_id, @Nullable JsonObject multiblock, @Nullable Boolean enable_visualize, boolean text){
			JsonObject o = new JsonObject();
			o.addProperty("type", "multiblock");
			o.addProperty("name", name);
			if(multiblock_id!=null) o.addProperty("multiblock_id", multiblock_id);
			if(multiblock!=null) o.add("multiblock", multiblock);
			if(enable_visualize!=null) o.addProperty("enable_visualize", enable_visualize);
			if(text) o.addProperty("text", getUnlocalizedText());
			this.pages.add(o);
			return this;
		}
		public BookEntry makeEntityPage(String entity, @Nullable Float scale, @Nullable Float offset, @Nullable Boolean rotate, @Nullable Float default_rotation, @Nullable String name, boolean text){
			JsonObject o = new JsonObject();
			o.addProperty("type", "entity");
			o.addProperty("entity", entity);
			if(scale!=null) o.addProperty("scale", scale);
			if(offset!=null) o.addProperty("offset", offset);
			if(rotate!=null) o.addProperty("rotate", rotate);
			if(default_rotation!=null) o.addProperty("default_rotation", default_rotation);
			if(name!=null) o.addProperty("name", name);
			if(text) o.addProperty("text", getUnlocalizedText());
			this.pages.add(o);
			return this;
		}
		public BookEntry makeSpotlightPage(String item, @Nullable String title, @Nullable Boolean link_recipe, boolean text){
			JsonObject o = new JsonObject();
			o.addProperty("type", "spotlight");
			o.addProperty("item", item);
			if(title!=null) o.addProperty("title", title);
			if(link_recipe!=null) o.addProperty("link_recipe", link_recipe);
			if(text) o.addProperty("text", getUnlocalizedText());
			this.pages.add(o);
			return this;
		}
		public BookEntry makeLinkPage(@Nullable String title, String url, String link_text){
			JsonObject o = new JsonObject();
			o.addProperty("type", "link");
			o.addProperty("url", url);
			o.addProperty("link_text", link_text);
			o.addProperty("text", getUnlocalizedText());
			if(title!=null) o.addProperty("title", title);
			this.pages.add(o);
			return this;
		}
		public BookEntry makeRelationsPage(@Nullable String entries, @Nullable String title, boolean text){
			JsonObject o = new JsonObject();
			o.addProperty("type", "relations");
			if(entries!=null) o.addProperty("entries", entries);
			if(title!=null) o.addProperty("title", title);
			if(text) o.addProperty("text", getUnlocalizedText());
			this.pages.add(o);
			return this;
		}
		public BookEntry makeQuestPage(@Nullable String trigger, @Nullable String title, boolean text){
			JsonObject o = new JsonObject();
			o.addProperty("type", "quest");
			if(trigger!=null) o.addProperty("trigger", trigger);
			if(title!=null) o.addProperty("title", title);
			if(text) o.addProperty("text", getUnlocalizedText());
			this.pages.add(o);
			return this;
		}
		public BookEntry makeEmptyPage(@Nullable Boolean draw_filter){
			JsonObject o = new JsonObject();
			o.addProperty("type", "empty");
			if(draw_filter!=null) o.addProperty("draw_filter", draw_filter);
			this.pages.add(o);
			return this;
		}
		private String getUnlocalizedText(){
			return "page."+MODID+"."+this.name+"."+this.pages.size();
		}
	}
}
