package ttmp.infernoreborn.ability.generator.node.action;

import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.ability.generator.node.Node;
import ttmp.infernoreborn.ability.generator.node.variable.SomeInteger;
import ttmp.infernoreborn.ability.generator.node.condition.Condition;
import ttmp.infernoreborn.ability.generator.parser.Parsers;
import ttmp.infernoreborn.capability.AbilityHolder;

import javax.annotation.Nullable;

public abstract class Action implements Node{
	@Nullable private final Condition condition;
	@Nullable private final SomeInteger repeat;

	public Action(@Nullable Condition condition, @Nullable SomeInteger repeat){
		this.condition = condition;
		this.repeat = repeat;
	}
	public Action(JsonObject object){
		this.condition = object.has("if") ? Parsers.parseCondition(object.get("if")) : null;
		this.repeat = object.has("repeat") ? Parsers.parseInteger(object.get("repeat")) : null;
	}

	@Nullable public Condition getCondition(){
		return condition;
	}
	@Nullable public SomeInteger getRepeat(){
		return repeat;
	}

	public void act(LivingEntity entity, AbilityHolder holder){
		if(condition!=null&&!condition.matches(entity, holder)) return;
		for(int i = 0, j = repeat!=null ? repeat.getInt(entity, holder) : 1; i<j; i++)
			doAct(entity, holder);
	}
	protected abstract void doAct(LivingEntity entity, AbilityHolder holder);

	@Override public JsonObject serialize(){
		JsonObject o = new JsonObject();
		if(condition!=null) o.add("if", condition.serialize());
		if(repeat!=null) o.add("repeat", repeat.serialize());
		doSerialize(o);
		return o;
	}

	protected abstract void doSerialize(JsonObject object);
}
