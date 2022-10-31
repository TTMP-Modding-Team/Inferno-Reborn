package ttmp.infernoreborn.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Essence{
	private final EssenceType type;
	private final int amount;

	public Essence(EssenceType type, int amount){
		this.type = type;
		this.amount = amount;
	}

	public EssenceType getType(){
		return type;
	}
	public int getAmount(){
		return amount;
	}

	@Nullable public static Essence from(ItemStack stack){
		return from(stack.getItem(), stack.getCount());
	}
	@Nullable public static Essence from(Item item, int count){
		for(EssenceType t : EssenceType.values())
			for(EssenceSize s : EssenceSize.values())
				if(t.getItem(s)==item) return new Essence(t, count*s.getCompressionRate());
		return null;
	}

	public static boolean isEssenceItem(ItemStack stack){
		return isEssenceItem(stack.getItem());
	}
	public static boolean isEssenceItem(Item item){
		for(EssenceType t : EssenceType.values())
			for(EssenceSize s : EssenceSize.values())
				if(t.getItem(s)==item) return true;
		return false;
	}

	public static List<ItemStack> items(EssenceType type, int amount){
		if(amount<=0) return Collections.emptyList();
		List<ItemStack> items = new ArrayList<>();
		addItems(items, type, amount);
		return items;
	}

	public static List<ItemStack> items(EssenceHolder holder){
		if(holder.isEmpty()) return Collections.emptyList();
		List<ItemStack> items = new ArrayList<>();
		for(EssenceType t : EssenceType.values())
			addItems(items, t, holder.getEssence(t));
		return items;
	}

	public static void addItems(List<ItemStack> items, EssenceType type, int amount){
		if(amount>=9*9){
			items.add(new ItemStack(type.getExquisiteEssenceItem(), amount/9*9));
			amount %= 9*9;
		}
		if(amount>=9){
			items.add(new ItemStack(type.getGreaterEssenceItem(), amount/9));
			amount %= 9;
		}
		if(amount>=1) items.add(new ItemStack(type.getEssenceItem(), amount));
	}
}
