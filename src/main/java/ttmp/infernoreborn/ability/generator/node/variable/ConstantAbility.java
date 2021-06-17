package ttmp.infernoreborn.ability.generator.node.variable;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import ttmp.infernoreborn.ability.Ability;
import ttmp.infernoreborn.ability.holder.AbilityHolder;
import ttmp.infernoreborn.contents.Abilities;

import javax.annotation.Nullable;
import java.util.Objects;

public class ConstantAbility implements SomeAbility{
	@Nullable private final Ability ability;

	public ConstantAbility(@Nullable Ability ability){
		this.ability = ability;
	}
	public ConstantAbility(String abilityName){
		this(Abilities.getRegistry().getValue(new ResourceLocation(abilityName)));
	}

	@Nullable @Override public Ability getAbility(LivingEntity entity, AbilityHolder holder){
		return ability;
	}

	@Override public JsonElement serialize(){
		return ability!=null ? new JsonPrimitive(Objects.requireNonNull(ability.getRegistryName()).toString()) : JsonNull.INSTANCE;
	}

	@Override public String toString(){
		return Objects.toString(ability);
	}
}
