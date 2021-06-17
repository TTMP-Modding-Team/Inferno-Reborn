package ttmp.infernoreborn.ability.generator.node.variable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.ability.Ability;
import ttmp.infernoreborn.ability.generator.pool.WeightedAbilityPool;
import ttmp.infernoreborn.ability.holder.AbilityHolder;

import javax.annotation.Nullable;
import java.util.Objects;

public class WeightedRandomAbility implements SomeAbility{
	private final WeightedAbilityPool pool;

	public WeightedRandomAbility(WeightedAbilityPool pool){
		this.pool = Objects.requireNonNull(pool);
	}
	public WeightedRandomAbility(JsonObject object){
		this.pool = WeightedAbilityPool.parse(object);
	}

	@Nullable @Override public Ability getAbility(LivingEntity entity, AbilityHolder holder){
		return pool.nextItem(entity.getRandom());
	}
	@Override public JsonElement serialize(){
		return pool.serialize();
	}
}
