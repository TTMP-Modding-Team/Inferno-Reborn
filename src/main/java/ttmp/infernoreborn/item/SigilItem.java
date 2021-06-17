package ttmp.infernoreborn.item;

import net.minecraft.item.Item;
import ttmp.infernoreborn.sigil.Sigil;

import java.util.Objects;
import java.util.function.Supplier;

public class SigilItem extends Item{
	private final Supplier<Sigil> sigil;

	public SigilItem(Supplier<Sigil> sigil, Properties properties){
		super(properties);
		this.sigil = Objects.requireNonNull(sigil);
	}

	public Sigil getSigil(){
		return sigil.get();
	}
}
