package ttmp.infernoreborn.util;

import net.minecraft.item.Item;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Locale;
import java.util.function.Supplier;

public enum EssenceType{
	BLOOD, METAL, MAGIC, EARTH, FIRE, AIR, WATER, FROST, DEATH, DOMINANCE;

	public final String id = name().toLowerCase(Locale.ROOT);

	private Supplier<Item> essenceItem;
	private Supplier<Item> greaterEssenceItem;
	private Supplier<Item> exquisiteEssenceItem;

	public ITextComponent getName(){
		return new TranslationTextComponent("infernoreborn.essence."+id);
	}

	public Item getEssenceItem(){
		return essenceItem.get();
	}
	public Item getGreaterEssenceItem(){
		return greaterEssenceItem.get();
	}
	public Item getExquisiteEssenceItem(){
		return exquisiteEssenceItem.get();
	}

	public Item getItem(EssenceSize size){
		switch(size){
			case ESSENCE:
				return essenceItem.get();
			case GREATER_ESSENCE:
				return greaterEssenceItem.get();
			case EXQUISITE_ESSENCE:
				return exquisiteEssenceItem.get();
			default:
				throw new IllegalStateException("Unreachable");
		}
	}

	/**
	 * Don't ever call this, I'll cut your throat
	 */
	public void setItem(Supplier<Item> item, EssenceSize size){
		switch(size){
			case ESSENCE:
				if(essenceItem!=null) throw new IllegalStateException("Duplicated item set for "+size);
				essenceItem = item;
				break;
			case GREATER_ESSENCE:
				if(greaterEssenceItem!=null) throw new IllegalStateException("Duplicated item set for "+size);
				greaterEssenceItem = item;
				break;
			case EXQUISITE_ESSENCE:
				if(exquisiteEssenceItem!=null) throw new IllegalStateException("Duplicated item set for "+size);
				exquisiteEssenceItem = item;
				break;
			default:
				throw new IllegalStateException("Unreachable");
		}
	}

	public static EssenceType of(String id){
		return valueOf(id.toUpperCase(Locale.ROOT));
	}
}
