package ttmp.infernoreborn.ability.generator.node.condition;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.capability.AbilityHolder;

public class ConstantCondition implements Condition{
	private final boolean value;

	public ConstantCondition(boolean value){
		this.value = value;
	}

	@Override public boolean matches(LivingEntity entity, AbilityHolder holder){
		return value;
	}

	@Override public JsonElement serialize(){
		return new JsonPrimitive(value);
	}

	@Override public String toString(){
		return Boolean.toString(value);
	}
}
