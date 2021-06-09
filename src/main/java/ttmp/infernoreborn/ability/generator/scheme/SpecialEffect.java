package ttmp.infernoreborn.ability.generator.scheme;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import ttmp.infernoreborn.InfernoReborn;

import java.util.Arrays;
import java.util.Objects;

public final class SpecialEffect{ // TODO 재미없네요
	public static SpecialEffect parse(JsonObject object){
		JsonArray jsonColors = JSONUtils.getAsJsonArray(object, "colors");
		if(jsonColors.size()>3) InfernoReborn.LOGGER.warn("Too many colors");
		int[] colors = new int[Math.min(3, jsonColors.size())];
		for(int i = 0; i<colors.length; i++){
			colors[i] = jsonColors.get(i).getAsInt();
		}
		return new SpecialEffect(colors);
	}
	public static SpecialEffect read(PacketBuffer buf){
		int[] colors = new int[buf.readVarInt()];
		for(int i = 0; i<colors.length; i++)
			colors[i] = buf.readInt();
		return new SpecialEffect(colors);
	}
	public static SpecialEffect create(int... colors){
		if(colors.length<=0||colors.length>3) throw new IllegalArgumentException("colors");
		return new SpecialEffect(colors);
	}

	private final int[] colors;

	private SpecialEffect(int[] colors){
		this.colors = Objects.requireNonNull(colors);
	}

	public int[] getColors(){
		return colors;
	}

	public JsonObject serialize(){
		JsonObject o = new JsonObject();
		JsonArray colors = new JsonArray();
		for(int color : this.colors) colors.add(color);
		o.add("colors", colors);
		return o;
	}

	public void write(PacketBuffer buf){
		buf.writeVarInt(this.colors.length);
		for(int color : this.colors) buf.writeInt(color);
	}

	@Override public String toString(){
		return "SpecialEffect{"+
				"colors="+Arrays.toString(colors)+
				'}';
	}
}
