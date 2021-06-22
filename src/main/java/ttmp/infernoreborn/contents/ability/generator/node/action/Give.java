package ttmp.infernoreborn.contents.ability.generator.node.action;

import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.contents.ability.Ability;
import ttmp.infernoreborn.contents.ability.generator.node.condition.Condition;
import ttmp.infernoreborn.contents.ability.generator.node.variable.ConstantAbility;
import ttmp.infernoreborn.contents.ability.generator.node.variable.SomeAbility;
import ttmp.infernoreborn.contents.ability.generator.node.variable.SomeInteger;
import ttmp.infernoreborn.contents.ability.generator.node.variable.NewRandomAbility;
import ttmp.infernoreborn.contents.ability.generator.parser.Parsers;
import ttmp.infernoreborn.contents.ability.holder.AbilityHolder;
import ttmp.infernoreborn.util.WeightedPool;

import javax.annotation.Nullable;

public class Give extends Action{
	private final SomeAbility someAbility;

	public Give(Ability ability){
		this(new ConstantAbility(ability));
	}
	public Give(WeightedPool<Ability> pool){
		this(new NewRandomAbility(pool));
	}
	public Give(Ability ability, @Nullable Condition condition, @Nullable SomeInteger repeat){
		this(new ConstantAbility(ability), condition, repeat);
	}
	public Give(WeightedPool<Ability> pool, @Nullable Condition condition, @Nullable SomeInteger repeat){
		this(new NewRandomAbility(pool), condition, repeat);
	}
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
