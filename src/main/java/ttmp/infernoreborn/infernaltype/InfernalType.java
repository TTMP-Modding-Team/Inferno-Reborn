package ttmp.infernoreborn.infernaltype;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Objects;

public final class InfernalType{
	public static InfernalType read(PacketBuffer buf){
		return new InfernalType(buf.readResourceLocation(),
				buf.readBoolean() ? SpecialEffect.read(buf) : null,
				buf.readBoolean() ? ItemDisplay.read(buf) : null);
	}

	private final ResourceLocation id;
	@Nullable private final SpecialEffect specialEffect;
	@Nullable private final ItemDisplay itemDisplay;

	public InfernalType(ResourceLocation id, @Nullable SpecialEffect specialEffect, @Nullable ItemDisplay itemDisplay){
		this.id = Objects.requireNonNull(id);
		this.specialEffect = specialEffect;
		this.itemDisplay = itemDisplay;
	}
	public InfernalType(ResourceLocation id, JsonObject object){
		this.id = Objects.requireNonNull(id);
		this.specialEffect = object.has("specialEffect") ? SpecialEffect.parse(object.get("specialEffect").getAsJsonObject()) : null;
		this.itemDisplay = object.has("itemDisplay") ? ItemDisplay.parse(object.get("itemDisplay").getAsJsonObject()) : null;
	}

	public ResourceLocation getId(){
		return id;
	}
	@Nullable public SpecialEffect getSpecialEffect(){
		return specialEffect;
	}
	@Nullable public ItemDisplay getItemDisplay(){
		return itemDisplay;
	}

	public JsonObject serialize(){
		JsonObject o = new JsonObject();
		serialize(o);
		return o;
	}

	public void serialize(JsonObject o){
		if(specialEffect!=null) o.add("specialEffect", specialEffect.serialize());
		if(itemDisplay!=null) o.add("itemDisplay", itemDisplay.serialize());
	}

	public void write(PacketBuffer buf){
		buf.writeResourceLocation(id);
		buf.writeBoolean(specialEffect!=null);
		if(specialEffect!=null) specialEffect.write(buf);
		buf.writeBoolean(itemDisplay!=null);
		if(itemDisplay!=null) itemDisplay.write(buf);
	}

	@Override public boolean equals(Object o){
		if(this==o) return true;
		if(o==null||getClass()!=o.getClass()) return false;
		InfernalType that = (InfernalType)o;
		return id.equals(that.id);
	}
	@Override public int hashCode(){
		return id.hashCode();
	}

	@Override public String toString(){
		return "InfernalType{"+
				"id="+id+
				", specialEffect="+specialEffect+
				", itemDisplay="+itemDisplay+
				'}';
	}
}
