package ttmp.infernoreborn.contents.ability.generator.node.variable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.contents.ability.generator.parser.Parsers;
import ttmp.infernoreborn.contents.ability.holder.AbilityHolder;
import ttmp.infernoreborn.util.WeightedPool;

import javax.annotation.Nullable;
import java.util.Objects;

public class NewRandomAbility implements SomeAbility{
	private final WeightedPool<Ability> pool;

	public NewRandomAbility(WeightedPool<Ability> pool){
		this.pool = Objects.requireNonNull(pool);
	}
	public NewRandomAbility(JsonObject object){
		this.pool = Parsers.deserializeAbilityPool(object);
	}

	@Nullable @Override public Ability getAbility(LivingEntity entity, AbilityHolder holder){
		return pool.nextItem(entity.getRandom(), holder.getAbilities());
	}
	@Override public JsonElement serialize(){
		return Parsers.serializeAbilityPool(pool);
	}
}
