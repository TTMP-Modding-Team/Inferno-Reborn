package ttmp.infernoreborn.api.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

import static ttmp.infernoreborn.api.InfernoRebornApi.MODID;

public final class FluidTagIngredient extends FluidIngredient<FluidTagIngredient>{
	public static final FluidIngredientType<FluidTagIngredient> TYPE = new Type(new ResourceLocation(MODID, "tag"));

	private final ResourceLocation tagId;
	private final ITag<Fluid> tag;

	public FluidTagIngredient(ResourceLocation tagId, ITag<Fluid> tag, int amount){
		super(amount);
		this.tagId = tagId;
		this.tag = tag;
	}

	@Override public FluidIngredientType<FluidTagIngredient> type(){
		return TYPE;
	}
	@Override public FluidTagIngredient self(){
		return this;
	}

	public ResourceLocation getTagId(){
		return tagId;
	}
	public ITag<Fluid> getTag(){
		return tag;
	}

	@Override protected void createPreview(List<FluidStack> fluids){
		for(Fluid f : tag.getValues()) fluids.add(new FluidStack(f, 1));
	}
	@Override public boolean test(FluidStack fluidStack){
		return tag.contains(fluidStack.getFluid());
	}

	@Override public String toString(){
		return "[Fluid Tag: "+tagId+"] * "+amount;
	}

	private static final class Type extends FluidIngredientType<FluidTagIngredient>{
		public Type(ResourceLocation id){
			super(id);
		}
		@Override public FluidTagIngredient read(JsonObject object) throws JsonParseException{
			String tagValue = JSONUtils.getAsString(object, "tag");
			ResourceLocation tagId = ResourceLocation.tryParse(tagValue);
			if(tagId==null) throw new JsonParseException("Invalid tag '"+tagValue+"'");
			ITag<Fluid> tag = TagCollectionManager.getInstance().getFluids().getTag(tagId);
			if(tag==null) throw new JsonParseException("Invalid tag '"+tagValue+"'");
			int amount = JSONUtils.getAsInt(object, "amount");
			if(amount<=0) throw new JsonParseException("Non-positive fluid amount");

			return new FluidTagIngredient(tagId, tag, amount);
		}
		@Override protected void writeTo(FluidTagIngredient ingredient, JsonObject object){
			object.addProperty("tag", ingredient.getTagId().toString());
			object.addProperty("amount", ingredient.amount);
		}
		@Override public FluidTagIngredient read(PacketBuffer buffer){
			ResourceLocation tagId = buffer.readResourceLocation();
			return new FluidTagIngredient(tagId, TagCollectionManager.getInstance().getFluids().getTagOrEmpty(tagId),
					buffer.readVarInt());
		}
		@Override protected void writeTo(FluidTagIngredient ingredient, PacketBuffer buffer){
			buffer.writeResourceLocation(ingredient.getTagId());
			buffer.writeVarInt(ingredient.getAmount());
		}
	}
}
