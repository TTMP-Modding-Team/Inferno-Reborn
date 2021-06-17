package ttmp.infernoreborn.ability.generator.node.variable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.JSONUtils;
import ttmp.infernoreborn.ability.holder.AbilityHolder;

public class RangedInteger implements SomeInteger{
	public static RangedInteger parse(JsonObject object){
		return new RangedInteger(JSONUtils.getAsInt(object, "min"), JSONUtils.getAsInt(object, "max"));
	}

	private final int min;
	private final int max;

	public RangedInteger(int min, int max){
		this.min = min;
		this.max = max;
		if(min>max) throw new IllegalArgumentException("min > max");
	}

	@Override public int getInt(LivingEntity entity, AbilityHolder holder){
		if(min==max) return min;
		return entity.getRandom().nextInt(max-min)+min;
	}

	@Override public JsonElement serialize(){
		JsonObject o = new JsonObject();
		o.addProperty("min", min);
		o.addProperty("max", max);
		return o;
	}
}
