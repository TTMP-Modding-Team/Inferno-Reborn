package ttmp.infernoreborn.datagen;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ttmp.infernoreborn.ability.Ability;
import ttmp.infernoreborn.ability.generator.AbilityGenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Consumer;

import static ttmp.infernoreborn.InfernoReborn.MODID;
import static ttmp.infernoreborn.InfernoReborn.VERSION;

public class AbilDexDataProvider implements IDataProvider{
	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.disableHtmlEscaping()
			.create();

	private final DataGenerator generator;

	public AbilDexDataProvider(DataGenerator generator){
		this.generator = generator;
	}

	protected void generate(Consumer<AbilityGenerator> consumer){
		//consumer.accept(new AbilityGenerator(new ResourceLocation(MODID, "empty"), 50, null, null, false));
		//consumer.accept(new AbilityGenerator(new ResourceLocation(MODID, "test"), 50, null, new Give(new ConstantAbility(Abilities.HEART.get())), true));
		//consumer.accept();
	}
	public void makeBook(DirectoryCache directoryCache, String name, String landingtext, String version){
		JsonObject json = new JsonObject();
		json.addProperty("name", name);
		json.addProperty("landing_text", landingtext);
		json.addProperty("version", version);
		json.addProperty("dont_generate_book", true);
		json.addProperty("custom_book_item", MODID + ":abildex");
		Path path = createPath(this.generator.getOutputFolder(), "/book.json");
		try {
			IDataProvider.save(GSON, directoryCache, json, path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void makeCategory(DirectoryCache directoryCache, Path output, String name, String icon){
		JsonObject json = new JsonObject();
		json.addProperty("name", getUnlocalizedName("category", name));
		json.addProperty("description", getUnlocalizedName("category", name + ".desc"));
		json.addProperty("icon", icon);
		Path path = createPath(output, "/en_us/categories/" + name + ".json");
		try {
			IDataProvider.save(GSON, directoryCache, json, path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void makeEntries(DirectoryCache directoryCache, Path output, String name, String category, String icon) {
		Path path = createPath(this.generator.getOutputFolder(), "/en_us/entries/" + category + "/" + name + ".json");
		JsonObject o = new JsonObject();
		o.addProperty("name", getUnlocalizedName("entry", name));
		o.addProperty("icon", icon);
		o.addProperty("category", MODID + ":" + category);
		JsonArray pages = new JsonArray();
		JsonObject page = new JsonObject();
		page.addProperty("type", "text");
		page.addProperty("text", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce et tortor porttitor odio ultrices consequat. Etiam lacinia tempus nulla eu convallis. Nullam ultricies euismod felis, at vestibulum nulla tincidunt ac. Sed eu lacus ante. Nunc diam lorem, luctus vel pellentesque vitae, dapibus eget massa. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. In interdum laoreet lacinia. Mauris eget nulla massa. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nunc ultricies vulputate nibh, sed commodo odio gravida id.\n" +
				"\n" +
				"Nulla ligula elit, egestas non lacus eu, imperdiet interdum magna. Nunc ullamcorper lacus a turpis auctor sodales. Donec lectus nisl, tempus nec tellus posuere, accumsan tempor elit. Nunc sed dictum tellus, ut vehicula est. Aliquam non dolor eget augue hendrerit tristique. Nunc sed lacinia justo, efficitur rhoncus nunc. Proin bibendum, tortor id aliquam fringilla, mi magna consectetur massa, nec malesuada dui dolor sed nisi. Suspendisse finibus nibh lacinia pulvinar ornare. Donec vitae rutrum massa.\n" +
				"\n" +
				"Sed tincidunt vestibulum mauris a elementum. Praesent efficitur, nunc ut eleifend rhoncus, quam dui volutpat nisi, vel viverra sem purus at leo. In maximus neque quis lectus mattis egestas. Ut at interdum lorem. Sed eleifend commodo vestibulum. Aenean lectus nisi, efficitur non neque quis, euismod cursus mi. Nullam laoreet, nisl non pretium bibendum, arcu mi consequat libero, ac consequat elit velit id massa. Etiam blandit nunc turpis, ut condimentum eros ultricies eu. Maecenas mauris lorem, eleifend et ultrices in, mattis a arcu. Nulla maximus dolor id dolor tempor, sed rhoncus lectus euismod. Nulla id felis ac tortor rhoncus bibendum ut non risus. Donec varius risus odio, vitae viverra erat convallis eu. Sed non feugiat ante. Quisque ac condimentum libero, eget porttitor dui. Donec a leo scelerisque, facilisis lorem et, consequat dui.\n" +
				"\n" +
				"Nullam malesuada a dolor vel volutpat. Vivamus nisi velit, tincidunt in quam et, varius ultrices lacus. Praesent sagittis sem odio, vitae varius nisi tempor ut. Duis luctus porta euismod. Interdum et malesuada fames ac ante ipsum primis in faucibus. Integer ultrices mi ac est sodales, ut accumsan magna rhoncus. Donec facilisis nisi quis tincidunt suscipit. Aliquam ut diam nisi. Curabitur posuere sagittis nisi, at laoreet nisl. Nulla est elit, varius eu enim vel, aliquam volutpat tellus. Morbi eleifend lectus justo, eu varius eros tincidunt quis. Integer quis libero ac justo molestie posuere.\n" +
				"\n" +
				"Sed sagittis id dolor at tempor. Donec blandit diam sit amet facilisis porttitor. Suspendisse lectus justo, efficitur vel nisi non, luctus vehicula nisi. Nam mauris justo, porttitor ut dolor nec, euismod mattis diam. Proin orci sapien, ullamcorper id arcu sed, posuere mollis massa. Ut ultrices bibendum tincidunt. Nunc luctus pellentesque mauris, non pretium ligula convallis a. Nulla vitae felis lobortis, eleifend dolor a, semper turpis.");
		pages.add(page);
		o.add("pages", pages);
		try {
			IDataProvider.save(GSON, directoryCache, o, path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void makeTemplates() {
		// TODO
	}

	@Override public void run(DirectoryCache directoryCache){
		Path output = this.generator.getOutputFolder();
		Set<ResourceLocation> set = Sets.newHashSet();
		//IDataProvider.save(GSON, directoryCache, makeBook("TEST", "FREKINGTEST", "ITSUPPORTSstring?"), createPath(output, "book.json"));
		makeBook(directoryCache, getUnlocalizedName("item", "abildex"), getUnlocalizedName("text", "landingtext"), VERSION);
		makeCategory(directoryCache, output, "quickstart", "minecraft:cobblestone");
		makeCategory(directoryCache, output, "abilities", "infernoreborn:inferno_spark");
		makeCategory(directoryCache, output, "items", "minecraft:stick");
		for (Ability ability : GameRegistry.findRegistry(Ability.class).getValues()){
			makeEntries(directoryCache, output, ability.toString().substring(22), "abilities", "minecraft:stick");
		}
		/*Consumer<AbilityGenerator> consumer = (abilityGenerator) -> {
			if(!set.add(abilityGenerator.getId())) throw new IllegalStateException("Duplicate ability generator "+abilityGenerator.getId());
			Path path = createPath(output, abilityGenerator.getId());

			try{
				InfernoReborn.LOGGER.debug("Generating AbilityGenerator {} at {}", abilityGenerator.getId(), path);
				IDataProvider.save(GSON, directoryCache, abilityGenerator.serialize(), path);
			}catch(IOException ex){
				InfernoReborn.LOGGER.error("Couldn't save advancement {}", path, ex);
			}
		};

		//generate(consumer);
		generate(c);*/
	}

	@Override public String getName(){
		return "AbilDex";
	}

	private static Path createPath(Path output, String filename){
		//return output.resolve("data/patchouli_books/"+resourceLocation.getNamespace()+"/"+AbilityGenerators.Listener.FOLDER+"/"+resourceLocation.getPath()+".json");
		return output.resolve("data/" + MODID + "/patchouli_books/abildex" + filename);
	}

	private String getUnlocalizedName(String type, String s){
		return type + "." + MODID + "." + s;
	}
}
