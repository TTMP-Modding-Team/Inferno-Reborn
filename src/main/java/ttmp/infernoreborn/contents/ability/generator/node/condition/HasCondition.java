package ttmp.infernoreborn.contents.ability.generator.node.condition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.contents.ability.generator.node.Node;
import ttmp.infernoreborn.contents.ability.holder.AbilityHolder;
import ttmp.infernoreborn.contents.Abilities;

import java.util.Objects;

public class HasCondition implements Condition{
	public static Condition parse(JsonObject object){
		ResourceLocation key = new ResourceLocation(JSONUtils.getAsString(object, "has"));
		Ability a = Abilities.getRegistry().getValue(key);
		return a!=null ? new HasCondition(a) : new ConstantCondition(false);
	}

	private final Ability ability;

	public HasCondition(Ability ability){
		this.ability = Objects.requireNonNull(ability);
	}

	@Override public boolean matches(LivingEntity entity, AbilityHolder holder){
		return holder.has(ability);
	}
	@Override public JsonElement serialize(){
		JsonObject o = new JsonObject();
		o.addProperty("has", Node.toSerializedString(Objects.requireNonNull(ability.getRegistryName())));
		return o;
	}
}
