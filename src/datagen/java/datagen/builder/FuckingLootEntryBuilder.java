package datagen.builder;

import net.minecraft.loot.AlternativesLootEntry;
import net.minecraft.loot.LootEntry;
import ttmp.infernoreborn.contents.loot.FuckingLootEntry;

import java.util.Arrays;

public class FuckingLootEntryBuilder extends LootEntry.Builder<FuckingLootEntryBuilder>{
	private final LootEntry[] children;

	public FuckingLootEntryBuilder(LootEntry.Builder<?>... children){
		this.children = Arrays.stream(children).map(c -> c.build()).toArray(LootEntry[]::new);
	}

	@Override protected FuckingLootEntryBuilder getThis(){
		return this;
	}
	@Override public AlternativesLootEntry.Builder otherwise(LootEntry.Builder<?> child){
		return new AlternativesLootEntry.Builder(this, child);
	}
	@Override public LootEntry build(){
		return new FuckingLootEntry(children, getConditions());
	}
}
