package ttmp.infernoreborn.ability.generator.node.condition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.ability.holder.AbilityHolder;

public class RollCondition implements Condition{
	private final double chance;

	public RollCondition(double chance){
		this.chance = chance;
	}
	public RollCondition(JsonObject object){
		this.chance = object.get("roll").getAsDouble();
	}

	@Override public boolean matches(LivingEntity entity, AbilityHolder holder){
		if(chance<=0) return false;
		if(chance>=1) return true;
		return entity.getRandom().nextDouble()<chance;
	}

	@Override public JsonElement serialize(){
		JsonObject o = new JsonObject();
		o.addProperty("roll", chance);
		return o;
	}
}
