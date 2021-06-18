package ttmp.infernoreborn.contents.ability.generator.pool;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.contents.ability.generator.node.Node;
import ttmp.infernoreborn.contents.Abilities;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

public class WeightedAbilityPool extends WeightedPool<Ability> implements Node{
	public static WeightedAbilityPool parse(JsonObject object){
		Object2IntMap<Ability> m = new Object2IntOpenHashMap<>();
		int nullWeight = 0;
		for(Map.Entry<String, JsonElement> e : object.entrySet()){
			String key = e.getKey();
			if(key.isEmpty()) nullWeight = e.getValue().getAsInt();
			else{
				Ability a = Abilities.getRegistry().getValue(new ResourceLocation(key));
				if(a!=null) m.put(a, e.getValue().getAsInt());
			}
		}
		return new WeightedAbilityPool(m, nullWeight);
	}

	public WeightedAbilityPool(Object2IntMap<Ability> items){
		super(items);
	}
	public WeightedAbilityPool(Object2IntMap<Ability> items, int nullWeight){
		super(items, nullWeight);
	}

	@Override public JsonElement serialize(){
		JsonObject object = new JsonObject();
		for(Object2IntMap.Entry<Ability> e : getItems().object2IntEntrySet())
			object.addProperty(Node.toSerializedString(Objects.requireNonNull(e.getKey().getRegistryName())), e.getIntValue());
		return object;
	}

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private final Object2IntMap<Ability> pool = new Object2IntArrayMap<>();
		private int nullWeight = 0;

		public Builder add(@Nullable Ability ability, int weight){
			if(weight<0) throw new IllegalArgumentException("weight");
			if(ability==null) nullWeight = weight;
			else pool.put(ability, weight);
			return this;
		}

		public WeightedAbilityPool build(){
			return new WeightedAbilityPool(pool, nullWeight);
		}
	}
}
