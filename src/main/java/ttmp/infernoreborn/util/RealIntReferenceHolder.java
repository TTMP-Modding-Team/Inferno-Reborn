package ttmp.infernoreborn.util;

import net.minecraft.util.IntReferenceHolder;

import java.util.function.Consumer;

public final class RealIntReferenceHolder{
	private final IntReferenceHolder i1 = IntReferenceHolder.standalone(), i2 = IntReferenceHolder.standalone();

	public int get(){
		return i1.get()<<16&0xFFFF0000|i2.get()&0xFFFF;
	}

	public void set(int value){
		i1.set(value>>16&0xFFFF);
		i2.set(value&0xFFFF);
	}

	public void register(Consumer<IntReferenceHolder> consumer){
		consumer.accept(i1);
		consumer.accept(i2);
	}
}
