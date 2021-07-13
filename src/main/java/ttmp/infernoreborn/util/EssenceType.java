package ttmp.infernoreborn.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Locale;
import java.util.function.Supplier;

public enum EssenceType{
	BLOOD, METAL, EARTH, MAGIC, FIRE, AIR, WATER, DEATH, DOMINANCE;

	public final String id = name().toLowerCase(Locale.ROOT);

	private Supplier<Item> shardItemSupplier;
	private Supplier<Item> crystalItemSupplier;
	private Supplier<Item> greaterCrystalItemSupplier;

	public ITextComponent getName(){
		return new TranslationTextComponent("infernoreborn.essence."+id);
	}

	public Item getShardItem(){
		return shardItemSupplier.get();
	}
	public Item getCrystalItem(){
		return crystalItemSupplier.get();
	}
	public Item getGreaterCrystalItem(){
		return greaterCrystalItemSupplier.get();
	}

	public Item getItem(EssenceSize size){
		switch(size){
			case SHARD:
				return shardItemSupplier.get();
			case CRYSTAL:
				return crystalItemSupplier.get();
			case GREATER_CRYSTAL:
				return greaterCrystalItemSupplier.get();
			default:
				throw new IllegalStateException("Unreachable");
		}
	}

	/**
	 * Don't ever call this, I'll cut your throat
	 */
	public void setItem(Supplier<Item> item, EssenceSize size){
		switch(size){
			case SHARD:
				if(shardItemSupplier!=null) throw new IllegalStateException("Duplicated item set for "+size);
				shardItemSupplier = item;
				break;
			case CRYSTAL:
				if(crystalItemSupplier!=null) throw new IllegalStateException("Duplicated item set for "+size);
				crystalItemSupplier = item;
				break;
			case GREATER_CRYSTAL:
				if(greaterCrystalItemSupplier!=null) throw new IllegalStateException("Duplicated item set for "+size);
				greaterCrystalItemSupplier = item;
				break;
			default:
				throw new IllegalStateException("Unreachable");
		}
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
}
