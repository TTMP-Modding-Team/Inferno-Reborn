package ttmp.infernoreborn.util;

import net.minecraft.item.Item;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;

import static ttmp.infernoreborn.contents.ModItems.EARTH_ESSENCE_CRYSTAL;
import static ttmp.infernoreborn.contents.ModItems.EARTH_ESSENCE_SHARD;
import static ttmp.infernoreborn.contents.ModItems.FROST_ESSENCE_CRYSTAL;
import static ttmp.infernoreborn.contents.ModItems.FROST_ESSENCE_SHARD;
import static ttmp.infernoreborn.contents.ModItems.GREATER_EARTH_ESSENCE_CRYSTAL;
import static ttmp.infernoreborn.contents.ModItems.GREATER_FROST_ESSENCE_CRYSTAL;
import static ttmp.infernoreborn.contents.ModItems.GREATER_HEART_ESSENCE_CRYSTAL;
import static ttmp.infernoreborn.contents.ModItems.GREATER_METAL_ESSENCE_CRYSTAL;
import static ttmp.infernoreborn.contents.ModItems.HEART_ESSENCE_CRYSTAL;
import static ttmp.infernoreborn.contents.ModItems.HEART_ESSENCE_SHARD;
import static ttmp.infernoreborn.contents.ModItems.METAL_ESSENCE_CRYSTAL;
import static ttmp.infernoreborn.contents.ModItems.METAL_ESSENCE_SHARD;

public enum EssenceType{
	HEART(HEART_ESSENCE_SHARD, HEART_ESSENCE_CRYSTAL, GREATER_HEART_ESSENCE_CRYSTAL),
	METAL(METAL_ESSENCE_SHARD, METAL_ESSENCE_CRYSTAL, GREATER_METAL_ESSENCE_CRYSTAL),
	FROST(FROST_ESSENCE_SHARD, FROST_ESSENCE_CRYSTAL, GREATER_FROST_ESSENCE_CRYSTAL),
	EARTH(EARTH_ESSENCE_SHARD, EARTH_ESSENCE_CRYSTAL, GREATER_EARTH_ESSENCE_CRYSTAL);

	private final Supplier<Item> shardItemSupplier;
	private final Supplier<Item> normalItemSupplier;
	private final Supplier<Item> crystalItemSupplier;

	EssenceType(Supplier<Item> shardItemSupplier, Supplier<Item> normalItemSupplier, Supplier<Item> crystalItemSupplier){
		this.shardItemSupplier = Objects.requireNonNull(shardItemSupplier);
		this.normalItemSupplier = Objects.requireNonNull(normalItemSupplier);
		this.crystalItemSupplier = Objects.requireNonNull(crystalItemSupplier);
	}

	public Item getShardItem(){
		return shardItemSupplier.get();
	}
	public Item getNormalItem(){
		return normalItemSupplier.get();
	}
	public Item getCrystalItem(){
		return crystalItemSupplier.get();
	}

	public ITextComponent getName(){
		return new TranslationTextComponent("infernoreborn.essence."+name().toLowerCase(Locale.ROOT));
	}
}
