package ttmp.infernoreborn.datagen.book;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;

import java.util.function.Supplier;

public final class Stack{
	private final ItemStack stack;

	public Stack(Supplier<ItemStack> stackSupplier){
		this(stackSupplier.get());
	}
	public Stack(ItemStack stack){
		this.stack = stack;
	}
	public Stack(IItemProvider itemSupplier){
		this(itemSupplier.asItem());
	}
	public Stack(Item item){
		this.stack = new ItemStack(item);
	}

	@Override public String toString(){
		StringBuilder b = new StringBuilder().append(stack.getItem().getRegistryName());
		if(stack.getCount()>1) b.append('#').append(stack.getCount());
		if(stack.hasTag()) b.append(stack.getTag());
		return b.toString();
	}
}
