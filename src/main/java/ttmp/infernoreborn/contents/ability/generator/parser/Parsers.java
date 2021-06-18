package ttmp.infernoreborn.contents.ability.generator.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ttmp.infernoreborn.contents.ability.generator.node.variable.ConstantAbility;
import ttmp.infernoreborn.contents.ability.generator.node.variable.ConstantInteger;
import ttmp.infernoreborn.contents.ability.generator.node.variable.RangedInteger;
import ttmp.infernoreborn.contents.ability.generator.node.variable.SomeAbility;
import ttmp.infernoreborn.contents.ability.generator.node.variable.SomeInteger;
import ttmp.infernoreborn.contents.ability.generator.node.variable.WeightedRandomAbility;
import ttmp.infernoreborn.contents.ability.generator.node.action.Action;
import ttmp.infernoreborn.contents.ability.generator.node.action.Give;
import ttmp.infernoreborn.contents.ability.generator.node.condition.Condition;
import ttmp.infernoreborn.contents.ability.generator.node.condition.ConstantCondition;
import ttmp.infernoreborn.contents.ability.generator.node.action.Do;
import ttmp.infernoreborn.contents.ability.generator.node.condition.HasCondition;
import ttmp.infernoreborn.contents.ability.generator.node.condition.RollCondition;

public final class Parsers{
	private Parsers(){}

	public static final ObjectParser<Action> ACTION_PARSER = new ObjectParser<Action>()
			.add("do", Do::new)
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
		else return new WeightedRandomAbility(element.getAsJsonObject());
	}
	public static SomeInteger parseInteger(JsonElement element){
		if(element.isJsonPrimitive()) return new ConstantInteger(element.getAsInt());
		else return RangedInteger.parse(element.getAsJsonObject());
	}
}
