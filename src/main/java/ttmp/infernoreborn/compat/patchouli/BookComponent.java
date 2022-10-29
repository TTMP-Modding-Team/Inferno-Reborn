package ttmp.infernoreborn.compat.patchouli;

import com.google.gson.JsonObject;

public abstract class BookComponent{
	protected transient int x;
	protected transient int y;

	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}

	public abstract String type();

	public BookComponent pos(int x, int y){
		this.x = x;
		this.y = y;
		return this;
	}

	public JsonObject serialize(){
		JsonObject o = new JsonObject();
		o.addProperty("type", type());
		if(x!=0) o.addProperty("x", x);
		if(y!=0) o.addProperty("y", y);
		doSerialize(o);
		return o;
	}

	protected abstract void doSerialize(JsonObject object);
}
