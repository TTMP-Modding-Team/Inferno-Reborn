package ttmp.infernoreborn.contents.ability.generator.node.action;

import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.contents.ability.generator.node.condition.Condition;
import ttmp.infernoreborn.contents.ability.generator.node.variable.SomeInteger;
import ttmp.infernoreborn.contents.ability.holder.AbilityHolder;

import javax.annotation.Nullable;
import java.util.List;

public class Do extends ListAction{
	public Do(Action... actions){
		super(actions);
	}
	public Do(@Nullable Condition condition, @Nullable SomeInteger repeat, Action... actions){
		super(condition, repeat, actions);
	}
	public Do(List<Action> actions){
		super(actions);
	}
	public Do(List<Action> actions, @Nullable Condition condition, @Nullable SomeInteger repeat){
		super(actions, condition, repeat);
	}
	public Do(JsonObject object){
		super(object);
	}

	@Override protected void doAct(LivingEntity entity, AbilityHolder holder){
		for(Action action : this.actions) action.act(entity, holder);
	}

	@Override protected String getJsonKey(){
		return "do";
	}
}
