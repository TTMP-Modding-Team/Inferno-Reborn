package ttmp.infernoreborn.contents.ability.generator.scheme;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;

public final class ItemDisplay{
	public static ItemDisplay parse(JsonObject object){
		return new ItemDisplay(JSONUtils.getAsInt(object, "color", 0xFFFFFF));
	}
	public static ItemDisplay read(PacketBuffer buf){
		return new ItemDisplay(buf.readInt());
	}

	private final int color;

	public ItemDisplay(int color){
		this.color = color;
	}

	public int getColor(){
		return color;
	}

	public JsonObject serialize(){
		JsonObject o = new JsonObject();
		if(color!=0xFFFFFF) o.addProperty("color", color);
		return o;
	}

	public void write(PacketBuffer buf){
		buf.writeInt(color);
	}

	@Override public String toString(){
		return "ItemDisplay{"+
				"color="+color+
				'}';
	}
}
