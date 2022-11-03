package ttmp.infernoreborn.contents.recipe.foundry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import ttmp.infernoreborn.contents.ModItems;
import ttmp.infernoreborn.contents.ModRecipes;
import ttmp.infernoreborn.inventory.FoundryInventory;
import ttmp.infernoreborn.util.EssenceHandler;
import ttmp.infernoreborn.util.EssenceHolder;
import ttmp.infernoreborn.util.EssenceType;
import ttmp.infernoreborn.util.Essences;
import ttmp.infernoreborn.util.QuantifiedIngredient;

import javax.annotation.Nullable;

public class FoundryRecipe implements IRecipe<FoundryInventory>{
	public static final int DEFAULT_PROCESSING_TIME = 1000;

	private final ResourceLocation id;

	private final QuantifiedIngredient[] ingredients;
	@Nullable private final Essences essences;

	private final int processingTime;

	private final ItemStack result;
	private final ItemStack byproduct;

	public FoundryRecipe(ResourceLocation id, QuantifiedIngredient[] ingredients, @Nullable Essences essences, int processingTime, ItemStack result, ItemStack byproduct){
		this.id = id;
		this.ingredients = ingredients;
		this.essences = essences;
		this.processingTime = processingTime;
		this.result = result;
		this.byproduct = byproduct;
	}

	public int getProcessingTime(){
		return processingTime;
	}

	@Override public boolean matches(FoundryInventory inv, World world){
		return consume(inv, true);
	}
	@Deprecated @Override public ItemStack assemble(FoundryInventory inv){
		return this.result.copy();
	}

	public boolean consume(FoundryInventory inv, boolean simulate){
		int[] consumptions = workOutConsumptions(inv);
		if(consumptions==null) return false;
		if(essences!=null&&!essences.isEmpty()){
			EssenceHandler essenceHolder = inv.getEssenceHandler();
			if(essenceHolder==null||!essenceHolder.extractEssences(essences, simulate)) return false;
		}
		if(!simulate){
			for(int i = 0; i<consumptions.length; i++)
				inv.removeItem(i, consumptions[i]);
		}
		return true;
	}

	@Nullable private int[] workOutConsumptions(FoundryInventory inv){
		int[] consumptions = new int[inv.getContainerSize()];
		for(QuantifiedIngredient ing : ingredients){
			int needed = ing.getQuantity();
			for(int i = 0; i<inv.getContainerSize()&&needed>0; i++){
				ItemStack stack = inv.getItem(i);
				if(!stack.isEmpty()&&ing.getIngredient().test(stack)){
					int amountLeft = Math.min(needed, stack.getCount()-consumptions[i]);
					consumptions[i] += amountLeft;
					needed -= amountLeft;
				}
			}
			if(needed>0) return null;
		}
		return consumptions;
	}

	@Override public boolean canCraftInDimensions(int width, int height){
		return true;
	}
	@Override public ItemStack getResultItem(){
		return result;
	}
	public ItemStack getByproduct(){
		return byproduct;
	}
	@Override public ResourceLocation getId(){
		return id;
	}
	@Override public IRecipeSerializer<?> getSerializer(){
		return ModRecipes.FOUNDRY.get();
	}
	@Override public IRecipeType<?> getType(){
		return ModRecipes.FOUNDRY_RECIPE_TYPE;
	}

	@Deprecated @Override public NonNullList<ItemStack> getRemainingItems(FoundryInventory inv){
		return NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
	}
	@Deprecated @Override public NonNullList<Ingredient> getIngredients(){
		return NonNullList.create();
	}
	@Override public boolean isSpecial(){
		return false;
	}
	@Override public ItemStack getToastSymbol(){
		return new ItemStack(ModItems.PRIMAL_INFERNO_SPARK.get()); // TODO
	}

	public QuantifiedIngredient[] getQuantifiedIngredients(){
		return ingredients;
	}
	public Essences getEssences(){
		return essences!=null ? essences : Essences.EMPTY;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FoundryRecipe>{
		@Override public FoundryRecipe fromJson(ResourceLocation id, JsonObject o){
			JsonArray ingredientsJson = JSONUtils.getAsJsonArray(o, "ingredients");
			QuantifiedIngredient[] ingredients = new QuantifiedIngredient[ingredientsJson.size()];
			for(int i = 0; i<ingredientsJson.size(); i++){
				JsonElement e = ingredientsJson.get(i);
				ingredients[i] = new QuantifiedIngredient(e.getAsJsonObject());
			}
			if(ingredients.length==0) throw new JsonParseException("No ingredients");
			if(ingredients.length>2) throw new JsonParseException("Too many ingredients");
			//noinspection ConstantConditions
			JsonObject essencesJson = JSONUtils.getAsJsonObject(o, "essences", null);
			EssenceHolder essences;
			//noinspection ConstantConditions
			if(essencesJson!=null){
				essences = new EssenceHolder();
				for(EssenceType t : EssenceType.values())
					if(essencesJson.has(t.id))
						essences.setEssence(t, JSONUtils.getAsInt(essencesJson, t.id));
			}else essences = null;
			int processingTime = JSONUtils.getAsInt(o, "processingTime", DEFAULT_PROCESSING_TIME);
			ItemStack result = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(o, "result"));
			//noinspection ConstantConditions
			JsonObject byproductJson = JSONUtils.getAsJsonObject(o, "byproduct", null);
			//noinspection ConstantConditions
			ItemStack byproduct = byproductJson!=null ? ShapedRecipe.itemFromJson(byproductJson) : ItemStack.EMPTY;
			return new FoundryRecipe(id, ingredients, essences, processingTime, result, byproduct);
		}

		@Override public FoundryRecipe fromNetwork(ResourceLocation id, PacketBuffer buf){
			QuantifiedIngredient[] ingredients = new QuantifiedIngredient[buf.readUnsignedByte()];
			for(int i = 0; i<ingredients.length; i++) ingredients[i] = QuantifiedIngredient.read(buf);
			EssenceHolder essenceHolder;
			short essenceHolderSize = buf.readUnsignedByte();
			if(essenceHolderSize>0){
				essenceHolder = new EssenceHolder();
				for(int i = essenceHolderSize; i>=0; i--){
					EssenceType type = EssenceType.values()[buf.readUnsignedByte()];
					int amount = buf.readVarInt();
					essenceHolder.setEssence(type, amount);
				}
			}else essenceHolder = null;
			int processingTime = buf.readVarInt();
			ItemStack result = buf.readItem();
			ItemStack byproduct = buf.readItem();
			return new FoundryRecipe(id, ingredients, essenceHolder, processingTime, result, byproduct);
		}

		@Override public void toNetwork(PacketBuffer buf, FoundryRecipe recipe){
			buf.writeByte(recipe.ingredients.length);
			for(QuantifiedIngredient ing : recipe.ingredients) ing.write(buf);
			if(recipe.essences!=null){
				int essences = 0;
				int idx = buf.writerIndex();
				buf.writeByte(0);
				for(EssenceType type : EssenceType.values()){
					int essence = recipe.essences.getEssence(type);
					if(essence>0){
						buf.writeByte(type.ordinal());
						buf.writeVarInt(essence);
						essences++;
					}
				}
				if(essences>0) buf.setByte(idx, essences);
			}else buf.writeByte(0);
			buf.writeVarInt(recipe.processingTime);
			buf.writeItem(recipe.result);
			buf.writeItem(recipe.byproduct);
		}
	}
}
