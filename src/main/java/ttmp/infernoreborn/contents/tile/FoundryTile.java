package ttmp.infernoreborn.contents.tile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import ttmp.infernoreborn.InfernoReborn;
import ttmp.infernoreborn.capability.Caps;
import ttmp.infernoreborn.contents.ModBlocks;
import ttmp.infernoreborn.contents.ModRecipes;
import ttmp.infernoreborn.contents.ModTileEntities;
import ttmp.infernoreborn.contents.block.FoundryBlock;
import ttmp.infernoreborn.contents.container.FoundryContainer;
import ttmp.infernoreborn.contents.recipe.foundry.FoundryRecipe;
import ttmp.infernoreborn.inventory.FoundryInventory;
import ttmp.infernoreborn.util.Essence;
import ttmp.infernoreborn.util.EssenceHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;
import static net.minecraft.state.properties.BlockStateProperties.LIT;

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

	@Nullable public FoundryRecipe getCurrentRecipe(){
		return currentRecipe;
	}
	public int getProcess(){
		return process;
	}

	public void dropAllContents(){
		BlockPos pos = getBlockPos();
		for(int i = 0; i<inv.getSlots(); i++){
			ItemStack stackInSlot = inv.getStackInSlot(i);
			if(!stackInSlot.isEmpty()){
				InventoryHelper.dropItemStack(Objects.requireNonNull(getLevel()), pos.getX(), pos.getY(), pos.getZ(), stackInSlot);
				inv.setStackInSlot(i, ItemStack.EMPTY);
			}
		}
	}

	@Override public void tick(){
		if(level==null||level.isClientSide) return;
		ItemStack essenceHolder = inv.getStackInSlot(ESSENCE_HOLDER_SLOT);
		if(!essenceHolder.isEmpty()){
			//noinspection ConstantConditions
			@Nullable EssenceHandler h = essenceHolder.getCapability(Caps.essenceHandler).orElse(null);
			//noinspection ConstantConditions
			if(h!=null){
				ItemStack essenceInput = inv.getStackInSlot(ESSENCE_INPUT_SLOT);
				Essence essence = Essence.from(essenceInput);
				if(essence!=null){
					if(h.insertEssence(essence.getType(), essence.getAmount(), true)==essence.getAmount()){
						h.insertEssence(essence.getType(), essence.getAmount(), false);
						inv.setStackInSlot(ESSENCE_INPUT_SLOT, ItemStack.EMPTY);
					}else{
						essence = Essence.from(essenceInput.getItem(), 1);
						if(essence!=null&&h.insertEssence(essence.getType(), essence.getAmount(), true)==essence.getAmount()){
							h.insertEssence(essence.getType(), essence.getAmount(), false);
							essenceInput.shrink(1);
						}
					}
				}
			}
		}
		if(currentRecipe!=null){
			if(process<currentRecipe.getProcessingTime()) process++;
			if(process>=currentRecipe.getProcessingTime()){
				if(insertItems(currentRecipe, false)) searchRecipe();
			}
		}else searchRecipe();

		boolean lit = currentRecipe!=null;
		if(this.getBlockState().getValue(LIT)!=lit){
			BlockPos.Mutable mpos = new BlockPos.Mutable().set(getBlockPos());
			this.level.setBlock(mpos, this.getBlockState().setValue(LIT, lit), 3);
			BlockState fireboxBlockState = ModBlocks.FOUNDRY_FIREBOX.get()
					.defaultBlockState()
					.setValue(LIT, lit)
					.setValue(HORIZONTAL_FACING, this.getBlockState().getValue(HORIZONTAL_FACING));
			this.level.setBlock(FoundryBlock.moveFromOrigin(mpos, fireboxBlockState), fireboxBlockState, 3);
		}
	}

	private void searchRecipe(){
		this.process = 0;
		if(inv.getStackInSlot(INPUT_SLOT_1).isEmpty()&&inv.getStackInSlot(INPUT_SLOT_2).isEmpty()) return;
		MinecraftServer server = Objects.requireNonNull(level).getServer();
		if(server==null){
			InfernoReborn.LOGGER.warn("Foundry is trying to search for recipes in client side!");
			return;
		}

		if(currentRecipe!=null&&startIfCanHandleResult(currentRecipe)) return;
		for(FoundryRecipe r : server.getRecipeManager().getRecipesFor(ModRecipes.FOUNDRY_RECIPE_TYPE, foundryInventory, level))
			if(r!=currentRecipe&&startIfCanHandleResult(r)) return;

		this.currentRecipe = null;
	}

	private boolean startIfCanHandleResult(FoundryRecipe recipe){
		if(!insertItems(recipe, true)) return false;
		this.currentRecipe = recipe;
		return recipe.consume(foundryInventory, false);
	}

	private boolean insertItems(FoundryRecipe recipe, boolean simulate){
		ItemStackHandler ish = new ItemStackHandler(2);
		ish.setStackInSlot(0, inv.getStackInSlot(OUTPUT_SLOT_1).copy());
		ish.setStackInSlot(1, inv.getStackInSlot(OUTPUT_SLOT_2).copy());
		ItemStack result = recipe.getResultItem().copy();
		ItemStack byproduct = recipe.getByproduct().copy();

		for(int i = 0; i<ish.getSlots(); i++){
			result = ish.insertItem(i, result, false);
			byproduct = ish.insertItem(i, byproduct, false);
		}
		if(!result.isEmpty()||!byproduct.isEmpty()) return false;
		if(!simulate){
			inv.setStackInSlot(OUTPUT_SLOT_1, ish.getStackInSlot(0));
			inv.setStackInSlot(OUTPUT_SLOT_2, ish.getStackInSlot(1));
		}
		return true;
	}

	@Nullable private IItemHandler essenceInput;
	@Nullable private IItemHandler input;
	@Nullable private IItemHandler output;

	public IItemHandler getEssenceInput(){
		if(essenceInput==null) essenceInput = new RangedWrapper(inv, ESSENCE_INPUT_SLOT, ESSENCE_INPUT_SLOT+1){
			@Nonnull @Override public ItemStack extractItem(int slot, int amount, boolean simulate){
				return ItemStack.EMPTY;
			}
		};
		return essenceInput;
	}
	public IItemHandler getInput(){
		if(input==null) input = new RangedWrapper(inv, INPUT_SLOT_1, INPUT_SLOT_2+1){
			@Nonnull @Override public ItemStack extractItem(int slot, int amount, boolean simulate){
				return ItemStack.EMPTY;
			}
		};
		return input;
	}
	public IItemHandler getOutput(){
		if(output==null) output = new RangedWrapper(inv, OUTPUT_SLOT_1, OUTPUT_SLOT_2+1){
			@Nonnull @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
				return stack;
			}
		};
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

	@Nonnull @Override public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side){
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
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
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
					return stack.getCapability(Caps.essenceHandler).isPresent();
				case ESSENCE_INPUT_SLOT:
					return Essence.isEssenceItem(stack);
				default:
					return true;
			}
		}
	}

	private final class FoundryInventoryImpl extends RecipeWrapper implements FoundryInventory{
		FoundryInventoryImpl(){
			super((IItemHandlerModifiable)getInput());
		}

		@SuppressWarnings("ConstantConditions") @Nullable @Override public EssenceHandler getEssenceHandler(){
			return FoundryTile.this.inv.getStackInSlot(ESSENCE_HOLDER_SLOT).getCapability(Caps.essenceHandler).orElse(null);
		}
	}
}
