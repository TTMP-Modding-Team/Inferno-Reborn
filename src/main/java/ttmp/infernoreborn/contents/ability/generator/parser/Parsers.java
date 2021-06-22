package ttmp.infernoreborn.contents.ability.generator.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.contents.Abilities;
import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.contents.ability.generator.node.Node;
import ttmp.infernoreborn.contents.ability.generator.node.action.Action;
import ttmp.infernoreborn.contents.ability.generator.node.action.Choose;
import ttmp.infernoreborn.contents.ability.generator.node.action.Do;
import ttmp.infernoreborn.contents.ability.generator.node.action.Give;
import ttmp.infernoreborn.contents.ability.generator.node.condition.Condition;
import ttmp.infernoreborn.contents.ability.generator.node.condition.ConstantCondition;
import ttmp.infernoreborn.contents.ability.generator.node.condition.HasCondition;
import ttmp.infernoreborn.contents.ability.generator.node.condition.RollCondition;
import ttmp.infernoreborn.contents.ability.generator.node.variable.ConstantAbility;
import ttmp.infernoreborn.contents.ability.generator.node.variable.ConstantInteger;
import ttmp.infernoreborn.contents.ability.generator.node.variable.NewRandomAbility;
import ttmp.infernoreborn.contents.ability.generator.node.variable.RangedInteger;
import ttmp.infernoreborn.contents.ability.generator.node.variable.SomeAbility;
import ttmp.infernoreborn.contents.ability.generator.node.variable.SomeInteger;
import ttmp.infernoreborn.util.WeightedPool;

import java.util.Map;
import java.util.Objects;

public final class Parsers{
	private Parsers(){}

	public static final ObjectParser<Action> ACTION_PARSER = new ObjectParser<Action>()
			.add("do", Do::new)
			.add("choose", Choose::new)
			.add("give", Give::new);
	public static final ObjectParser<Condition> CONDITION_PARSER = new ObjectParser<Condition>()
			.add("has", HasCondition::parse)
			.add("roll", RollCondition::new);

	public static Action parseAction(JsonObject object){
		return ACTION_PARSER.parseOrThrow(object);
	}
	public static Condition parseCondition(JsonElement element){
		if(element.isJsonPrimitive()) return new ConstantCondition(element.getAsBoolean());
		else return CONDITION_PARSER.parseOrThrow(element.getAsJsonObject());
	}
	public static SomeAbility parseAbility(JsonElement element){
		if(element.isJsonPrimitive()) return new ConstantAbility(element.getAsString());
		else return new NewRandomAbility(element.getAsJsonObject());
	}
	public static SomeInteger parseInteger(JsonElement element){
		if(element.isJsonPrimitive()) return new ConstantInteger(element.getAsInt());
		else return RangedInteger.parse(element.getAsJsonObject());
	}

	public static JsonObject serializeAbilityPool(WeightedPool<Ability> pool){
		JsonObject object = new JsonObject();
		for(Object2IntMap.Entry<Ability> e : pool.getItems().object2IntEntrySet())
			object.addProperty(Node.toSerializedString(Objects.requireNonNull(e.getKey().getRegistryName())), e.getIntValue());
		return object;
	}
	public static WeightedPool<Ability> deserializeAbilityPool(JsonObject object){
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
		return new WeightedPool<>(m, nullWeight);
	}
}
