package ttmp.infernoreborn.ability.generator.node.action;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.ability.generator.node.variable.SomeInteger;
import ttmp.infernoreborn.ability.generator.node.condition.Condition;
import ttmp.infernoreborn.ability.generator.parser.Parsers;
import ttmp.infernoreborn.capability.AbilityHolder;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class Do extends Action{
	private final List<Action> actions;

	public Do(List<Action> actions){
		this(actions, null, null);
	}
	public Do(List<Action> actions, @Nullable Condition condition, @Nullable SomeInteger repeat){
		super(condition, repeat);
		this.actions = actions;
	}
	public Do(JsonObject object){
		super(object);
		JsonElement element = object.get("do");
		if(element.isJsonObject()){
			this.actions = Collections.singletonList(Parsers.parseAction(element.getAsJsonObject()));
		}else{
			ImmutableList.Builder<Action> b = ImmutableList.builder();
			for(JsonElement e : element.getAsJsonArray()){
				b.add(Parsers.parseAction(e.getAsJsonObject()));
			}
			this.actions = b.build();
		}
	}

	@Override protected void doAct(LivingEntity entity, AbilityHolder h){
		for(Action a : actions) a.act(entity, h);
	}

	@Override protected void doSerialize(JsonObject object){
		switch(actions.size()){
			case 0:
				object.add("do", new JsonArray());
				break;
			case 1:
				object.add("do", actions.get(0).serialize());
				break;
			default:
				JsonArray array = new JsonArray();
				for(Action action : actions) array.add(action.serialize());
				object.add("do", array);
		}
	}
}
