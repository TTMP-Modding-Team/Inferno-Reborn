package ttmp.infernoreborn.contents.container;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import ttmp.infernoreborn.capability.EssenceHolder;
import ttmp.infernoreborn.util.EssenceSize;
import ttmp.infernoreborn.util.EssenceType;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class EssenceHolderItemHandler implements IItemHandler{
	public static EssenceHolderItemHandler withInstance(EssenceHolder essenceHolder){
		return new EssenceHolderItemHandler(){
			@Override public EssenceHolder getEssenceHolder(){
				return essenceHolder;
			}
		};
	}
	public static EssenceHolderItemHandler withSupplier(Supplier<EssenceHolder> supplier){
		return new EssenceHolderItemHandler(){
			@Override public EssenceHolder getEssenceHolder(){
				return supplier.get();
			}
		};
	}
	public static EssenceHolderItemHandler withLazyOptional(Supplier<LazyOptional<EssenceHolder>> supplier){
		return withLazyOptional(supplier, new EssenceHolder());
	}
	public static EssenceHolderItemHandler withLazyOptional(Supplier<LazyOptional<EssenceHolder>> supplier, EssenceHolder defaultEssenceHolder){
		return new EssenceHolderItemHandler(){
			@Nullable private LazyOptional<EssenceHolder> lazyOptional;

			@Override public EssenceHolder getEssenceHolder(){
				if(lazyOptional!=null) return lazyOptional.orElse(defaultEssenceHolder);
				lazyOptional = supplier.get();
				EssenceHolder essenceHolder = lazyOptional.orElse(defaultEssenceHolder);
				lazyOptional.addListener(lo -> {
					if(lazyOptional==lo) lazyOptional = null;
				});
				return essenceHolder;
			}
		};
	}

	public abstract EssenceHolder getEssenceHolder();

	@Override public int getSlots(){
		return EssenceType.values().length*EssenceSize.values().length;
	}

	@Override public ItemStack getStackInSlot(int slot){
		EssenceType type = type(slot);
		EssenceSize size = size(slot);
		return new ItemStack(type.getItem(size), Math.min(64, getEssenceHolder().getEssence(type)/size.getCompressionRate()));
	}

	@Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
		if(stack.isEmpty()) return ItemStack.EMPTY;
		EssenceType type = type(slot);
		EssenceSize size = size(slot);
		if(type.getItem(size)!=stack.getItem()) return stack;

		int totalEssence = stack.getCount()*size.getCompressionRate();
		EssenceHolder essenceHolder = getEssenceHolder();
		int essencesTaken = essenceHolder.insertEssence(type, totalEssence, true);
		if(totalEssence==essencesTaken){
			if(!simulate) essenceHolder.insertEssence(type, totalEssence, false);
			return ItemStack.EMPTY;
		}
		int essenceToBeTaken = essencesTaken-essencesTaken%size.getCompressionRate();
		int insertedItem = essenceToBeTaken/size.getCompressionRate();
		if(essenceHolder.insertEssence(type, essenceToBeTaken, true)==essenceToBeTaken){
			if(!simulate) essenceHolder.insertEssence(type, essenceToBeTaken, false);
			ItemStack copy = stack.copy();
			copy.shrink(insertedItem);
			return copy;
		}
		// Fuck you
		return stack;
	}

	@Override public ItemStack extractItem(int slot, int amount, boolean simulate){
		if(amount<=0) return ItemStack.EMPTY;
		EssenceType type = type(slot);
		EssenceSize size = size(slot);
		EssenceHolder essenceHolder = getEssenceHolder();
		int amountStored = essenceHolder.getEssence(type)/size.getCompressionRate();
		if(amountStored<=0) return ItemStack.EMPTY;

		int finalAmount = Math.min(amountStored, amount);
		if(!simulate) essenceHolder.extractEssence(type, finalAmount*size.getCompressionRate(), false);
		return new ItemStack(type.getItem(size), finalAmount);
	}

	@Override public int getSlotLimit(int slot){
		return 64;
	}

	@Override public boolean isItemValid(int slot, ItemStack stack){
		return item(slot)==stack.getItem();
	}

	public EssenceType type(int slot){
		return EssenceType.values()[slot/EssenceSize.values().length%EssenceType.values().length];
	}
	public EssenceSize size(int slot){
		return EssenceSize.values()[slot%EssenceSize.values().length];
	}
	public Item item(int slot){
		EssenceType type = type(slot);
		EssenceSize size = size(slot);
		return type.getItem(size);
	}
}
