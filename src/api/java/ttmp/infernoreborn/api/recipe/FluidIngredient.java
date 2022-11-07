package ttmp.infernoreborn.api.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public abstract class FluidIngredient<T extends FluidIngredient<T>> implements Predicate<FluidStack>{
	protected final int amount;

	protected FluidIngredient(int amount){
		this.amount = amount;
	}

	public abstract FluidIngredientType<T> type();
	public abstract T self();

	public final int getAmount(){
		return amount;
	}

	@Nullable private volatile List<FluidStack> preview;
	public List<FluidStack> preview(){
		if(preview==null){
			synchronized(this){
				if(preview==null){
					List<FluidStack> preview = new ArrayList<>();
					createPreview(preview);
					this.preview = preview;
				}
			}
		}
		return Objects.requireNonNull(preview);
	}

	protected abstract void createPreview(List<FluidStack> fluids);

	public final JsonObject write(){
		return type().write(self());
	}
	public final void write(PacketBuffer buffer){
		type().write(self(), buffer);
	}

	@Override public abstract String toString();

	private static final Map<ResourceLocation, FluidIngredientType<?>> fluidIngredients = new HashMap<>();

	public static void register(FluidIngredientType<?> type){
		if(fluidIngredients.putIfAbsent(type.id, type)!=null)
			throw new IllegalStateException("Duplicated registration of fluid ingredient type '"+type.id+"'");
	}

	public static FluidIngredient<?> readFrom(JsonObject object){
		if(object.has("type")){
			String typeValue = JSONUtils.getAsString(object, "type");
			ResourceLocation typeId = ResourceLocation.tryParse(typeValue);
			if(typeId==null) throw new JsonParseException("Invalid type '"+typeValue+"'");
			FluidIngredientType<?> type = fluidIngredients.get(typeId);
			if(type==null) throw new JsonParseException("Unknown type '"+typeValue+"'");
			return type.read(object);
		}
		if(object.has("fluid")&&object.has("tag"))
			throw new JsonParseException("Cannot define both fluid and tag property");
		if(object.has("fluid")) return SimpleFluidIngredient.TYPE.read(object);
		if(object.has("tag")) return FluidTagIngredient.TYPE.read(object);
		throw new JsonParseException("Fluid ingredient with no property");
	}

	public static FluidIngredient<?> readFrom(PacketBuffer buffer){
		ResourceLocation id = buffer.readResourceLocation();
		FluidIngredientType<?> type = fluidIngredients.get(id);
		if(type!=null) return type.read(buffer);
		throw new IllegalArgumentException("Unknown fluid ingredient type '"+id+"'");
	}
}
