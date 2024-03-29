package ttmp.infernoreborn.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collector;

// TODO dont change the classname, this is perfect
public final class StupidUtils{
	private StupidUtils(){}

	private static final Collector<ResourceLocation, ListNBT, ListNBT> idToNbtCollector = new Collector<ResourceLocation, ListNBT, ListNBT>(){
		final Supplier<ListNBT> supplier = ListNBT::new;
		final BiConsumer<ListNBT, ResourceLocation> accumulator = (l, r) -> l.add(StringNBT.valueOf(r.toString()));
		final BinaryOperator<ListNBT> combiner = (l1, l2) -> l1;
		final Function<ListNBT, ListNBT> finisher = it -> it;
		final Set<Characteristics> characteristics = Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));

		@Override public Supplier<ListNBT> supplier(){
			return supplier;
		}
		@Override public BiConsumer<ListNBT, ResourceLocation> accumulator(){
			return accumulator;
		}
		@Override public BinaryOperator<ListNBT> combiner(){
			return combiner;
		}
		@Override public Function<ListNBT, ListNBT> finisher(){
			return finisher;
		}
		@Override public Set<Characteristics> characteristics(){
			return characteristics;
		}
	};

	public static <T extends IForgeRegistryEntry<T>> ListNBT writeToNbt(Collection<T> collection, IForgeRegistry<T> registry){
		return collection.stream()
				.map(registry::getKey)
				.filter(Objects::nonNull)
				.collect(idToNbtCollector);
	}

	public static <T extends IForgeRegistryEntry<T>> ListNBT writeToNbt(T[] collection, IForgeRegistry<T> registry){
		return Arrays.stream(collection)
				.map(registry::getKey)
				.filter(Objects::nonNull)
				.collect(idToNbtCollector);
	}

	public static <T extends IForgeRegistryEntry<T>> void read(ListNBT nbt, IForgeRegistry<T> registry, Consumer<T> forEach){
		if(!nbt.isEmpty()) nbt.stream()
				.map(INBT::getAsString)
				.map(ResourceLocation::tryParse)
				.filter(Objects::nonNull)
				.map(registry::getValue)
				.filter(Objects::nonNull)
				.forEach(forEach);
	}

	public static <T extends IForgeRegistryEntry<T>> T[] readToArray(ListNBT nbt, IForgeRegistry<T> registry, IntFunction<T[]> arrayGenerator){
		if(nbt.isEmpty()) return arrayGenerator.apply(0);
		return nbt.stream()
				.map(INBT::getAsString)
				.map(ResourceLocation::tryParse)
				.filter(Objects::nonNull)
				.map(registry::getValue)
				.filter(Objects::nonNull)
				.toArray(arrayGenerator);
	}

	public static int getInt(ItemStack stack, String name){
		CompoundNBT tag = stack.getTag();
		return tag!=null ? tag.getInt(name) : 0;
	}
	public static void set(ItemStack stack, String name, int i){
		if(i==0){
			CompoundNBT tag = stack.getTag();
			if(tag!=null) tag.remove(name);
		}else stack.getOrCreateTag().putInt(name, i);
	}

	public static float getFloat(ItemStack stack, String name){
		CompoundNBT tag = stack.getTag();
		return tag!=null ? tag.getFloat(name) : 0;
	}
	public static void set(ItemStack stack, String name, float f){
		if(f==0){
			CompoundNBT tag = stack.getTag();
			if(tag!=null) tag.remove(name);
		}else stack.getOrCreateTag().putFloat(name, f);
	}

	public static boolean getBool(ItemStack stack, String name){
		CompoundNBT tag = stack.getTag();
		return tag!=null&&tag.getBoolean(name);
	}
	public static void set(ItemStack stack, String name, boolean b){
		CompoundNBT tag = stack.getTag();
		if(b){
			if(tag==null) stack.setTag(tag = new CompoundNBT());
			tag.putBoolean(name, true);
		}else if(tag!=null){
			tag.remove(name);
			if(tag.isEmpty()) stack.setTag(null);
		}
	}
}
