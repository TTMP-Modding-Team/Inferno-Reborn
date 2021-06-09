package ttmp.infernoreborn.ability.generator.scheme;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;

public final class ItemDisplay{
	public static ItemDisplay parse(JsonObject object){
		int primaryColor = JSONUtils.getAsInt(object, "primaryColor", 0xFFFFFF);
		return new ItemDisplay(
				primaryColor,
				JSONUtils.getAsInt(object, "secondaryColor", 0xFFFFFF),
				JSONUtils.getAsInt(object, "highlightColor", primaryColor)
		);
	}
	public static ItemDisplay read(PacketBuffer buf){
		return new ItemDisplay(buf.readInt(), buf.readInt(), buf.readInt());
	}

	private final int primaryColor;
	private final int secondaryColor;
	private final int highlightColor;

	public ItemDisplay(int primaryColor, int secondaryColor, int highlightColor){
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
		this.highlightColor = highlightColor;
	}

	public int getPrimaryColor(){
		return primaryColor;
	}
	public int getSecondaryColor(){
		return secondaryColor;
	}
	public int getHighlightColor(){
		return highlightColor;
	}

	public JsonObject serialize(){
		JsonObject o = new JsonObject();
		if(primaryColor!=0xFFFFFF) o.addProperty("primaryColor", primaryColor);
		if(secondaryColor!=0xFFFFFF) o.addProperty("secondaryColor", secondaryColor);
		if(highlightColor!=primaryColor) o.addProperty("highlightColor", highlightColor);
		return o;
	}

	public void write(PacketBuffer buf){
		buf.writeInt(primaryColor);
		buf.writeInt(secondaryColor);
		buf.writeInt(highlightColor);
	}

	@Override public String toString(){
		return "ItemDisplay{"+
				"primaryColor="+primaryColor+
				", secondaryColor="+secondaryColor+
				", highlightColor="+highlightColor+
				'}';
	}
}
