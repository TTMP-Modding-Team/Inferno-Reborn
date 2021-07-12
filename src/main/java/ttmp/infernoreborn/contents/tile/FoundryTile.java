package ttmp.infernoreborn.contents.tile;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import ttmp.infernoreborn.capability.Caps;
import ttmp.infernoreborn.contents.ModTileEntities;
import ttmp.infernoreborn.contents.container.EssenceHolderItemHandler;
import ttmp.infernoreborn.util.EssenceHolder;

import javax.annotation.Nullable;

public class FoundryTile extends TileEntity implements ITickableTileEntity{
	public static final int ESSENCE_HOLDER_SLOT = 0;
	public static final int ESSENCE_INPUT_SLOT = 1;
	public static final int INPUT_SLOT_1 = 2;
	public static final int INPUT_SLOT_2 = 3;
	public static final int OUTPUT_SLOT_1 = 4;
	public static final int OUTPUT_SLOT_2 = 5;

	private final ItemStackHandler inv = new ItemStackHandler(6);

	public FoundryTile(){
		this(ModTileEntities.FOUNDRY.get());
	}
	public FoundryTile(TileEntityType<?> type){
		super(type);
	}

	@Override public void tick(){
		ItemStack essenceHolder = inv.getStackInSlot(ESSENCE_HOLDER_SLOT);
		if(!essenceHolder.isEmpty()){
			essenceHolder.getCapability(Caps.essenceHolder).ifPresent(e -> {
				EssenceHolderItemHandler eih = EssenceHolderItemHandler.withInstance(e);
				inv.setStackInSlot(ESSENCE_INPUT_SLOT, );
			});
		}
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
		inv.deserializeNBT(nbt.getCompound("inv"));
		super.load(state, nbt);
	}
	@Override public CompoundNBT save(CompoundNBT nbt){
		nbt.put("inv", inv.serializeNBT());
		return super.save(nbt);
	}

	@Override protected void invalidateCaps(){
		super.invalidateCaps();
		if(essenceInputLO!=null) essenceInputLO.invalidate();
		if(inputLO!=null) inputLO.invalidate();
		if(outputLO!=null) outputLO.invalidate();
	}
}
