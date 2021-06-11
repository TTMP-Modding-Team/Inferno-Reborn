package ttmp.infernoreborn.util;

import net.minecraft.item.Item;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;

import static ttmp.infernoreborn.contents.ModItems.*;

public enum EssenceType{
	BLOOD(BLOOD_ESSENCE_SHARD, BLOOD_ESSENCE_CRYSTAL, GREATER_BLOOD_ESSENCE_CRYSTAL),
	METAL(METAL_ESSENCE_SHARD, METAL_ESSENCE_CRYSTAL, GREATER_METAL_ESSENCE_CRYSTAL),
	FROST(FROST_ESSENCE_SHARD, FROST_ESSENCE_CRYSTAL, GREATER_FROST_ESSENCE_CRYSTAL),
	EARTH(EARTH_ESSENCE_SHARD, EARTH_ESSENCE_CRYSTAL, GREATER_EARTH_ESSENCE_CRYSTAL);

	public final String id = name().toLowerCase(Locale.ROOT);

	private final Supplier<Item> shardItemSupplier;
	private final Supplier<Item> crystalItemSupplier;
	private final Supplier<Item> greaterShardItemSupplier;

	EssenceType(Supplier<Item> shardItemSupplier, Supplier<Item> crystalItemSupplier, Supplier<Item> greaterShardItemSupplier){
		this.shardItemSupplier = Objects.requireNonNull(shardItemSupplier);
		this.crystalItemSupplier = Objects.requireNonNull(crystalItemSupplier);
		this.greaterShardItemSupplier = Objects.requireNonNull(greaterShardItemSupplier);
	}

	public Item getShardItem(){
		return shardItemSupplier.get();
	}
	public Item getCrystalItem(){
		return crystalItemSupplier.get();
	}
	public Item getGreaterShardItem(){
		return greaterShardItemSupplier.get();
	}

	public ITextComponent getName(){
		return new TranslationTextComponent("infernoreborn.essence."+id);
	}

	public Item getItem(EssenceSize size){
		switch(size){
			case SHARD:
				return shardItemSupplier.get();
			case CRYSTAL:
				return crystalItemSupplier.get();
			case GREATER_CRYSTAL:
				return greaterShardItemSupplier.get();
			default:
				throw new IllegalStateException("Unreachable");
		}
	}
}
