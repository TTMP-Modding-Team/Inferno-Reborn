package ttmp.infernoreborn.api.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public abstract class FluidIngredientType<T extends FluidIngredient<T>>{
	public final ResourceLocation id;

	public FluidIngredientType(ResourceLocation id){
		this.id = Objects.requireNonNull(id);
	}

	public abstract T read(JsonObject object) throws JsonParseException;
	public final JsonObject write(T ingredient){
		JsonObject object = new JsonObject();
		writeTo(ingredient, object);
		object.addProperty("type", id.toString());
		return object;
	}

	protected abstract void writeTo(T ingredient, JsonObject object);

	public abstract T read(PacketBuffer buffer);
	public final void write(T ingredient, PacketBuffer buffer){
		buffer.writeResourceLocation(id);
		writeTo(ingredient, buffer);
	}
	protected abstract void writeTo(T ingredient, PacketBuffer buffer);

	@Override public boolean equals(Object o){
		if(this==o) return true;
		if(o==null||getClass()!=o.getClass()) return false;
		FluidIngredientType<?> that = (FluidIngredientType<?>)o;
		return id.equals(that.id);
	}
	@Override public int hashCode(){
		return Objects.hash(id);
	}

	@Override public String toString(){
		return "FluidIngredientSerializer{"+
				"id="+id+
				'}';
	}
}
