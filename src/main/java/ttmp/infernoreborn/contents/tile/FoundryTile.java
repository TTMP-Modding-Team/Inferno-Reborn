package ttmp.infernoreborn.contents.tile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.capability.Caps;
import ttmp.infernoreborn.contents.ModRecipes;
import ttmp.infernoreborn.contents.ModTileEntities;
import ttmp.infernoreborn.contents.container.FoundryContainer;
import ttmp.infernoreborn.contents.recipe.foundry.FoundryRecipe;
import ttmp.infernoreborn.inventory.EssenceHolderItemHandler;
import ttmp.infernoreborn.inventory.FoundryInventory;
import ttmp.infernoreborn.util.EssenceHolder;
import ttmp.infernoreborn.util.EssenceType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class FoundryTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider{
	public static final int ESSENCE_HOLDER_SLOT = 0;
	public static final int ESSENCE_INPUT_SLOT = 1;
	public static final int INPUT_SLOT_1 = 2;
	public static final int INPUT_SLOT_2 = 3;
	public static final int OUTPUT_SLOT_1 = 4;
	public static final int OUTPUT_SLOT_2 = 5;

	private final ItemStackHandler inv = new ItemHandler();
	private final FoundryInventory foundryInventory = new FoundryInventoryImpl();

	@Nullable private FoundryRecipe currentRecipe;
	private int process;

	public FoundryTile(){
		this(ModTileEntities.FOUNDRY.get());
	}
	public FoundryTile(TileEntityType<?> type){
		super(type);
	}

	public int getProcess(){
		return process;
	}

	@Override public void tick(){
		if(level==null||level.isClientSide) return;
		ItemStack essenceHolder = inv.getStackInSlot(ESSENCE_HOLDER_SLOT);
		ItemStack essence = inv.getStackInSlot(ESSENCE_INPUT_SLOT);
		if(!essenceHolder.isEmpty()&&!essence.isEmpty()){
			//noinspection ConstantConditions
			@Nullable EssenceHolder h = essenceHolder.getCapability(Caps.essenceHolder).orElse(null);
			//noinspection ConstantConditions
			if(h!=null){
				EssenceHolderItemHandler ih = EssenceHolderItemHandler.withInstance(h);
				for(int i = 0; i<ih.getSlots(); i++){
					essence = ih.insertItem(i, essence, false);
					if(essence.isEmpty()) break;
				}
				inv.setStackInSlot(ESSENCE_INPUT_SLOT, essence);
			}
		}
		if(currentRecipe!=null){
			if(process<currentRecipe.getProcessingTime()) process++;
			if(process>=currentRecipe.getProcessingTime()){
				if(insertItems(currentRecipe, false)) updateCurrentRecipe();
			}
		}
	}

	private void updateCurrentRecipe(){
		MinecraftServer server = Objects.requireNonNull(level).getServer();
		if(server==null){
			InfernoReborn.LOGGER.warn("Foundry is trying to search for recipes in client side!");
			return;
		}
		this.process = 0;

		if(currentRecipe!=null)
			if(startIfCanHandleResult(currentRecipe)) return;
		for(FoundryRecipe r : server.getRecipeManager().getRecipesFor(ModRecipes.FOUNDRY_RECIPE_TYPE, foundryInventory, level))
			if(startIfCanHandleResult(r)) return;

		this.currentRecipe = null;
	}

	private boolean startIfCanHandleResult(FoundryRecipe recipe){
		if(!insertItems(recipe, true)) return false;
		this.currentRecipe = recipe;
		recipe.consume(foundryInventory, false);
		return true;
	}

	private boolean insertItems(FoundryRecipe recipe, boolean simulate){
		int[] insertions = new int[2];
		ItemStack result = recipe.getResultItem();
		int resultLeft = result.getCount();
		ItemStack byproduct = recipe.getByproduct();
		int byproductLeft = byproduct.getCount();

		for(int i = 0; i<insertions.length; i++){
			int slot = i+OUTPUT_SLOT_1;

			int inserted = simulateInsert(slot, result, resultLeft, insertions[i]);
			if(inserted>0){
				insertions[i] += inserted;
				resultLeft -= inserted;
			}
			inserted = simulateInsert(slot, byproduct, byproductLeft, insertions[i]);
			if(inserted>0){
				insertions[i] += inserted;
				byproductLeft -= inserted;
			}
		}
		if(resultLeft>0||byproductLeft>0) return false;
		if(!simulate){
			resultLeft = result.getCount();
			byproductLeft = byproduct.getCount();

			for(int i = 0; i<insertions.length; i++){
				int slot = i+OUTPUT_SLOT_1;

				int inserted = insert(slot, result, resultLeft);
				if(inserted>0){
					resultLeft -= inserted;
				}
				inserted = insert(slot, byproduct, byproductLeft);
				if(inserted>0){
					byproductLeft -= inserted;
				}
			}
			if(resultLeft>0||byproductLeft>0){
				InfernoReborn.LOGGER.error("Failed to insert result items into Foundry inventory, resultLeft = {}, byproductLeft = {}", resultLeft, byproductLeft);
			}
		}
		return true;
	}

	private int simulateInsert(int slot, ItemStack stack, int amountToInsert, int amountInserted){
		if(amountToInsert<=0) return 0;
		ItemStack stackInSlot = inv.getStackInSlot(slot);
		if(stackInSlot.isEmpty()) return amountToInsert;
		else if(!Container.consideredTheSameItem(stackInSlot, stack)) return 0;
		return Math.min(amountToInsert, stackInSlot.getMaxStackSize()-stackInSlot.getCount()-amountInserted);
	}

	private int insert(int slot, ItemStack stack, int amountToInsert){
		if(amountToInsert<=0) return 0;
		ItemStack stackInSlot = inv.getStackInSlot(slot);
		if(stackInSlot.isEmpty()){
			ItemStack copy = stack.copy();
			copy.setCount(amountToInsert);
			inv.setStackInSlot(slot, copy);
			return amountToInsert;
		}else if(!Container.consideredTheSameItem(stackInSlot, stack)) return 0;
		int toInsert = Math.min(amountToInsert, stackInSlot.getMaxStackSize()-stackInSlot.getCount());
		stackInSlot.grow(toInsert);
		return toInsert;
	}

	@Nullable private IItemHandler essenceInput;
	@Nullable private IItemHandler input;
	@Nullable private IItemHandler output;

	public IItemHandler getEssenceInput(){
		if(essenceInput==null) essenceInput = new RangedWrapper(inv, ESSENCE_INPUT_SLOT, ESSENCE_INPUT_SLOT+1);
		return essenceInput;
	}
	public IItemHandler getInput(){
		if(input==null) input = new RangedWrapper(inv, INPUT_SLOT_1, INPUT_SLOT_2+1);
		return input;
	}
	public IItemHandler getOutput(){
		if(output==null) output = new RangedWrapper(inv, OUTPUT_SLOT_1, OUTPUT_SLOT_2+1);
		return output;
	}

	@Nullable private LazyOptional<IItemHandler> essenceInputLO;
	@Nullable private LazyOptional<IItemHandler> inputLO;
	@Nullable private LazyOptional<IItemHandler> outputLO;

	public LazyOptional<IItemHandler> getEssenceInputLO(){
		if(essenceInputLO==null) essenceInputLO = LazyOptional.of(this::getEssenceInput);
		return essenceInputLO;
	}
	public LazyOptional<IItemHandler> getInputLO(){
		if(inputLO==null) inputLO = LazyOptional.of(this::getInput);
		return inputLO;
	}
	public LazyOptional<IItemHandler> getOutputLO(){
		if(outputLO==null) outputLO = LazyOptional.of(this::getOutput);
		return outputLO;
	}

	@Override public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side){
		if(cap==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return getEssenceInputLO().cast();
		return super.getCapability(cap, side);
	}

	@Override public void load(BlockState state, CompoundNBT nbt){
		if(nbt.contains("currentRecipe", Constants.NBT.TAG_STRING)){
			currentRecipe = getFoundryRecipe(new ResourceLocation(nbt.getString("currentRecipe")));
			process = nbt.getInt("process");
		}else{
			currentRecipe = null;
			process = 0;
		}
		inv.deserializeNBT(nbt.getCompound("inv"));
		super.load(state, nbt);
	}
	@Override public CompoundNBT save(CompoundNBT nbt){
		if(currentRecipe!=null){
			nbt.putString("currentRecipe", currentRecipe.getId().toString());
			nbt.putInt("process", process);
		}
		nbt.put("inv", inv.serializeNBT());
		return super.save(nbt);
	}

	@Nullable private FoundryRecipe getFoundryRecipe(ResourceLocation id){
		World level = getLevel();
		if(level==null) return null;
		MinecraftServer server = level.getServer();
		if(server==null) return null;
		IRecipe<?> r = server.getRecipeManager().byKey(id).orElse(null);
		return r instanceof FoundryRecipe ? (FoundryRecipe)r : null;
	}

	@Override protected void invalidateCaps(){
		super.invalidateCaps();
		if(essenceInputLO!=null) essenceInputLO.invalidate();
		if(inputLO!=null) inputLO.invalidate();
		if(outputLO!=null) outputLO.invalidate();
	}

	@Override public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.infernoreborn.foundry");
	}
	@Override public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player){
		return new FoundryContainer(id, playerInventory, inv, () -> process, () -> currentRecipe!=null ? currentRecipe.getProcessingTime() : 0);
	}

	public static final class ItemHandler extends ItemStackHandler{
		public ItemHandler(){
			super(6);
		}

		@Override public boolean isItemValid(int slot, @Nonnull ItemStack stack){
			switch(slot){
				case ESSENCE_HOLDER_SLOT:
					return stack.getCapability(Caps.essenceHolder).isPresent();
				case ESSENCE_INPUT_SLOT:
					return EssenceType.isEssenceItem(stack);
				default:
					return true;
			}
		}
	}

	private final class FoundryInventoryImpl extends RecipeWrapper implements FoundryInventory{
		FoundryInventoryImpl(){
			super((IItemHandlerModifiable)getInput());
		}

		@SuppressWarnings("ConstantConditions") @Nullable @Override public EssenceHolder getEssenceHolder(){
			return inv.getStackInSlot(ESSENCE_HOLDER_SLOT).getCapability(Caps.essenceHolder).orElse(null);
		}
	}
}
