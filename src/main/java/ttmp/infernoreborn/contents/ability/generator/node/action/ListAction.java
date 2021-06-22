package ttmp.infernoreborn.contents.ability.generator.node.action;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ttmp.infernoreborn.contents.ability.generator.node.condition.Condition;
import ttmp.infernoreborn.contents.ability.generator.node.variable.SomeInteger;
import ttmp.infernoreborn.contents.ability.generator.parser.Parsers;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public abstract class ListAction extends Action{
	protected final List<Action> actions;

	public ListAction(Action... actions){
		this(ImmutableList.copyOf(actions));
	}
	public ListAction(@Nullable Condition condition, @Nullable SomeInteger repeat, Action... actions){
		this(ImmutableList.copyOf(actions), condition, repeat);
	}
	public ListAction(List<Action> actions){
		this(actions, null, null);
	}
	public ListAction(List<Action> actions, @Nullable Condition condition, @Nullable SomeInteger repeat){
		super(condition, repeat);
		this.actions = actions;
	}
	public ListAction(JsonObject object){
		super(object);
		JsonElement element = object.get(getJsonKey());
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

	protected abstract String getJsonKey();

	@Override protected void doSerialize(JsonObject object){
		switch(actions.size()){
			case 0:
				object.add(getJsonKey(), new JsonArray());
				break;
			case 1:
				object.add(getJsonKey(), actions.get(0).serialize());
				break;
			default:
				JsonArray array = new JsonArray();
				for(Action action : actions) array.add(action.serialize());
				object.add(getJsonKey(), array);
		}
	}
}
