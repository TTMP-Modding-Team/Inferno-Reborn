package ttmp.infernoreborn.ability.generator.node.variable;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.capability.AbilityHolder;

public class ConstantInteger implements SomeInteger{
	private final int i;

	public ConstantInteger(int i){
		this.i = i;
	}

	@Override public int getInt(LivingEntity entity, AbilityHolder holder){
		return i;
	}

	@Override public JsonElement serialize(){
		return new JsonPrimitive(i);
	}
}
