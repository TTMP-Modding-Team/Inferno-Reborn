package ttmp.infernoreborn.ability.generator.pool;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.ability.Ability;
import ttmp.infernoreborn.ability.generator.node.Node;
import ttmp.infernoreborn.contents.Abilities;

import java.util.Map;
import java.util.Objects;

public class WeightedAbilityPool extends WeightedPool<Ability> implements Node{
	public static WeightedAbilityPool parse(JsonObject object){
		Object2IntMap<Ability> m = new Object2IntOpenHashMap<>();
		for(Map.Entry<String, JsonElement> e : object.entrySet()){
			Ability a = Abilities.getRegistry().getValue(new ResourceLocation(e.getKey()));
			if(a!=null) m.put(a, e.getValue().getAsInt());
		}
		return new WeightedAbilityPool(m);
	}

	public WeightedAbilityPool(Object2IntMap<Ability> items){
		super(items);
	}

	@Override public JsonElement serialize(){
		JsonObject object = new JsonObject();
		for(Object2IntMap.Entry<Ability> e : getItems().object2IntEntrySet())
			object.addProperty(Node.toSerializedString(Objects.requireNonNull(e.getKey().getRegistryName())), e.getIntValue());
		return object;
	}
}
