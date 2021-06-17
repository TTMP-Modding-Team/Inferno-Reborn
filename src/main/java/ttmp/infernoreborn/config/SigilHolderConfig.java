package ttmp.infernoreborn.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.Map.Entry;

public final class SigilHolderConfig{
	private final Object2IntMap<ResourceLocation> maxPoints = new Object2IntOpenHashMap<>();

	public int getMaxPoints(Item item){
		ResourceLocation id = item.getRegistryName();
		if(id==null) return 0;
		return maxPoints.getOrDefault(id, 0);
	}
	public void setMaxPoints(Item item, int maxPoints){
		this.maxPoints.put(item.getRegistryName(), maxPoints);
	}
	public boolean has(Item item){
		return maxPoints.containsKey(item.getRegistryName());
	}
	public void remove(Item item){
		maxPoints.removeInt(item.getRegistryName());
	}

	public void read(JsonObject object){
		for(Entry<String, JsonElement> e : object.entrySet())
			maxPoints.put(new ResourceLocation(e.getKey()), e.getValue().getAsInt());
	}
	public JsonObject write(){
		JsonObject object = new JsonObject();
		for(Object2IntMap.Entry<ResourceLocation> e : maxPoints.object2IntEntrySet())
			object.addProperty(e.getKey().toString(), e.getIntValue());
		return object;
	}
}
