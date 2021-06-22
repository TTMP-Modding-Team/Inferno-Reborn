package ttmp.infernoreborn.contents.ability.generator.node.action;

import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import ttmp.infernoreborn.contents.ability.generator.node.condition.Condition;
import ttmp.infernoreborn.contents.ability.generator.node.variable.SomeInteger;
import ttmp.infernoreborn.contents.ability.holder.AbilityHolder;

import javax.annotation.Nullable;
import java.util.List;

public class Choose extends ListAction{
	public Choose(Action... actions){
		super(actions);
	}
	public Choose(@Nullable Condition condition, @Nullable SomeInteger repeat, Action... actions){
		super(condition, repeat, actions);
	}
	public Choose(List<Action> actions){
		super(actions);
	}
	public Choose(List<Action> actions, @Nullable Condition condition, @Nullable SomeInteger repeat){
		super(actions, condition, repeat);
	}
	public Choose(JsonObject object){
		super(object);
	}

	@Override protected void doAct(LivingEntity entity, AbilityHolder holder){
		switch(this.actions.size()){
			case 0:
				return;
			case 1:
				this.actions.get(0).act(entity, holder);
				return;
			default:{
				Action[] actions = this.actions.stream().filter(a -> a.canAct(entity, holder)).toArray(Action[]::new);
				actions[entity.getRandom().nextInt(actions.length)].act(entity, holder);
			}
		}
	}

	@Override protected String getJsonKey(){
		return "choose";
	}
}
