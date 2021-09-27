package ttmp.infernoreborn.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.util.EssenceType;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import static ttmp.infernoreborn.InfernoReborn.MODID;

public class McmetaGen implements IDataProvider{
	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.disableHtmlEscaping()
			.create();
	private static final ExistingFileHelper.ResourceType TEXTURE = new ExistingFileHelper.ResourceType(ResourcePackType.CLIENT_RESOURCES, ".png", "textures");

	private final DataGenerator generator;
	private final ExistingFileHelper existingFileHelper;

	private final Map<ResourceLocation, JsonObject> mcmeta = new HashMap<>();

	public McmetaGen(DataGenerator generator, ExistingFileHelper existingFileHelper){
		this.generator = Objects.requireNonNull(generator);
		this.existingFileHelper = Objects.requireNonNull(existingFileHelper);
	}

	protected void register(){
		for(EssenceType type : EssenceType.values()){
			add(new ResourceLocation(MODID, "item/essence/"+type.id), mcmeta(20, true));
			add(new ResourceLocation(MODID, "item/greater_essence/"+type.id), mcmeta(20, true));
			add(new ResourceLocation(MODID, "item/exquisite_essence/"+type.id), mcmeta(20, true));
		}
		add(new ResourceLocation(MODID, "block/foundry/firebox_on"), mcmeta(10, true));
		add(new ResourceLocation(MODID, "block/essence_holder/side"), mcmeta(10, true));
		add(new ResourceLocation(MODID, "block/essence_holder/top"), mcmeta(10, true));
		add(new ResourceLocation(MODID, "item/judgement"), mcmeta(3, false));
	}

	@Override public void run(DirectoryCache directoryCache){
		register();

		for(Entry<ResourceLocation, JsonObject> e : mcmeta.entrySet()){
			ResourceLocation key = e.getKey();
			Path path = createPath(generator.getOutputFolder(), key);
			try{
				IDataProvider.save(GSON, directoryCache, e.getValue(), path);
			}catch(IOException ex){
				InfernoReborn.LOGGER.error("Couldn't save mcmeta file {}", path, ex);
			}
		}
	}

	protected void add(ResourceLocation texture, JsonObject o){
		if(!existingFileHelper.exists(texture, TEXTURE))
			throw new RuntimeException("No texture resource "+texture);
		if(mcmeta.put(texture, Objects.requireNonNull(o))!=null){
			throw new IllegalArgumentException("Duplicated mcmeta "+texture);
		}
	}

	@Override public String getName(){
		return ".mcmeta";
	}

	public static JsonObject mcmeta(int frametime, boolean interpolate){
		JsonObject o = new JsonObject();
		JsonObject animation = new JsonObject();
		animation.addProperty("frametime", frametime);
		if(interpolate) animation.addProperty("interpolate", true);
		o.add("animation", animation);
		return o;
	}

	private static Path createPath(Path output, ResourceLocation res){
		return output.resolve("assets/"+res.getNamespace()+"/textures/"+res.getPath()+".png.mcmeta");
	}
}
