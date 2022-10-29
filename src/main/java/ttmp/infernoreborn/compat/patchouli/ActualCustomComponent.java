package ttmp.infernoreborn.compat.patchouli;

import com.google.gson.JsonObject;
import vazkii.patchouli.api.ICustomComponent;

/**
 * A custom component that functions both as datagen instance and component itself.
 */
public abstract class ActualCustomComponent extends BookComponent implements ICustomComponent{
	@Override public void build(int componentX, int componentY, int pageNum){
		this.x = componentX;
		this.y = componentY;
	}

	@Override public String type(){
		return "custom";
	}
	@Override protected void doSerialize(JsonObject object){
		object.addProperty("class", this.getClass().getName());
	}
}
