package ttmp.infernoreborn.api.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

public final class SimpleFluidIngredient extends FluidIngredient<SimpleFluidIngredient>{
	public static final FluidIngredientType<SimpleFluidIngredient> TYPE = new Type(new ResourceLocation(MODID, "fluid"));

	private final List<Fluid> fluids;

	public SimpleFluidIngredient(int amount, Fluid... fluids){
		super(amount);
		this.fluids = Arrays.asList(fluids.clone());
		if(fluids.length==0) throw new IllegalArgumentException("No fluid");
	}
	public SimpleFluidIngredient(List<Fluid> fluids, int amount){
		super(amount);
		this.fluids = new ArrayList<>(fluids);
		if(this.fluids.isEmpty()) throw new IllegalArgumentException("No fluid");
	}

	@Override public FluidIngredientType<SimpleFluidIngredient> type(){
		return TYPE;
	}
	@Override public SimpleFluidIngredient self(){
		return this;
	}

	@Override protected void createPreview(List<FluidStack> fluids){
		for(Fluid f : this.fluids) fluids.add(new FluidStack(f, 1));
	}
	@Override public boolean test(FluidStack fluidStack){
		Fluid fluid = fluidStack.getFluid();
		for(Fluid f : fluids) if(f.isSame(fluid)) return true;
		return false;
	}

	@Override public String toString(){
		return (fluids.size()==1 ? fluids.get(0).getRegistryName() :
				"["+fluids.stream().map(s -> s.getRegistryName()+"").collect(Collectors.joining(", "))+"]")
				+" * "+amount;
	}

	private static final class Type extends FluidIngredientType<SimpleFluidIngredient>{
		public Type(ResourceLocation id){
			super(id);
		}
		@Override public SimpleFluidIngredient read(JsonObject object) throws JsonParseException{
			JsonElement fluidElement = object.get("fluid");
			if(fluidElement==null) throw new JsonParseException("Missing property 'fluid'");
			List<Fluid> fluids;
			if(fluidElement.isJsonArray()){
				fluids = new ArrayList<>();
				for(JsonElement e : fluidElement.getAsJsonArray()) fluids.add(readFluid(e));
			}else fluids = Collections.singletonList(readFluid(fluidElement));

			int amount = JSONUtils.getAsInt(object, "amount");
			if(amount<=0) throw new JsonParseException("Non-positive fluid amount");

			return new SimpleFluidIngredient(fluids, amount);
		}
		@Override protected void writeTo(SimpleFluidIngredient ingredient, JsonObject object){
			if(ingredient.fluids.size()==1){
				object.addProperty("fluid", Objects.requireNonNull(ingredient.fluids.get(0).getFluid().getRegistryName()).toString());
			}else{
				JsonArray arr = new JsonArray();
				for(Fluid fluid : ingredient.fluids)
					arr.add(Objects.requireNonNull(fluid.getRegistryName()).toString());
				object.add("fluid", arr);
			}
			object.addProperty("amount", ingredient.amount);
		}
		@Override public SimpleFluidIngredient read(PacketBuffer buffer){
			List<Fluid> fluids = new ArrayList<>();
			for(int i = buffer.readVarInt(); i>0; i--){
				Fluid f = buffer.readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
				if(f!=null) fluids.add(f);
			}
			return new SimpleFluidIngredient(fluids, buffer.readVarInt());
		}
		@Override protected void writeTo(SimpleFluidIngredient ingredient, PacketBuffer buffer){
			buffer.writeVarInt(ingredient.fluids.size());
			for(Fluid fluid : ingredient.fluids) buffer.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS, fluid);
			buffer.writeVarInt(ingredient.amount);
		}
	}

	private static Fluid readFluid(JsonElement element){
		String fluidValue = element.getAsJsonPrimitive().getAsString();
		ResourceLocation fluidId = ResourceLocation.tryParse(fluidValue);
		if(fluidId==null) throw new JsonParseException("Invalid fluid '"+fluidValue+"'");
		Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidId);
		if(fluid==null) throw new JsonParseException("Unknown fluid '"+fluidValue+"'");
		return fluid;
	}
}
