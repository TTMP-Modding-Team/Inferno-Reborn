package ttmp.infernoreborn.ability.generator.node.action;

import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.ability.Ability;
import ttmp.infernoreborn.ability.generator.node.variable.SomeAbility;
import ttmp.infernoreborn.ability.generator.node.variable.SomeInteger;
import ttmp.infernoreborn.ability.generator.node.condition.Condition;
import ttmp.infernoreborn.ability.generator.parser.Parsers;
import ttmp.infernoreborn.ability.holder.AbilityHolder;

import javax.annotation.Nullable;

public class Give extends Action{
	private final SomeAbility someAbility;

	public Give(SomeAbility someAbility){
		this(someAbility, null, null);
	}
	public Give(SomeAbility someAbility, @Nullable Condition condition, @Nullable SomeInteger repeat){
		super(condition, repeat);
		this.someAbility = someAbility;
	}
	public Give(JsonObject object){
		super(object);
		this.someAbility = Parsers.parseAbility(object.get("give"));
	}

	@Override protected void doAct(LivingEntity entity, AbilityHolder holder){
		Ability ability = someAbility.getAbility(entity, holder);
		if(ability!=null) holder.add(ability);
	}

	@Override protected void doSerialize(JsonObject object){
		object.add("give", someAbility.serialize());
	}
}
